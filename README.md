# auth-microservice

Authentication and per-snippet authorization for the [Snippet Searcher platform](https://github.com/JVM-austral).
Every other service asks this one two questions: *who is this user?* and *can they read or write this snippet?*

Part of a microservices platform built with Kotlin and Spring Boot. See the
[organization overview](https://github.com/JVM-austral) for how the services fit together.

## What it does

- **Validates Auth0-issued JWTs** as an OAuth2 resource server, checking both the issuer and the audience.
- **Syncs users on first request**: a security filter creates the local user from the token's `sub` the first time
  it sees it, so there's no separate sign-up step between Auth0 and the platform.
- **Owns snippet permissions**: grants READ or WRITE access, revokes it, and answers permission checks for the other
  services.
- **Enforces sharing rules**: the first grant on a new snippet registers its creator; after that, only users with
  WRITE access can share it or revoke access. Duplicate grants are rejected, and nobody can revoke their own access.

## API

All endpoints require a valid Bearer token.

| Method | Path | Purpose |
| --- | --- | --- |
| `GET` | `/authentication/validate-user` | Validate the caller's token |
| `POST` | `/snippet-permissions/grant-read-access` | Share a snippet as read-only |
| `POST` | `/snippet-permissions/grant-write-access` | Share a snippet with edit rights |
| `DELETE` | `/snippet-permissions/revoke-access` | Revoke a user's access |
| `POST` | `/snippet-permissions/validate-read-access` | Can the user read this snippet? |
| `POST` | `/snippet-permissions/validate-write` | Can the user edit this snippet? |
| `GET` | `/snippet-permissions/shared-snippets/{userId}` | Snippets shared with a user |
| `GET` | `/users`, `/users/paginated`, `/users/{id}` | Look up users (e.g. to pick who to share with) |
| `POST` | `/users` | Create a user |

## Stack

Kotlin · Spring Boot 3 · Spring Security (OAuth2 resource server) · Spring Data JPA · PostgreSQL · Auth0 ·
Docker · New Relic (APM)

## Quality

- Unit, integration and end-to-end tests (`src/test`).
- The build fails below **80 % coverage** (JaCoCo) or on any **ktlint** violation; both are part of `./gradlew check`.
- CI/CD on GitHub Actions: build and test, push the Docker image to GHCR, then deploy to the dev or prod VM over
  SSH.

## Run it locally

Requires Docker and an Auth0 tenant (API audience + issuer).

```bash
cp .env-example .env    # database settings
# also set AUTH0_AUDIENCE, AUTH0_DOMAIN, AUTH0_CLIENT_ID, AUTH0_CLIENT_SECRET and AUTH_SERVER_URI
# (see src/main/resources/application.yml)
docker compose up --build
```

The service listens on `SERVER_PORT_AUTHORIZATION` (8000 by default in `.env-example`), backed by its own Postgres.
To run the whole platform together, use the `docker-compose.yml` in
[`snippet-manager`](https://github.com/JVM-austral/snippet-manager).

Tests:

```bash
./gradlew check
```
