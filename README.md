# Pokédex REST API - Phase 1

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.6-red)](https://maven.apache.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3-green)](https://swagger.io/)

A complete REST API for managing a Pokédex, implemented with Spring Boot. This is the **first phase** of the Pokédex project, focused on basic registration, query, and Pokémon management functionalities.

## 📋 Project Description

This project implements a digital Pokédex that allows:
- Register newly discovered Pokémon (by Professor Oak)
- Update the capture status of a Pokémon (by a Trainer)
- Query detailed information of existing Pokémon
- List all Pokémon with pagination
- Delete Pokémon from the registry

The application follows a clean architecture with separation of responsibilities between controllers, services, and repositories.

## 🎯 Implemented Functional Requirements

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/pokemons` | Paginated list of all Pokémon (ID, name, image) |
| `GET` | `/api/pokemons/{id}` | Complete details of a specific Pokémon (including captured status) |
| `POST` | `/api/pokemons` | Register a newly discovered Pokémon (defaults to `captured = false`) |
| `POST` | `/api/pokemons/{id}` | Update capture status of an existing Pokémon |
| `DELETE` | `/api/pokemons/{id}` | Delete a Pokémon from the registry |

### Business Rules
- Duplicate Pokémon by ID or name are not allowed
- All fields in the registration request are required, except for `type2`.
- Input data is validated to ensure integrity (e.g., positive numbers for stats, non-empty names).

## 🛠️ Technologies Used

- **Backend**: Java 17, Spring Boot 3.5.13
- **Database**: PostgreSQL with Flyway for migrations
- **ORM**: Spring Data JPA with Hibernate
- **API Documentation**: Springdoc (Swagger)
- **Validation**: Spring Boot Starter Validation
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven
- **Container**: Docker (optional for PostgreSQL)

## 📁 Project Structure

```
pokedex/
├── src/
│   ├── main/
│   │   ├── java/albprojects/pokedex/
│   │   │   ├── controller/          # REST Controllers and API interface
│   │   │   ├── dto/                 # Data Transfer Objects (with validation)
│   │   │   ├── exceptions/          # Custom Exceptions
│   │   │   ├── model/               # JPA Entities
│   │   │   ├── repository/          # Data Repositories
│   │   │   └── service/             # Business Logic
│   │   └── resources/
│   │       ├── db/migration/        # Flyway Migration Scripts
│   │       └── static/              # Static Files (initial data)
│   └── test/                        # Unit and Integration Tests
├── docker-compose.yaml              # Docker Configuration
├── pom.xml                          # Maven Configuration
└── README.md                        # This Documentation
```

## 🚀 Installation and Execution

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+ (or Docker to run PostgreSQL)

### 1. Clone the Repository
```bash
git clone https://github.com/albsanchez05/albprojects-pokedex.git
cd pokedex
```

### 2. Configure the Database

#### Option A: Using Docker (Recommended)
```bash
docker-compose up -d
```

#### Option B: Local PostgreSQL
Create a database named `mi_base_datos` with user `admin` and password `password123`.

### 3. Run the Application
```bash
./mvnw spring-boot:run
```

The application will be available at `http://localhost:8080`.

### 4. Run Tests
```bash
# Unit tests
./mvnw test -Dtest=PokemonServiceTest

# Integration tests
./mvnw test -Dtest=PokemonControllerIntegrationTest

# All tests
./mvnw test
```

## 📚 API Documentation

The API is documented using **Swagger**. Once the application is running, you can access the interactive API documentation at:

[**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)

### Example Endpoints

#### `POST /api/pokemons`
Registers a new Pokémon. The `captured` status will be `false` by default.

**Request Body:**
```json
{
  "pokemonId": 1,
  "name": "Bulbasaur",
  "type1": "Grass",
  "type2": "Poison",
  "hp": 45,
  "attack": 49,
  "defense": 49,
  "spAttack": 65,
  "spDefense": 65,
  "speed": 45,
  "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
}
```

#### `POST /api/pokemons/{id}`
Updates the capture status of an existing Pokémon.

**Request Body:**
```json
{
  "pokedexId": 1,
  "captured": true
}
```

**Response:**
The full Pokémon object with the updated `captured` status.

## 📈 Future Phases

This is **Phase 1** of the Pokédex project. Future phases will include:

- **Phase 2**: Web user interface
- **Phase 3**: Authentication and multiple users
- **Phase 4**: Integration with external APIs (PokéAPI)
- **Phase 5**: Advanced features (evolutions, moves, etc.)

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👤 Author

**Alberto Sánchez** - https://github.com/albsanchez05
