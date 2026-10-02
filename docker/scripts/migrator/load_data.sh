#!/usr/bin/env bash

set -eu

POD_NAME="port-forward-pod"
POD_IMAGE="ministryofjustice/port-forward"

COORDINATOR_NAMESPACE="hmpps-assess-risks-and-needs-integrations-dev"
COORDINATOR_SECRET_NAME="hmpps-assess-risks-and-needs-integrations-rds-instance"
COORDINATOR_LOCAL_PORT=5436

SAN_NAMESPACE="hmpps-strengths-based-needs-assessments-dev"
SAN_SECRET_NAME="hmpps-strengths-based-needs-assessments-rds-instance"
SAN_LOCAL_PORT=5437

AAP_NAMESPACE="hmpps-arns-assessment-platform-dev"
AAP_SECRET_NAME="rds-aurora-instance-output"
AAP_LOCAL_PORT=5438

DOCKER_CONNECTION_STRING="postgres://root:dev@localhost:5432/postgres"

echo "Fetching secrets..."
COORDINATOR_INSTANCE_ADDRESS=$(kubectl -n "${COORDINATOR_NAMESPACE}" get secret "${COORDINATOR_SECRET_NAME}" -o json | jq -r '.data.rds_instance_address' | base64 --decode)
COORDINATOR_DATABASE_USERNAME=$(kubectl -n "${COORDINATOR_NAMESPACE}" get secret "${COORDINATOR_SECRET_NAME}" -o json | jq -r '.data.database_username' | base64 --decode)
COORDINATOR_DATABASE_PASSWORD=$(kubectl -n "${COORDINATOR_NAMESPACE}" get secret "${COORDINATOR_SECRET_NAME}" -o json | jq -r '.data.database_password' | base64 --decode)
COORDINATOR_DATABASE_NAME=$(kubectl -n "${COORDINATOR_NAMESPACE}" get secret "${COORDINATOR_SECRET_NAME}" -o json | jq -r '.data.database_name' | base64 --decode)
COORDINATOR_CONNECTION_STRING="postgres://${COORDINATOR_DATABASE_USERNAME}:${COORDINATOR_DATABASE_PASSWORD}@localhost:${COORDINATOR_LOCAL_PORT}/${COORDINATOR_DATABASE_NAME}"

SAN_INSTANCE_ADDRESS=$(kubectl -n "${SAN_NAMESPACE}" get secret "${SAN_SECRET_NAME}" -o json | jq -r '.data.rds_instance_address' | base64 --decode)
SAN_DATABASE_USERNAME=$(kubectl -n "${SAN_NAMESPACE}" get secret "${SAN_SECRET_NAME}" -o json | jq -r '.data.database_username' | base64 --decode)
SAN_DATABASE_PASSWORD=$(kubectl -n "${SAN_NAMESPACE}" get secret "${SAN_SECRET_NAME}" -o json | jq -r '.data.database_password' | base64 --decode)
SAN_DATABASE_NAME=$(kubectl -n "${SAN_NAMESPACE}" get secret "${SAN_SECRET_NAME}" -o json | jq -r '.data.database_name' | base64 --decode)
SAN_CONNECTION_STRING="postgres://${SAN_DATABASE_USERNAME}:${SAN_DATABASE_PASSWORD}@localhost:${SAN_LOCAL_PORT}/${SAN_DATABASE_NAME}"

AAP_INSTANCE_ADDRESS=$(kubectl -n "${AAP_NAMESPACE}" get secret "${AAP_SECRET_NAME}" -o json | jq -r '.data.rds_cluster_endpoint' | base64 --decode)
AAP_DATABASE_USERNAME=$(kubectl -n "${AAP_NAMESPACE}" get secret "${AAP_SECRET_NAME}" -o json | jq -r '.data.database_username' | base64 --decode)
AAP_DATABASE_PASSWORD=$(kubectl -n "${AAP_NAMESPACE}" get secret "${AAP_SECRET_NAME}" -o json | jq -r '.data.database_password' | base64 --decode)
AAP_DATABASE_NAME=$(kubectl -n "${AAP_NAMESPACE}" get secret "${AAP_SECRET_NAME}" -o json | jq -r '.data.database_name' | base64 --decode)
AAP_CONNECTION_STRING="postgres://${AAP_DATABASE_USERNAME}:${AAP_DATABASE_PASSWORD}@localhost:${AAP_LOCAL_PORT}/${AAP_DATABASE_NAME}"

echo "Starting port-forwards..."

kubectl -n "${COORDINATOR_NAMESPACE}" port-forward pod/${POD_NAME} ${COORDINATOR_LOCAL_PORT}:5432 >/tmp/portforward-coordinator.log 2>&1 &
COORDINATOR_PF_PID=$!

kubectl -n "${SAN_NAMESPACE}" port-forward pod/${POD_NAME} ${SAN_LOCAL_PORT}:5432 >/tmp/portforward-san.log 2>&1 &
SAN_PF_PID=$!

kubectl -n "${AAP_NAMESPACE}" port-forward pod/${POD_NAME} ${AAP_LOCAL_PORT}:5432 >/tmp/portforward-aap.log 2>&1 &
AAP_PF_PID=$!

# Kill port-forwards on exit
trap "kill $COORDINATOR_PF_PID" EXIT
trap "kill $SAN_PF_PID" EXIT
trap "kill $AAP_PF_PID" EXIT

echo "Waiting for port-forwards to be ready..."

while ! nc -z localhost ${COORDINATOR_LOCAL_PORT}; do sleep 0.2; done
while ! nc -z localhost ${SAN_LOCAL_PORT}; do sleep 0.2; done
while ! nc -z localhost ${AAP_LOCAL_PORT}; do sleep 0.2; done

echo "Streaming AAP data into docker database..."

pg_dump \
  --schema="assessment-platform" \
  --clean \
  --no-owner \
  --no-privileges \
  ${AAP_CONNECTION_STRING} \
  | psql ${DOCKER_CONNECTION_STRING}

echo "Streaming Coordinator data into docker database..."

pg_dump \
  --schema="coordinator" \
  --clean \
  --no-owner \
  --no-privileges \
  ${COORDINATOR_CONNECTION_STRING} \
  | psql ${DOCKER_CONNECTION_STRING}

echo "Streaming SAN data into docker database..."

pg_dump \
  --schema="strengthsbasedneedsapi" \
  --clean \
  --no-owner \
  --no-privileges \
  ${SAN_CONNECTION_STRING} \
  | psql ${DOCKER_CONNECTION_STRING}

echo "Done. Happy migrating."
