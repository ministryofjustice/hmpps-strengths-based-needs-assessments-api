# hmpps-strengths-based-needs-assessments-api
[![repo standards badge](https://img.shields.io/badge/dynamic/json?color=blue&style=flat&logo=github&label=MoJ%20Compliant&query=%24.message&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv2%2Fcompliant-repository%2Fhmpps-strengths-based-needs-assessments-api)](https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/public-report/hmpps-strengths-based-needs-assessments-api "Link to report")
[![CircleCI](https://circleci.com/gh/ministryofjustice/hmpps-strengths-based-needs-assessments-api/tree/main.svg?style=svg)](https://circleci.com/gh/ministryofjustice/hmpps-strengths-based-needs-assessments-api)
[![Docker Repository on Quay](https://quay.io/repository/hmpps/hmpps-strengths-based-needs-assessments-api/status "Docker Repository on Quay")](https://quay.io/repository/hmpps/hmpps-strengths-based-needs-assessments-api)
[![API docs](https://img.shields.io/badge/API_docs_-view-85EA2D.svg?logo=swagger)](https://api.strengths-based-needs-dev.hmpps.service.justice.gov.uk/swagger-ui/index.html#/)

API for the Strengths and Needs assessment service.

## Running the service

The service and all of its dependencies are run in [Docker](https://www.docker.com/get-started/) containers.

To start it, run:

`make up`

The service is on http://localhost:8080

To check the status, go to http://localhost:8080/health

The UI can be accessed on http://localhost:7072

The Swagger docs are on http://localhost:8080/swagger-ui/index.html 

To update containers

`make down update up`

## Development

To start the API in development mode, run:

`make dev-up`

To enable live-reload after starting in development mode, run:

`make watch`

A remote debugger can be attached to the containerised JVM on port 5005

![debugger.png](.readme/debugger.png)

To generate an authentication token (JWT) for the local API, run:

`make dev-api-token`

Run `make` to see the full list of dev commands.

## Testing

`make lint` to run the linter.

`make test` to run the test suite.

## Deployment

Deployments of the main branch to Development -> Preproduction -> Production are automated through the [build-test-and-deploy](https://app.circleci.com/pipelines/github/ministryofjustice/hmpps-strengths-based-needs-assessments-api/554/workflows/228227bb-282f-4322-8414-178e82b0f60e) workflow in CircleCI.

To deploy a branch manually to the Test environment, open the project in CircleCI and follow these steps:

1. Select the branch you wish to deploy
2. Press the "Trigger Pipeline" button
3. Add a string parameter named "deploy" with value "test"
4. Press "Trigger Pipeline"

## Connecting to a remote Database in dev/preprod/prod

1. Switch to the Kubernetes context/namespace of the database you are connecting to
2. `make db-port-forward-pod` to create a DB port-forwarding pod in the namespace
3. `make db-port-forward` to forward traffic from your machine to the pod

Then open a new terminal and either:

`make db-connect` to connect to the remote DB on the command line

or

`make db-connection-string` to output a connection string for your database IDE

To export the remote DB (saved locally as out.sql), run:

`make db-export`

## Service dependencies

* [hmpps-auth](https://github.com/ministryofjustice/hmpps-auth) - for authentication using OAuth/JWT
* PostgreSQL - for persisting assessment data
