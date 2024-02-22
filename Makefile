SHELL = '/bin/bash'
LOCAL_COMPOSE_FILES = -f docker-compose.yml -f docker-compose.local.yml
export COMPOSE_PROJECT_NAME=hmpps-strengths-based-needs-assessments

default: help

help: ## The help text you're reading.
	@grep --no-filename -E '^[0-9a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

up: ## Starts/restarts the API in a production container.
	docker compose down api
	docker compose up api --wait --no-recreate

down: ## Stops and removes all containers in the project.
	docker compose down

build-api: ## Builds a production image of the API.
	docker compose build api

dev-up: ## Starts/restarts the API in a development container. A remote debugger can be attached on port 5005.
	docker compose down api
	docker compose ${LOCAL_COMPOSE_FILES} up --wait --no-recreate api

dev-build: ## Builds a development image of the API.
	docker compose ${LOCAL_COMPOSE_FILES} build api

dev-down: ## Stops and removes the API container.
	docker compose down api

rebuild: dev-up ## Re-builds and live-reloads the API.
	docker compose ${LOCAL_COMPOSE_FILES} exec api gradle compileKotlin --parallel --build-cache --configuration-cache

watch: dev-up ## Watches for file changes and live-reloads the API. To be used in conjunction with dev-up e.g. "make dev-up watch"
	docker compose ${LOCAL_COMPOSE_FILES} exec api gradle compileKotlin --continuous --parallel --build-cache --configuration-cache

test: ## Runs the test suite
	./gradlew test

lint: ## Runs the Kotlin linter
	./gradlew ktlintCheck

clean: ## Stops and removes all project containers. Deletes local build/cache directories.
	docker compose down
	rm -rf .gradle build
