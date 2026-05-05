# Phase 4 - External API Integration

## Why

The current Pokedex requires manual data entry for new Pokemon, which is slow and error-prone. Phase 4 integrates external Pokemon data so admins can import reliable base information faster and maintain consistency.

## What

Integrate the backend with PokeAPI and expose controlled import/search capabilities to populate local Pokemon entries from external data while preserving local business rules and security.

## Constraints

### Must
- Keep local database as the source of truth for the app.
- Use backend integration for external API calls ( no direct frontend call to PokeAPI ).
- Preserve existing auth and role model ( `USER`, `ADMIN` ).
- Maintain existing endpoint behavior for current clients.
- Add clear timeout/error handling for external API failures.

### Must Not
- Do not expose PokeAPI dependency details directly in frontend components.
- Do not bypass local validation/business rules when importing.
- Do not modify unrelated frontend routing/auth features from Phase 3.

### Out of Scope
- Background job scheduling for periodic sync.
- Bulk import of full Pokemon catalog.
- Multi-provider external integrations beyond PokeAPI.

## Current State

Phase 3 already provides authenticated UI flows ( list, detail, capture, admin actions ) and stable backend JWT-based protection. Pokemon creation is manual through existing API contract.

Phase 4 now also includes:

- Backend external integration package under `src/main/java/albprojects/pokedex/integration/pokeapi/`
- Admin-only backend endpoints for external search/import
- Angular admin import/search panel in `frontend/src/app/features/pokemon/pokedex-grid/`
- Improved upstream error mapping for timeout, DNS, and TLS/certificate failures

- Relevant files: `src/main/java/albprojects/pokedex/pokemon/controller/PokemonController.java`
- Relevant files: `src/main/java/albprojects/pokedex/pokemon/service/PokemonService.java`
- Relevant files: `src/main/java/albprojects/pokedex/common/config/SecurityConfig.java`
- Relevant files: `frontend/src/app/features/pokemon/pokedex-grid/`
- Existing patterns to follow: service-layer orchestration, DTO-driven API contracts, global exception handling, role-based endpoint security

## Tasks

### T1: Define External Integration Layer
**What:** Add a dedicated PokeAPI client/service in backend with DTO mapping and resilient HTTP configuration ( timeout + error translation ).
**Files:** `src/main/java/albprojects/pokedex/integration/pokeapi/`, `src/main/resources/application.properties`
**Verify:** `./mvnw.cmd test -Dtest=*Integration*`

### T2: Add Import/Search Backend Endpoints
**What:** Expose admin-protected endpoints to search/import Pokemon from PokeAPI into local DB, reusing existing validation and duplicate checks.
**Files:** `src/main/java/albprojects/pokedex/pokemon/controller/`, `src/main/java/albprojects/pokedex/pokemon/service/`, `src/main/java/albprojects/pokedex/common/config/SecurityConfig.java`
**Verify:** `./mvnw.cmd test -Dtest=PokemonControllerIntegrationTest`

### T3: Extend Frontend Admin Import Flow
**What:** Add UI controls for admins to fetch Pokemon data from external source and prefill registration form before saving locally.
**Files:** `frontend/src/app/core/services/pokemon.service.ts`, `frontend/src/app/features/pokemon/pokedex-grid/`
**Verify:** Manual check: admin can search/import external Pokemon data and save to local Pokedex

### T4: Add Tests and Error Handling for External Failures
**What:** Cover success/failure scenarios ( timeout, 404 from external API, malformed payload ) and present clear user-facing messages.
**Files:** `src/test/java/albprojects/pokedex/`, `frontend/src/app/features/pokemon/pokedex-grid/`
**Verify:** `./mvnw.cmd test` and `cd frontend && npm run build`

## Validation

- `./mvnw.cmd clean test`
- `./mvnw.cmd spring-boot:run`
- `cd frontend && npm install && npm run build`
- Manual check: login as `ADMIN`, import a Pokemon from external source, save it, then verify list/detail locally.
- Manual check: login as `USER` and confirm import actions are not visible.

## PO / QA Environment Notes

### Recommended validation path

For PO/QA on Windows inside the corporate network, prefer a local host execution of the backend with the Windows certificate store enabled for Java:

```powershell
$env:MAVEN_OPTS="-Djavax.net.ssl.trustStoreType=Windows-ROOT -Djava.net.useSystemProxies=true"
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex"
.\mvnw.cmd spring-boot:run
```

Then run the frontend locally:

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex\frontend"
npm start
```

### Why this matters

PokeAPI is called by the backend over HTTPS. In a corporate network with TLS inspection, Java may reject the re-signed certificate unless it is told to trust the Windows certificate store.

### Docker note

Docker can still be used for standard environments, but in a corporate TLS-inspected network the Linux container will require a custom Java truststore. This is now exposed through `JAVA_TOOL_OPTIONS` in `docker-compose.yaml`, but the truststore itself must be provided outside the repository.

### Suggested manual evidence for Phase 4

- Swagger: `GET /api/pokemons/external/25` returns `200` as `ADMIN`
- Swagger: `POST /api/pokemons/external/pikachu/import` returns `200` on first call and `400` on second call
- UI: admin sees the external import panel and can import a Pokemon
- UI: regular user does not see the admin panel

