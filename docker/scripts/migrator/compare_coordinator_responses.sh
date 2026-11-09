#!/usr/bin/env bash
# Compares the "sanOasysEquivalent" field of two sets of Coordinator Fetch responses
# (e.g. pre- and post-migration), each a directory containing a manifest.csv
# (uuid,oasys_pk) plus one <uuid>.json response per assessment. Responses are paired
# up by oasysPk, since the assessment UUID differs between the two sets.
#
# Writes a summary.tsv (oasys_pk, old_uuid, new_uuid, result) and a unified diff per
# mismatch under <report-dir>/diffs/ to <report-dir>.
#
# Usage: compare_coordinator_responses.sh <old-dir> <new-dir> <report-dir>

set -eu

OLD_DIR="${1:?Usage: compare_coordinator_responses.sh <old-dir> <new-dir> <report-dir>}"
NEW_DIR="${2:?Usage: compare_coordinator_responses.sh <old-dir> <new-dir> <report-dir>}"
REPORT_DIR="${3:?Usage: compare_coordinator_responses.sh <old-dir> <new-dir> <report-dir>}"

OLD_MANIFEST="${OLD_DIR}/manifest.csv"
NEW_MANIFEST="${NEW_DIR}/manifest.csv"

[ -f "${OLD_MANIFEST}" ] || { echo "Missing ${OLD_MANIFEST}" >&2; exit 1; }
[ -f "${NEW_MANIFEST}" ] || { echo "Missing ${NEW_MANIFEST}" >&2; exit 1; }

mkdir -p "${REPORT_DIR}/diffs"

SUMMARY="${REPORT_DIR}/summary.tsv"
printf 'oasys_pk\told_uuid\tnew_uuid\tresult\n' > "${SUMMARY}"

MATCHED=0
MISMATCHED=0
MISSING=0

while IFS=',' read -r old_uuid oasys_pk; do
  [ -z "${oasys_pk}" ] && continue

  new_uuid=$(awk -F',' -v pk="${oasys_pk}" '$2 == pk {print $1; exit}' "${NEW_MANIFEST}")

  if [ -z "${new_uuid}" ]; then
    echo "No migrated assessment found for oasysPk ${oasys_pk} (was ${old_uuid})" >&2
    printf '%s\t%s\t-\tMISSING\n' "${oasys_pk}" "${old_uuid}" >> "${SUMMARY}"
    MISSING=$((MISSING + 1))
    continue
  fi

  OLD_JSON="${OLD_DIR}/${old_uuid}.json"
  NEW_JSON="${NEW_DIR}/${new_uuid}.json"

  if diff -q \
    <(jq -S '.sanOasysEquivalent // {}' "${OLD_JSON}") \
    <(jq -S '.sanOasysEquivalent // {}' "${NEW_JSON}") \
    > /dev/null; then
    printf '%s\t%s\t%s\tMATCH\n' "${oasys_pk}" "${old_uuid}" "${new_uuid}" >> "${SUMMARY}"
    MATCHED=$((MATCHED + 1))
  else
    diff -u \
      <(jq -S '.sanOasysEquivalent // {}' "${OLD_JSON}") \
      <(jq -S '.sanOasysEquivalent // {}' "${NEW_JSON}") \
      > "${REPORT_DIR}/diffs/${oasys_pk}.diff" || true
    printf '%s\t%s\t%s\tMISMATCH\n' "${oasys_pk}" "${old_uuid}" "${new_uuid}" >> "${SUMMARY}"
    MISMATCHED=$((MISMATCHED + 1))
  fi
done < "${OLD_MANIFEST}"

echo "Compared $((MATCHED + MISMATCHED)) assessment(s): ${MATCHED} match, ${MISMATCHED} mismatch, ${MISSING} missing."

if [ "${MISMATCHED}" -gt 0 ] || [ "${MISSING}" -gt 0 ]; then
  exit 1
fi
