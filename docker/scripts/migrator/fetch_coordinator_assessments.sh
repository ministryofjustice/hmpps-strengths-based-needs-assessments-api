#!/usr/bin/env bash
# Fetches, from the Coordinator, the current state of every association of a given
# entity_type (e.g. ASSESSMENT pre-migration, AAP_SAN post-migration), and saves each
# response as a file named after the entity's UUID. Also writes manifest.csv
# (uuid,oasys_pk) so responses from different entity_type/UUID spaces (e.g. pre- and
# post-migration) can later be paired up by oasysPk.
#
# entity_type is the actual disambiguator in coordinator.oasys_associations - the same
# oasysPk can have several associated entity_uuids of different types (e.g. a PLAN
# association alongside an ASSESSMENT one), so this queries associations directly
# rather than joining through one specific source table.
#
# Usage: fetch_coordinator_assessments.sh <output-dir> <entity-type>

set -eu

OUTPUT_DIR="${1:?Usage: fetch_coordinator_assessments.sh <output-dir> <entity-type>}"
ENTITY_TYPE="${2:?Usage: fetch_coordinator_assessments.sh <output-dir> <entity-type>}"

DOCKER_CONNECTION_STRING="postgres://root:dev@localhost:5432/postgres"
HMPPS_AUTH_URL="http://localhost:9090/auth"
COORDINATOR_API_URL="http://localhost:8070"
# The Fetch endpoint is the one real OASys calls in production, so it needs the oastub
# client's roles (ROLE_STRENGTHS_AND_NEEDS_OASYS etc.) - san-api's own outbound client
# (sentence-plan-api-client) gets a 403 here despite authenticating fine.
CLIENT_ID="hmpps-assess-risks-and-needs-oastub-ui"
CLIENT_SECRET="clientsecret"

mkdir -p "${OUTPUT_DIR}"
: > "${OUTPUT_DIR}/manifest.csv"

echo "Fetching an access token..."

ACCESS_TOKEN=$(curl --fail --silent --show-error \
  --location "${HMPPS_AUTH_URL}/oauth/token" \
  --user "${CLIENT_ID}:${CLIENT_SECRET}" \
  --data-urlencode 'grant_type=client_credentials' \
  | jq -r '.access_token')

echo "Looking up oasysPks for ${ENTITY_TYPE} associations..."

ASSOCIATIONS=$(psql "${DOCKER_CONNECTION_STRING}" -tA -F'|' -c "
  SELECT entity_uuid, oasys_assessment_pk
  FROM coordinator.oasys_associations
  WHERE entity_type = '${ENTITY_TYPE}'
    AND NOT deleted;
")

TOTAL=$(echo "${ASSOCIATIONS}" | grep -c . || true)
COUNT=0
FAILURES=0

echo "Found ${TOTAL} ${ENTITY_TYPE} association(s). Fetching..."

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
