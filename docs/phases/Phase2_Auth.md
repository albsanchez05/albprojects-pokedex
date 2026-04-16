# Phase 2 - Authentication

## Why

Phase 1 exposes all routes without authentication, which prevents user identification and leaves sensitive operations unprotected. This phase introduces a robust authentication foundation to prepare for multi-user support and progressive security hardening of the Pokedex API.

## What

Implement stateless JWT authentication with users persisted in the database, a login endpoint, role-based protection for business endpoints, standardized auth error handling, and unit/integration test coverage to validate the full flow ( login -> token -> access to protected API ).

### Role Model and Endpoint Access

- `ADMIN`: full access to authentication and Pokemon endpoints, including write operations.
- `USER`: access to authentication endpoints, read Pokemon endpoints, and capture endpoint.

Access rules to implement:

- `POST /api/auth/register`: public
- `POST /api/auth/login`: public
- `GET /api/pokemons/**`: authenticated ( `USER` or `ADMIN` )
- `POST /api/pokemons`: `ADMIN` only
- `POST /api/pokemons/{id}`: authenticated ( `USER` or `ADMIN` )
- `PUT /api/pokemons/**`: `ADMIN` only
- `DELETE /api/pokemons/**`: `ADMIN` only

## Constraints

### Must
- Keep the current controller/service/repository architecture and existing Spring Boot conventions.
- Add only dependencies strictly required for authentication.
- Use `spring-boot-starter-security` as the base security dependency.
- Use one JWT implementation strategy consistently. Preferred option for this phase: Spring-native JWT support via `spring-security-oauth2-jose` ( do not mix multiple JWT libraries ).
- Use Flyway for all schema changes related to users/roles.
- Keep the API stateless ( no server session ).
- Preserve compatibility with existing Pokemon business features ( outside access protection changes ).
- Enforce exactly two business roles for this phase: `ADMIN` and `USER`.

### Must Not
- Do not modify Pokemon business logic that is unrelated to authentication.
- Do not introduce unnecessary external frameworks ( IAM, OAuth provider, etc. ).
- Do not break existing tests without explicit adaptations related to security.
- Do not store passwords in plain text.

### Out of Scope
- OAuth2/social login implementation ( local auth only in Phase 2 ).
- Refresh tokens / advanced token rotation ( limitation: users re-login when token expires ).
- Forgot-password/email verification flows ( accepted product limitation ).
- Fine-grained authorization by Pokemon resource ( limitation: initial global endpoint-level RBAC ).
- Advanced hardening ( rate limiting, full audit trail, distributed anti-bruteforce protections ).

## Current State

The backend exposes Pokemon endpoints through `PokemonController` and `PokemonControllerAPI`, with business logic in `PokemonService` and persistence in `PokemonRepository`. Business errors are centralized in `GlobalExceptionHandler`.
No security/auth building blocks currently exist ( no Spring Security/JWT dependency, no user entity, no login endpoint, no security filter ).
There is currently no explicit role model ( `ADMIN`, `USER` ) and no endpoint-level authorization matrix.

- Relevant files: `pom.xml`
- Relevant files: `src/main/java/albprojects/pokedex/controller/PokemonController.java`
- Relevant files: `src/main/java/albprojects/pokedex/controller/PokemonControllerAPI.java`
- Relevant files: `src/main/java/albprojects/pokedex/service/PokemonService.java`
- Relevant files: `src/main/java/albprojects/pokedex/repository/PokemonRepository.java`
- Relevant files: `src/main/java/albprojects/pokedex/exceptions/GlobalExceptionHandler.java`
- Relevant files: `src/main/resources/db/migration/V1__create_table_pokemon.sql`
- Relevant files: `src/test/java/albprojects/pokedex/PokemonServiceTest.java`
- Relevant files: `src/test/java/albprojects/pokedex/PokemonControllerIntegrationTest.java`
- Existing patterns to follow: Spring dependency injection, DTO validation, JUnit/Mockito + MockMvc integration tests, versioned Flyway migrations.

