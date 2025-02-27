# hmpps-strengths-based-needs-assessments-api
![Main Pipeline](https://github.com/ministryofjustice/hmpps-strengths-based-needs-assessments-api/actions/workflows/pipeline_main.yml/badge.svg?branch=main)
[![repo standards badge](https://img.shields.io/badge/dynamic/json?color=blue&style=flat&logo=github&label=MoJ%20Compliant&query=%24.message&url=https%3A%2F%2Foperations-engineering-reports.cloud-platform.service.justice.gov.uk%2Fapi%2Fv2%2Fcompliant-repository%2Fhmpps-strengths-based-needs-assessments-api)](https://operations-engineering-reports.cloud-platform.service.justice.gov.uk/public-report/hmpps-strengths-based-needs-assessments-api "Link to report")
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

Deployments of the main branch to Development -> Preproduction -> Production are automated through the Main workflow in GitHub Actions.

To deploy a branch manually to the Dev or Test environment, go to the Actions tab on GitHub and follow these steps:

1. Select the "Deploy to environment" workflow
2. Click "Run workflow"
3. Select the branch you wish to deploy
4. Select the environment you wish to deploy to
5. Optionally input an image tag (from the Docker registry)
6. Click "Run workflow"

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
