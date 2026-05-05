# Pokedex REST API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.5-red)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3-green)](https://swagger.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)
[![Angular](https://img.shields.io/badge/Angular-21-red)](https://angular.dev/)

Spring Boot + Angular Pokédex application delivered by phases. The backend owns authentication, authorization, business rules, and PokeAPI integration. The frontend consumes only the local API.

## Phase Progress

- Phase 1: Completed (Pokemon CRUD API, validation, pagination, Swagger, tests)
- Phase 2: Completed (authentication and authorization with Spring Security and JWT)
- Phase 3: Completed baseline (Angular UI for login and Pokédex interactions)
- Phase 4: In progress (external PokeAPI integration for admin import/search)

## What The Application Does

- List Pokemons with pagination
- Retrieve Pokemon details by Pokédex ID
- Register and delete Pokemons ( `ADMIN` )
- Mark a Pokemon as captured ( `USER` and `ADMIN` )
- Authenticate with JWT
- Search and import Pokemon data from PokeAPI through the backend ( `ADMIN` )

## PO / QA Quick Start

### Recommended Local Windows Setup

This is the most reliable setup for PO/QA validation in the current corporate network because Java can reuse the Windows certificate store.

#### Prerequisites

- Java 17+
- Node.js 20+
- PostgreSQL running locally on port `5432`
- Optional: Bruno or Postman for API validation

#### Backend configuration

The backend now reads environment variables with safe defaults, so `application.properties` should not need manual edits.

For the current default setup:

- Database: `pokedex_database`
- User: `professor-oak`
- Password: `gottacatchthemall`
- API port: `8082`

#### Important note for corporate networks

If HTTPS traffic is inspected by a corporate certificate, set these JVM options before running the backend locally on Windows:

```powershell
$env:MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT -Djava.net.useSystemProxies=true"
```

Then start the backend:

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex"
.\mvnw.cmd spring-boot:run
```

Start the frontend in another terminal:

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex\frontend"
npm install
npm start
```

#### URLs

- Frontend: `http://localhost:4200`
- Swagger UI: `http://localhost:8082/swagger-ui.html`
- API base URL: `http://localhost:8082/api`

#### Test users

- `admin` / `Admin@123` -> fallback admin account for local manual testing
- `po-admin` -> seeded `ADMIN` user for PO validation
- `qa-user` -> seeded `USER` user for QA validation

> The plain passwords for `po-admin` and `qa-user` are intentionally not stored in the repository. Share them out-of-band if these dedicated accounts are part of UAT. If they are not available yet, PO/QA can still validate admin flows with `admin` and user flows with a freshly registered regular user.

## Docker Setup

### Standard Docker run

Use this when the machine can reach public HTTPS endpoints without a custom corporate certificate requirement.

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex"
Copy-Item ".env.example" ".env" -Force
docker-compose up --build -d
```

Access:

- Frontend: `http://localhost:4200`
- Swagger UI: `http://localhost:8082/swagger-ui.html`
- PostgreSQL: `localhost:5444`

Stop:

```powershell
docker-compose down -v
```

### Docker in a corporate network

The current Docker image runs on Linux. It cannot use `Windows-ROOT`, so if the network re-signs HTTPS traffic, external PokeAPI calls may fail unless a trusted Java truststore is provided.

The compose file now supports `JAVA_TOOL_OPTIONS` through `.env`.

Example:

```dotenv
JAVA_TOOL_OPTIONS=-Djavax.net.ssl.trustStore=/app/certs/company-truststore.jks -Djavax.net.ssl.trustStorePassword=changeit
```

This requires a truststore prepared outside the repository and made available to the container runtime.

## Environment Variables

The project root contains `.env.example` with the main Docker variables used by PO/QA.

Most important backend variables:

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `INTEGRATION_POKEAPI_BASE_URL`
- `INTEGRATION_POKEAPI_CONNECT_TIMEOUT_MS`
- `INTEGRATION_POKEAPI_READ_TIMEOUT_MS`
- `INTEGRATION_POKEAPI_ENABLED`
- `JAVA_TOOL_OPTIONS` ( Docker only, optional )

## API Client Collections

### Bruno

1. Open the `bruno/Pokedex API` collection.
2. Select environment `Local_Environment`.
3. Run `Auth/Login_User` or another login request.
4. Use the saved token for protected endpoints.

### Postman

1. Import `postman/Pokedex API.postman_collection.json`.
2. Run the login request first.
3. Reuse the saved token for the protected requests.

## Phase 4 Manual Validation

### Backend

- `GET /api/pokemons/external/25` as `ADMIN` -> expected `200`
- `GET /api/pokemons/external/pikachu` as `ADMIN` -> expected `200`
- `POST /api/pokemons/external/pikachu/import` -> expected `200` on first import, `400` on duplicate import
- Same external endpoints as `USER` -> expected `403`

### Frontend

- Login as `ADMIN`
- Open `+ Register Pokemon`
- Search `pikachu` or `25` in the external import panel
- Confirm form prefill and successful import to local DB
- Login as `USER` and confirm the register/import panel is not visible

## Running Tests

Backend:

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex"
.\mvnw.cmd test
```

Frontend:

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex\frontend"
npm run build
```

## API Documentation

- Swagger UI: `http://localhost:8082/swagger-ui.html`

## Author

- Alberto Sanchez: https://github.com/albsanchez05
