# Pokédex REST API - Phase 1

[![Java](https://img.shields.io/badge/Java-17-orange)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9.6-red)](https://maven.apache.org/)

A complete REST API for managing a Pokédex, implemented with Spring Boot. This is the **first phase** of the Pokédex project, focused on basic registration, query, and Pokémon management functionalities.

## 📋 Project Description

This project implements a digital Pokédex that allows:
- Register newly discovered Pokémon
- Query detailed information of existing Pokémon
- Manage Pokémon capture status
- List all Pokémon with pagination
- Delete Pokémon from the registry

The application follows a clean architecture with separation of responsibilities between controllers, services, and repositories.

## 🎯 Implemented Functional Requirements

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/pokemons` | Paginated list of all Pokémon (ID, name, image) |
| `GET` | `/api/pokemons/{id}` | Complete details of a specific Pokémon |
| `POST` | `/api/pokemons` | Register a newly discovered Pokémon |
| `POST` | `/api/pokemons/capture` | Register capture of an existing Pokémon |
| `DELETE` | `/api/pokemons/{id}` | Delete a Pokémon from the registry |
| `DELETE` | `/api/pokemons` | Delete all Pokémon |

### Business Rules
- Pokémon IDs must be between 1 and 151 (first generation limit)
- Duplicate Pokémon by ID or name are not allowed
- Required fields include: ID, name, type1, HP, attack, defense, special attack, special defense, speed, and image
- Type2 is optional

## 🛠️ Technologies Used

- **Backend**: Java 17, Spring Boot 3.5.13
- **Database**: PostgreSQL with Flyway for migrations
- **ORM**: Spring Data JPA with Hibernate
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Build Tool**: Maven
- **Container**: Docker (optional for PostgreSQL)

## 📁 Project Structure

```
pokedex/
├── src/
│   ├── main/
│   │   ├── java/albprojects/pokedex/
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── dto/                 # Data Transfer Objects
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
git clone https://github.com/your-username/pokedex.git
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

The application will be available at `http://localhost:8080`

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

### GET /api/pokemons
Paginated list of Pokémon with basic information.

**Query Parameters:**
- `page` (optional): Page number (0-based)
- `size` (optional): Page size (default: 20)

**Response:**
```json
{
  "content": [
    {
      "pokemonId": 1,
      "name": "Bulbasaur",
      "image": "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/1.png"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20
  },
  "totalElements": 1
}
```

### GET /api/pokemons/{id}
Complete details of a Pokémon.

**Parameters:**
- `id`: Pokémon ID (1-151)

**Response:**
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

### POST /api/pokemons
Register a new Pokémon.

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

### POST /api/pokemons/capture
Register capture of an existing Pokémon.

**Request Body:**
```json
{
  "pokedexId": 1,
  "name": "Bulbasaur"
}
```

### DELETE /api/pokemons/{id}
Delete a specific Pokémon.

### DELETE /api/pokemons
Delete all Pokémon.

## 🧪 Testing

The project includes comprehensive test coverage:

- **Unit Tests** (18 tests): Test business logic in isolation
- **Integration Tests** (10 tests): Verify end-to-end behavior with real database

Coverage includes:
- Success and error cases
- Business validations
- Exception handling
- Pagination and filters

## 🔧 Configuration

### Environment Variables
The application uses the following configurations (in `application.properties`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5444/mi_base_datos
spring.datasource.username=admin
spring.datasource.password=password123
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
```

### Database Migrations
Migrations are managed with Flyway. The initial script creates the `pokemon` table with all necessary fields.

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

## 📄 License

This project is under the MIT License. See the `LICENSE` file for more details.

## 👤 Author

**Alberto Sánchez** - https://github.com/albsanchez05

## 🙏 Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot) for the framework
- [PokéAPI](https://pokeapi.co/) for Pokémon data
- Development community for best practices

---

⭐ If you like this project, give it a star on GitHub!
