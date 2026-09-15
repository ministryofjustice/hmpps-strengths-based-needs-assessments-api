#!/usr/bin/env bash
# Fetches, from the Coordinator, the current state of every assessment found in the given
# source table, and saves each response as a file named after the assessment's UUID.
# Also writes manifest.csv (uuid,oasys_pk) so responses from different assessment UUID
# spaces (e.g. pre- and post-migration) can later be paired up by oasysPk.
#
# Each assessment UUID is looked up in the coordinator schema's oasys_associations table
# (entity_uuid -> oasys_assessment_pk) to find the oasysPk used by the Fetch endpoint.
#
# Usage: fetch_coordinator_assessments.sh <output-dir> <source-schema> <source-table>

set -eu

OUTPUT_DIR="${1:?Usage: fetch_coordinator_assessments.sh <output-dir> <source-schema> <source-table>}"
SOURCE_SCHEMA="${2:?Usage: fetch_coordinator_assessments.sh <output-dir> <source-schema> <source-table>}"
SOURCE_TABLE="${3:?Usage: fetch_coordinator_assessments.sh <output-dir> <source-schema> <source-table>}"

DOCKER_CONNECTION_STRING="postgres://root:dev@localhost:5432/postgres"
HMPPS_AUTH_URL="http://localhost:9090/auth"
COORDINATOR_API_URL="http://localhost:8070"
CLIENT_ID="sentence-plan-api-client"
CLIENT_SECRET="sentence-plan-api-client"

mkdir -p "${OUTPUT_DIR}"
: > "${OUTPUT_DIR}/manifest.csv"

echo "Fetching an access token..."

ACCESS_TOKEN=$(curl --fail --silent --show-error \
  --location "${HMPPS_AUTH_URL}/oauth/token" \
  --user "${CLIENT_ID}:${CLIENT_SECRET}" \
  --data-urlencode 'grant_type=client_credentials' \
  | jq -r '.access_token')

echo "Looking up oasysPks for assessments in \"${SOURCE_SCHEMA}\".\"${SOURCE_TABLE}\"..."

ASSOCIATIONS=$(psql "${DOCKER_CONNECTION_STRING}" -tA -F'|' -c "
  SELECT a.uuid, oa.oasys_assessment_pk
  FROM \"${SOURCE_SCHEMA}\".\"${SOURCE_TABLE}\" a
  JOIN coordinator.oasys_associations oa ON oa.entity_uuid = a.uuid;
")

TOTAL=$(echo "${ASSOCIATIONS}" | grep -c . || true)
COUNT=0
FAILURES=0

echo "Found ${TOTAL} assessment(s) with a Coordinator association. Fetching..."

while IFS='|' read -r uuid oasys_pk; do
  [ -z "${uuid}" ] && continue

  COUNT=$((COUNT + 1))
  echo "[${COUNT}/${TOTAL}] Fetching assessment ${uuid} (oasysPk ${oasys_pk})"

  if ! curl --fail --silent --show-error \
    --location "${COORDINATOR_API_URL}/oasys/${oasys_pk}" \
    --header "Authorization: Bearer ${ACCESS_TOKEN}" \
    --output "${OUTPUT_DIR}/${uuid}.json"; then
    echo "Failed to fetch assessment ${uuid} (oasysPk ${oasys_pk})" >&2
    rm -f "${OUTPUT_DIR}/${uuid}.json"
    FAILURES=$((FAILURES + 1))
  else
    echo "${uuid},${oasys_pk}" >> "${OUTPUT_DIR}/manifest.csv"
  fi
done <<< "${ASSOCIATIONS}"

echo "Done. Fetched $((COUNT - FAILURES))/${TOTAL} assessment(s), ${FAILURES} failure(s)."

if [ "${FAILURES}" -gt 0 ]; then
  exit 1
fi