## Tasks

### T1: Introduce Spring Security baseline and stateless configuration
**What:** Add required security dependencies, create global security configuration ( disable CSRF for stateless API, stateless policy, public auth routes, protected Pokemon routes by role rule ), wire a password encoder, and codify the `ADMIN`/`USER` access matrix.
**Files:** `pom.xml`, `src/main/java/albprojects/pokedex/config/SecurityConfig.java`, `src/main/resources/application.properties`
**Verify:** `./mvnw.cmd clean compile`

### T2: Add user model and Flyway migrations
**What:** Create minimal auth entities/models ( `User`, `Role` ), user repository, and SQL migrations for user/role tables with uniqueness constraints ( username/email ).
**Files:** `src/main/java/albprojects/pokedex/model/User.java`, `src/main/java/albprojects/pokedex/model/Role.java`, `src/main/java/albprojects/pokedex/repository/UserRepository.java`, `src/main/resources/db/migration/V4__create_table_users.sql`, `src/main/resources/db/migration/V5__create_table_user_roles.sql`
**Verify:** `./mvnw.cmd test -Dtest=PokemonServiceTest`

### T3: Implement JWT authentication services
**What:** Add a custom `UserDetailsService`, JWT service ( generation/validation ), JWT filter ( `OncePerRequestFilter` ), and authentication service ( register/login ) with password hashing and authorities loading.
**Files:** `src/main/java/albprojects/pokedex/service/AuthService.java`, `src/main/java/albprojects/pokedex/service/CustomUserDetailsService.java`, `src/main/java/albprojects/pokedex/config/JwtService.java`, `src/main/java/albprojects/pokedex/config/JwtAuthenticationFilter.java`
**Verify:** `./mvnw.cmd -DskipTests compile`

### T4: Expose auth endpoints and related DTOs
**What:** Create `/api/auth/register` and `/api/auth/login` endpoints, auth request/response DTOs, and document expected responses ( success/errors ).
**Files:** `src/main/java/albprojects/pokedex/controller/AuthController.java`, `src/main/java/albprojects/pokedex/controller/AuthControllerAPI.java`, `src/main/java/albprojects/pokedex/dto/AuthLoginRequestDTO.java`, `src/main/java/albprojects/pokedex/dto/AuthRegisterRequestDTO.java`, `src/main/java/albprojects/pokedex/dto/AuthResponseDTO.java`
**Verify:** `./mvnw.cmd test -Dtest=AuthControllerIntegrationTest`

### T5: Update error handling and add security test coverage
**What:** Extend error handling for 401/403/invalid auth cases, add auth unit tests and MockMvc integration tests ( no token, invalid token, valid token, insufficient role for `USER` on admin-only endpoints ), then adjust existing Pokemon tests if impacted by security.
**Files:** `src/main/java/albprojects/pokedex/exceptions/GlobalExceptionHandler.java`, `src/main/java/albprojects/pokedex/exceptions/AuthenticationException.java`, `src/test/java/albprojects/pokedex/AuthServiceTest.java`, `src/test/java/albprojects/pokedex/AuthControllerIntegrationTest.java`, `src/test/java/albprojects/pokedex/PokemonControllerIntegrationTest.java`
**Verify:** `./mvnw.cmd test`

## Validation

End-to-end validation after full auth phase implementation:

- `./mvnw.cmd clean test`
- `./mvnw.cmd test -Dtest=AuthServiceTest,AuthControllerIntegrationTest,PokemonControllerIntegrationTest`
- `./mvnw.cmd spring-boot:run`
- Manual check: create a user through `/api/auth/register`, obtain a JWT via `/api/auth/login`, call a protected Pokemon route without token ( 401 ), then with a valid token ( 200 ), and verify a forbidden access case ( 403 ) based on role.


