# Pokedex REST API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.5-red)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3-green)](https://swagger.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)
[![Angular](https://img.shields.io/badge/Angular-21-red)](https://angular.dev/)

Spring Boot + Angular Pokédex application delivered by phases. The backend owns authentication, authorization, business rules, and PokeAPI integration. The frontend only consumes the local API.

## Phase Progress

- Phase 1: Completed (Pokemon CRUD API, validation, pagination, Swagger, tests)
- Phase 2: Completed (authentication and authorization with Spring Security and JWT)
- Phase 3: Completed baseline (Angular UI for login and Pokédex interactions)
- Phase 4: In progress (external PokeAPI integration for admin import/search)

## What The Application Does

- List Pokemons with pagination
- Retrieve Pokemon details by Pokédex ID
- Register and delete Pokemons ( `ADMIN` )
- Mark a Pokemon as "captured" ( `USER` and `ADMIN` )
- Authenticate with JWT
- Search and import Pokemon data from PokeAPI through the backend ( `ADMIN` )

## PO / QA Quick Start

## Docker Setup

### Standard Docker run

Use this when the machine can reach public HTTPS endpoints without a custom corporate certificate requirement.

```powershell
cd "PATH OF THE PROJECT"
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

### Local Setup

This step is followed if you want to deploy the app from your local machine. 

#### Prerequisites

- Java 17+
- Node.js 20+
- PostgreSQL running locally on port `5432`
- Optional: Bruno or Postman for API validation

#### Backend configuration

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
cd "PATH OF THE PROJECT"
.\mvnw.cmd spring-boot:run
```

Start the frontend in another terminal:

```powershell
cd "PATH OF THE FOLDER FRONTEND WITHIN THE PROJECT"
npm install
npm start
```

#### URLs

- Frontend: `http://localhost:4200`
- Swagger UI: `http://localhost:8082/swagger-ui.html`
- API base URL: `http://localhost:8082/api`


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

## API Documentation

- Swagger UI: `http://localhost:8082/swagger-ui.html`

## Author

- Alberto Sanchez: https://github.com/albsanchez05
