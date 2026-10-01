# Migrator local runbook

How to run and verify a full SAN → AAP migration locally, using a real dataset pulled from a
GitHub Actions `e2e_test` run instead of whatever's in your local dev database. This mirrors what
the `migrator_test` job does in `pipeline_pr.yml`, but lets you inspect every step and iterate on
a fix without waiting on CI.

## Prerequisites

- Docker, running locally.
- `psql`, `jq`, `curl`, `zip`/`unzip` on your PATH (standard on macOS/Linux dev machines).
- A set of per-shard database dumps to migrate. Download the `db_dump_1` .. `db_dump_10`
  artifacts from a completed `e2e_test` GitHub Actions run (Summary page → Artifacts) into one
  local directory, e.g. `db_dumps/`. Each is a zip containing one `db_dump.dump` file - you don't
  need to unzip them yourself, `migrator-combine-db-dumps` does that.

## Important: always start from a clean stack

`make migrator-up` passes `--no-recreate`, so any container left running from a
previous session gets reused as-is - even if its image tag or the schema it depends on has since
changed underneath it. Run `make migrator-down` before starting, every time.

## Step-by-step

1. **Tear down and bring up the stack**:

   ```
   make migrator-down
   make migrator-up
   ```

2. **Restore and remap the shard dumps into postgres:**

   ```
   make migrator-combine-db-dumps DUMPS_DIR=db_dumps
   ```

   Each shard in `db_dumps/` was produced by an *independent* postgres instance (one per parallel
   e2e-test matrix job), so only `uuid` columns are globally unique - every integer
   SERIAL/IDENTITY id restarts at 1 in every shard. This restores shard 1's schema (`--clean
   --if-exists`, safe whether or not `san-api` has already created its own schema), then for each
   shard: restores its data, then shifts that shard's integer primary/foreign key columns
   (discovered generically from the schema's own key constraints - this doesn't hardcode table
   names from schemas we don't own the source of, e.g. `coordinator`) into their own
   non-overlapping ID block, before moving on to the next shard. Resyncs sequences at the end so
   later inserts (e.g. from the migrator itself) don't collide with the remapped data.

3. **Fetch pre-migration Coordinator responses**, you need to switch to the legacy SAN versions
of the Coordinator and Handover service:

   ```
   make use-legacy-san
   make migrator-fetch-coordinator-assessments \
     OUTPUT_DIR=/tmp/coordinator-pre ENTITY_TYPE=ASSESSMENT
   ```

   This queries `coordinator.oasys_associations` directly (not by joining through a source table -
   the same `oasysPk` can have several associated `entity_uuid`s of different types, e.g. a `PLAN`
   association alongside an `ASSESSMENT` one, so `entity_type` is the actual disambiguator), gets
   an access token as the `hmpps-assess-risks-and-needs-oastub-ui` client (the Fetch endpoint is
   the one real OASys calls in production, so it needs that client's roles - san-api's own
   outbound client gets a 403 here), and calls `GET /oasys/{oasysPk}` on the Coordinator for each
   association. Writes one `<uuid>.json` response per assessment plus a `manifest.csv`
   (`uuid,oasys_pk`) used later to pair pre/post responses up.

4. **Swap to the AAP version of Coordinator/Handover**:

   ```
   make use-aap-san
   ```

5. **Run the migrator:**

   ```
   make migrator-run
   ```

   Concurrent migrations (concurrency limit 25) can race to create the same OASys user's
   `user_details` row; the loser gets one automatic retry (`uq_user_id_and_type` collisions only -
   see `MigrationRunner.migrateWithRetry`). Everything else that fails is logged and skipped; the
   migrator keeps going rather than aborting the whole run. Check the summary at the end of the
   output:

   ```
   Failed to migrate <N> assessments
   ```

   and grep the full output for `Failed to migrate assessment` for per-assessment detail.

6. **Fetch post-migration Coordinator responses**:

   ```
   make migrator-fetch-coordinator-assessments \
     OUTPUT_DIR=/tmp/coordinator-post ENTITY_TYPE=AAP_SAN
   ```

7. **Compare the two sets of responses:**

   ```
   make compare-coordinator-responses \
     OLD_DIR=/tmp/coordinator-pre NEW_DIR=/tmp/coordinator-post REPORT_DIR=/tmp/coordinator-diff
   ```

   Pairs old/new responses up by `oasysPk` (the assessment UUID differs between the two, since one
   is the original SAN uuid and the other is the migrated AAP uuid) and diffs only the
   `sanOasysEquivalent` field of each (that's the field that matters for OASys). Writes
   `<report-dir>/summary.tsv` (`oasys_pk`, `old_uuid`, `new_uuid`, `MATCH`/`MISMATCH`/`MISSING`)
   and one unified diff per mismatch under `<report-dir>/diffs/`.

## Interpreting the results

- **`migrator-run` failures**: grep the log for `Failed to migrate assessment <id>: <message>`.
  To get the UUIDs behind a failed numeric id (the ids are the *remapped* ones from step 2, only
  meaningful within this local run), look them up directly:

  ```
  docker exec hmpps-assess-risks-and-needs-postgres-1 psql -U root -d postgres -tAc \
    "SELECT uuid FROM strengthsbasedneedsapi.assessments WHERE id = <id>;"
  ```

- **`compare-coordinator-responses` MISSING rows**: the assessment failed to migrate (no `AAP_SAN`
  association was ever created for it - migration is atomic per-assessment, so a failure never
  leaves a partial one behind).

- **`compare-coordinator-responses` MISMATCH rows**: read `<report-dir>/diffs/<oasys_pk>.diff`.
  Look for whether the same field/value pattern repeats across many diffs (a systematic mapping
  bug - e.g. we found one bug where `sanAssessmentData`/`sanOasysEquivalent` came back entirely
  empty for every migrated assessment, and later a narrower one where two specific fields flipped
  from `null` to `"0"` on every single assessment) versus differences scattered across many
  unrelated fields (more likely per-assessment data issues).

## Known, expected noise (not migrator bugs)

- `duplicate key value violates unique constraint "uq_user_id_and_type"` failures that persist
  even after the retry: every e2e shard's Cypress tests log in as the same small set of fixed test
  OASys users, so combining 10 shards' worth of fixture data creates artificial user collisions
  that would never happen migrating a single real environment once.
- Any assessment whose SAN uuid doesn't have a `coordinator.oasys_associations` row at all (no
  `ASSESSMENT`-type association) was never linked to an OASys record in the first place and won't
  appear in step 3's output - this is normal for some e2e test fixtures.
