# Pokedex REST API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.6-red)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3-green)](https://swagger.io/)

A complete REST API for managing a Pokedex, implemented with Spring Boot. The project is delivered incrementally by phases, with each phase extending the existing baseline.

## Phase Progress

- Phase 1: Completed (Pokemon CRUD API, validation, global exception handling, pagination, Swagger, tests)
- Phase 2: In progress (authentication and authorization with Spring Security and JWT)

## Project Description

This project implements a digital Pokedex that allows:
- Register newly discovered Pokemon
- Update the capture status of a Pokemon
- Query detailed information of existing Pokemon
- List all Pokemon with pagination (sorted by Pokedex ID in ascending order)
- Delete Pokemon from the registry

The application follows a clean architecture with separation between feature modules and shared components.

## Implemented Functional Requirements

### API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/pokemons` | Paginated list of all Pokemon (ID, name, image). Returns 404 if requested page does not exist. |
| `GET` | `/api/pokemons/{id}` | Complete details of a specific Pokemon (including captured status). |
| `POST` | `/api/pokemons` | Register a newly discovered Pokemon (defaults to `captured = false`). |
| `POST` | `/api/pokemons/{id}` | Update capture status of an existing Pokemon. |
| `DELETE` | `/api/pokemons/{id}` | Delete a Pokemon from the registry (idempotent behavior by business rule). |

### Business Rules

- Duplicate Pokemon by ID or name are not allowed.
- All registration fields are required except `type2`.
- Input data is validated to ensure integrity.
- `captured` defaults to `false` during registration.
- Deleting a non-existing Pokemon does not raise an error.

## Technologies Used

- Backend: Java 17, Spring Boot 3.5.13
- Database: PostgreSQL with Flyway for migrations
- ORM: Spring Data JPA with Hibernate
- Security: Spring Security + JWT (JJWT)
- API Documentation: Springdoc (Swagger)
- Validation: Spring Boot Starter Validation
- Testing: JUnit 5, Mockito, Spring Boot Test
- Build Tool: Maven
- Container: Docker (optional for PostgreSQL)

## Project Structure

```
albprojects-pokedex/
├── src/
│   ├── main/
│   │   ├── java/albprojects/pokedex/
│   │   │   ├── auth/                # Auth domain (models, repository, services)
│   │   │   ├── common/              # Shared config and exceptions
│   │   │   ├── pokemon/             # Pokemon feature (controller, dto, model, repository, service)
│   │   │   └── PokedexApplication.java
│   │   └── resources/
│   │       ├── db/migration/        # Flyway migration scripts
│   │       └── static/              # Static files (initial data)
│   └── test/                        # Unit and integration tests
├── docs/                            # Phase documentation
├── .github/templates/               # Internal templates
├── docker-compose.yaml
├── pom.xml
└── README.md
```

## Installation and Execution

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or Docker to run PostgreSQL)

### 1. Clone the Repository

```bash
git clone https://github.com/albsanchez05/albprojects-pokedex.git
cd albprojects-pokedex
```

### 2. Configure the Database

#### Option A: Docker

```bash
docker-compose up -d
```

#### Option B: Local PostgreSQL

Create a database and role matching your values in `src/main/resources/application.properties`.

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

The application is available at `http://localhost:8080`.

### 4. Run Tests

```bash
./mvnw test -Dtest=PokemonServiceTest
./mvnw test -Dtest=PokemonControllerIntegrationTest
./mvnw test
```

## API Documentation

Swagger UI:

- `http://localhost:8080/swagger-ui.html`

## Roadmap

- Phase 1: Core Pokemon API (completed)
- Phase 2: Authentication and multiple users (in progress)
- Phase 3: Web user interface
- Phase 4: Integration with external APIs (PokeAPI)
- Phase 5: Advanced features

## Author

- Alberto Sanchez: https://github.com/albsanchez05
