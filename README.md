# Pokedex REST API

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.8.5-red)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3-green)](https://swagger.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)

A complete REST API for managing a Pokedex, implemented with Spring Boot. The project is delivered incrementally by phases, with each phase extending the existing baseline.

## Phase Progress

- Phase 1: Completed (Pokemon CRUD API, validation, global exception handling, pagination, Swagger, tests)
- Phase 2: Completed (authentication and authorization with Spring Security and JWT, Dockerization)
- Phase 3: In progress (Web user interface)

## Project Description

This project implements a digital Pokedex that allows:
- Register newly discovered Pokemon
- Update the capture status of a Pokemon
- Query detailed information of existing Pokemon
- List all Pokemon with pagination (sorted by Pokedex ID in ascending order)
- Delete Pokemon from the registry

The application follows a clean architecture with separation between feature modules and shared components.

## How to Run This Project

You can run this project in two ways: using Docker (recommended for a quick setup) or running it locally on your machine.

### Option A: Running with Docker (Recommended)

This is the easiest way to get started, as it handles all dependencies for you.

**Prerequisites:**
*   Docker and Docker Compose

**Steps:**

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/albsanchez05/albprojects-pokedex.git
    cd albprojects-pokedex
    ```

2.  **Build and Run with Docker Compose**
    Execute the following command from the project root. It will build the Java app, create a Docker image, and start both the application and PostgreSQL containers.
    ```bash
    docker-compose up --build -d
    ```

3.  **Access the Application**
    *   **API & Swagger UI**: `http://localhost:8082/swagger-ui.html`
    *   **Database (for inspection)**: Host `localhost`, Port `5444`, DB `pokedex_database`, User `professor-oak`, Pass `gottacatchthemall`.

4.  **Stopping the Application**
    ```bash
    docker-compose down -v
    ```

### Option B: Running Locally

Use this option if you prefer to run the application directly on your host machine.

**Prerequisites:**
*   Java 17 or higher
*   Maven 3.6+
*   A running PostgreSQL instance

**Steps:**

1.  **Clone the Repository**
    ```bash
    git clone https://github.com/albsanchez05/albprojects-pokedex.git
    cd albprojects-pokedex
    ```

2.  **Configure and Run the Database**
    Ensure your PostgreSQL instance is running. You may need to create a database and user, and then update the credentials in `src/main/resources/application.properties`:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
    spring.datasource.username=your_user
    spring.datasource.password=your_password
    ```
    *Note: The project is currently configured to connect to the Docker database on port `5444`. You will need to adjust this for your local setup.*

3.  **Run the Application with Maven**
    ```bash
    ./mvnw spring-boot:run
    ```

4.  **Access the Application**
    *   **API & Swagger UI**: `http://localhost:8082/swagger-ui.html`

## Testing the API

To make testing easy, this project includes pre-configured collections for popular API clients. These collections automatically handle authentication tokens.

### For Postman Users

1.  **Install Postman**: Download it from the [Postman website](https://www.postman.com/downloads/).
2.  **Import the Collection**:
    *   Open Postman and click `File > Import...`.
    *   Select the file `postman/Pokedex API.postman_collection.json` from this project.
3.  **Run the `Login` Request**:
    *   In the imported collection, expand the `Auth` folder and click on the `Login` request.
    *   Click the `Send` button. This will authenticate you and automatically save the token.
4.  **Test Other Endpoints**: Now you can run any other request (like `Get All Pokemons`) and it will be automatically authenticated.

### For Bruno Users

1.  **Install Bruno**: Download it from the [Bruno website](https://www.usebruno.com/downloads).
2.  **Open the Collection**:
    *   In Bruno, click `Open Collection`.
    *   In the file dialog, navigate into the `bruno` directory and select the **`Pokedex API`** folder.
3.  **Select the Environment**:
    *   In the top-right corner of the Bruno window, click the dropdown menu that says `No Environment`.
    *   Select **`Local_Environment`** from the list. This is crucial for the API URLs to work.
4.  **Run the `Login` Request**:
    *   Expand the `Auth` folder and click on the `Login_User` request.
    *   Click the "play" button (▶️). This will authenticate you and automatically save the token.
5.  **Test Other Endpoints**: Now you can run any other request and it will be automatically authenticated.

## Running Tests

To run the suite of integration and unit tests, you can use the standard Maven command. This does not require Docker.

```bash
./mvnw test
```

## API Documentation

For a quick overview and manual testing, the API documentation is available through Swagger UI once the application is running.

- **Swagger UI URL**: `http://localhost:8082/swagger-ui.html`

## Roadmap

- Phase 1: Core Pokemon API (completed)
- Phase 2: Authentication and multiple users (completed)
- Phase 3: Web user interface (in progress)
- Phase 4: Integration with external APIs (PokeAPI)
- Phase 5: Advanced features

## Author

- Alberto Sanchez: https://github.com/albsanchez05
