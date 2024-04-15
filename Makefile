SHELL = '/bin/bash'
DEV_COMPOSE_FILES = -f docker-compose.yml -f docker-compose.dev.yml
TEST_COMPOSE_FILES = -f docker-compose.yml -f docker-compose.test.yml
LOCAL_COMPOSE_FILES = -f docker-compose.yml -f docker-compose.local.yml
PROJECT_NAME = hmpps-strengths-based-needs-assessments

export COMPOSE_PROJECT_NAME=${PROJECT_NAME}

default: help

help: ## The help text you're reading.
	@grep --no-filename -E '^[0-9a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

up: ## Starts/restarts the API in a production container.
	docker compose ${LOCAL_COMPOSE_FILES} down api
	docker compose ${LOCAL_COMPOSE_FILES} up api --wait --no-recreate

down: ## Stops and removes all containers in the project.
	docker compose down

build-api: ## Builds a production image of the API.
	docker compose build api

dev-up: ## Starts/restarts the API in a development container. A remote debugger can be attached on port 5005.
	docker compose down api
	docker compose ${DEV_COMPOSE_FILES} up --wait --no-recreate api

dev-build: ## Builds a development image of the API.
	docker compose ${DEV_COMPOSE_FILES} build api

dev-down: ## Stops and removes the API container.
	docker compose down api

rebuild: ## Re-builds and live-reloads the API.
	docker compose ${DEV_COMPOSE_FILES} exec api gradle compileKotlin --parallel --build-cache --configuration-cache

watch: ## Watches for file changes and live-reloads the API. To be used in conjunction with dev-up e.g. "make dev-up watch"
	docker compose ${DEV_COMPOSE_FILES} exec api gradle compileKotlin --continuous --parallel --build-cache --configuration-cache

test: ## Runs the test suite.
	docker compose ${DEV_COMPOSE_FILES} exec api gradle test --parallel

test-coverage: ## Runs the test suite and outputs a code coverage report.
	docker compose ${DEV_COMPOSE_FILES} exec api gradle koverHtmlReport --parallel

lint: ## Runs the Kotlin linter.
	docker compose ${DEV_COMPOSE_FILES} exec api gradle ktlintCheck detekt --parallel

lint-baseline: ## Generate a baseline file, ignoring all existing code smells.
	docker compose ${DEV_COMPOSE_FILES} exec api gradle detektBaseline --parallel

test-up: ## Stands up a test environment.
	docker compose --progress plain pull
	docker compose --progress plain ${TEST_COMPOSE_FILES} -p ${PROJECT_NAME}-test up --wait --force-recreate

test-down: ## Stops and removes all of the test containers.
	docker compose --progress plain ${TEST_COMPOSE_FILES} -p ${PROJECT_NAME}-test down

clean: ## Stops and removes all project containers. Deletes local build/cache directories.
	docker compose down
	rm -rf .gradle build

update: ## Downloads the latest versions of containers.
	docker compose pull
