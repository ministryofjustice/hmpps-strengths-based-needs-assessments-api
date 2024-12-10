SHELL = '/bin/bash'
DEV_COMPOSE_FILES = -f docker-compose.yml -f docker-compose.dev.yml
TEST_COMPOSE_FILES = -f docker-compose.yml -f docker-compose.test.yml
LOCAL_COMPOSE_FILES = -f docker-compose.yml -f docker-compose.local.yml
PROJECT_NAME = hmpps-assess-risks-and-needs

export COMPOSE_PROJECT_NAME=${PROJECT_NAME}

default: help

help: ## The help text you're reading.
	@grep --no-filename -E '^[0-9a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

up: ## Starts/restarts the API in a production container.
	docker compose ${LOCAL_COMPOSE_FILES} down san-api
	docker compose ${LOCAL_COMPOSE_FILES} up san-api --wait --no-recreate

down: ## Stops and removes all containers in the project.
	docker compose ${DEV_COMPOSE_FILES} down
	docker compose ${LOCAL_COMPOSE_FILES} down

build-api: ## Builds a production image of the API.
	docker compose build san-api

dev-up: ## Starts/restarts the API in a development container. A remote debugger can be attached on port 5005.
	docker compose down san-api
	docker compose ${DEV_COMPOSE_FILES} up --wait --no-recreate san-api

dev-build: ## Builds a development image of the API.
	docker compose ${DEV_COMPOSE_FILES} build san-api

dev-down: ## Stops and removes the API container.
	docker compose down san-api

dev-api-token: ## Generates a JWT for authenticating with the local API.
	docker compose ${DEV_COMPOSE_FILES} exec san-api \
		curl --location 'http://hmpps-auth:9090/auth/oauth/token' \
  	--header 'authorization: Basic aG1wcHMtc3RyZW5ndGhzLWFuZC1uZWVkcy11aS1jbGllbnQ6Y2xpZW50c2VjcmV0' \
  	--header 'Content-Type: application/x-www-form-urlencoded' \
  	--data-urlencode 'grant_type=client_credentials' \
  	| jq -r '.access_token' \
  	| xargs printf "\nToken:\n%s\n"

rebuild: ## Re-builds and live-reloads the API.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle compileKotlin --parallel --build-cache --configuration-cache

watch: ## Watches for file changes and live-reloads the API. To be used in conjunction with dev-up e.g. "make dev-up watch"
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle compileKotlin --continuous --parallel --build-cache --configuration-cache

test: ## Runs all the test suites.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle test --parallel

test-coverage: ## Runs the test suite and outputs a code coverage report.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle koverHtmlReport --parallel

test-unit: ## Runs the unit test suite.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle unitTests --parallel

test-integration: ## Runs the integration test suite.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle integrationTests --parallel

lint: ## Runs the Kotlin linter.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle ktlintCheck --parallel

lint-fix: ## Runs the Kotlin linter and auto-fixes.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle ktlintFormat --parallel

lint-baseline: ## Generate a baseline file, ignoring all existing code smells.
	docker compose ${DEV_COMPOSE_FILES} exec san-api gradle --parallel

test-up: ## Stands up a test environment.
	docker compose pull --policy missing
	docker compose ${TEST_COMPOSE_FILES} -p ${PROJECT_NAME}-test up --wait --force-recreate

test-down: ## Stops and removes all of the test containers.
	docker compose ${TEST_COMPOSE_FILES} -p ${PROJECT_NAME}-test down

clean: ## Stops and removes all project containers. Deletes local build/cache directories.
	docker compose down
	rm -rf .gradle build

update: ## Downloads the latest versions of containers.
	docker compose pull

save-logs: ## Saves docker container logs in a directory defined by OUTPUT_LOGS_DIR=
	mkdir -p ${OUTPUT_LOGS_DIR}
	docker logs ${PROJECT_NAME}-san-api-1 > ${OUTPUT_LOGS_DIR}/san-api.log
	docker logs ${PROJECT_NAME}-san-ui-1 > ${OUTPUT_LOGS_DIR}/san-ui.log
	docker logs ${PROJECT_NAME}-arns-handover-1 > ${OUTPUT_LOGS_DIR}/arns-handover.log
	docker logs ${PROJECT_NAME}-coordinator-api-1 > ${OUTPUT_LOGS_DIR}/coordinator-api.log
	docker logs ${PROJECT_NAME}-hmpps-auth-1 > ${OUTPUT_LOGS_DIR}/hmpps-auth.log

DB_PORT_FORWARD_PORT=5434
db-port-forward:
	kubectl delete pod --ignore-not-found=true port-forward-pod
	INSTANCE_ADDRESS=$$(kubectl get secret hmpps-strengths-based-needs-assessments-rds-instance -o json | jq -r '.data.rds_instance_address' | base64 --decode) \
	; kubectl run port-forward-pod --image=ministryofjustice/port-forward --port=5432 --env="REMOTE_HOST=$$INSTANCE_ADDRESS" --env="LOCAL_PORT=5432" --env="REMOTE_PORT=5432"
	kubectl wait --for=jsonpath='{.status.phase}'=Running pod/port-forward-pod
	kubectl port-forward port-forward-pod ${DB_PORT_FORWARD_PORT}:5432

db-connect:
	DATABASE_USERNAME=$$(kubectl get secret hmpps-strengths-based-needs-assessments-rds-instance -o json | jq -r '.data.database_username' | base64 --decode) \
	DATABASE_PASSWORD=$$(kubectl get secret hmpps-strengths-based-needs-assessments-rds-instance -o json | jq -r '.data.database_password' | base64 --decode) \
	DATABASE_NAME=$$(kubectl get secret hmpps-strengths-based-needs-assessments-rds-instance -o json | jq -r '.data.database_name' | base64 --decode) \
	; psql --pset=pager=off postgres://$$DATABASE_USERNAME:$$DATABASE_PASSWORD@localhost:${DB_PORT_FORWARD_PORT}/$$DATABASE_NAME
