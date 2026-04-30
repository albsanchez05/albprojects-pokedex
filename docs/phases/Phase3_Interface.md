# Phase 3 - Web User Interface

## Why

This phase introduces a user-friendly web application for the existing Pokedex REST API. It enables end users to interact with authentication and Pokemon features without API client tools while preserving the team's Angular-based frontend direction.

## What

Build a complete, responsive Angular Single-Page Application (SPA) that consumes the current backend API for registration, login, and Pokemon management flows.

## Constraints

### Must
- Framework: Angular ( latest stable version )
- Routing: Angular Router
- Styling: Tailwind CSS
- API communication: Angular HttpClient
- Create the frontend as a separate project under `frontend/` generated with Angular CLI
- Handle and display API error responses correctly in the UI
- Ensure local and Docker compatibility through relative API routing ( `/api/...` )

### Must Not
- Do not add backend dependencies
- Do not modify the existing backend API contract ( endpoints, request bodies, response bodies )

### Out of Scope
- Search and filtering in the Pokedex grid
- Complex state management libraries ( e.g., NgRx ) for this phase
- Full backend role-claim redesign in JWT payload ( UI includes a fallback role-capability probe )

## Current State

The backend is already implemented, dockerized, and includes authentication plus Pokemon CRUD endpoints. Swagger UI is available at `http://localhost:8082/swagger-ui.html`, and the stack can be started with Docker Compose.

Phase 3 implementation now includes:

- Authentication pages with validations and explicit API error rendering.
- Protected routes with logout-capable top navigation.
- Paginated Pokemon grid and Pokemon detail page.
- Capture status update in detail page.
- Admin-only UI actions ( register and delete ) with role resolution in frontend.
- A classic Pokedex-themed visual redesign for navbar, list, cards, and detail screens.
- Frontend Docker support and local proxy strategy using relative `/api` endpoints.

- Relevant files: `README.md`
- Relevant files: `docs/phases/Phase2_Auth.md`
- Relevant files: `frontend/src/app/auth/`
- Relevant files: `frontend/src/app/core/services/auth.service.ts`
- Relevant files: `frontend/src/app/core/services/pokemon.service.ts`
- Relevant files: `frontend/src/app/features/pokemon/pokedex-grid/`
- Relevant files: `frontend/src/app/features/pokemon/pokemon-card/`
- Relevant files: `frontend/src/app/features/pokemon/pokemon-detail/`
- Existing patterns to follow: Angular standalone components, REST consumption via HttpClient, route-based navigation, and role-aware UI behavior for action visibility

## Tasks

### T1: Frontend Project Setup
**What:** Initialize a new Angular project in `frontend/` using Angular CLI, configure Tailwind CSS, and enable HttpClient for API communication.
**Files:** `frontend/`, `frontend/angular.json`, `frontend/tailwind.config.js`, `frontend/src/main.ts`
**Verify:** `cd frontend && ng serve`

### T2: Implement Authentication Components and Service
**What:** Create `LoginComponent` and `RegisterComponent`, plus an `AuthService` for `/api/auth` calls. Store JWT in `localStorage` after successful authentication.
**Files:** `frontend/src/app/auth/login/`, `frontend/src/app/auth/register/`, `frontend/src/app/core/services/auth.service.ts`
**Verify:** Manual check: register and log in successfully, then confirm JWT is present in browser storage

### T3: Create Protected Routes and Navigation
**What:** Add a main layout with `NavbarComponent`, implement `AuthGuard` for protected routes, redirect unauthenticated users to `/login`, and wire logout behavior.
**Files:** `frontend/src/app/core/guards/auth-guard.ts`, `frontend/src/app/core/components/navbar/`, `frontend/src/app/app.routes.ts`
**Verify:** Manual check: accessing `/` while unauthenticated redirects to `/login`; logout clears session and blocks protected routes

### T4: Build Pokedex Grid Component
**What:** Build `PokedexGridComponent` with paginated listing through `PokemonService`, using reusable `PokemonCardComponent` and previous/next pagination controls.
**Files:** `frontend/src/app/features/pokemon/pokedex-grid/`, `frontend/src/app/features/pokemon/pokemon-card/`, `frontend/src/app/core/services/pokemon.service.ts`
**Verify:** Manual check: grid loads after login and pagination retrieves next/previous pages correctly

### T5: Build Pokemon Detail Component
**What:** Create `PokemonDetailComponent` to display a single Pokemon and support captured status updates via `PokemonService`.
**Files:** `frontend/src/app/features/pokemon/pokemon-detail/`, `frontend/src/app/core/services/pokemon.service.ts`
**Verify:** Manual check: selecting a card opens the correct detail page and capture status updates persist

### T6: Implement Admin-Only UI
**What:** Conditionally render admin actions using `*ngIf` and role data from `AuthService`. Include fallback role-capability resolution when JWT does not contain explicit role claims.
**Files:** `frontend/src/app/features/pokemon/pokedex-grid/pokedex-grid.html`, `frontend/src/app/features/pokemon/pokedex-grid/pokedex-grid.ts`, `frontend/src/app/features/pokemon/pokemon-detail/pokemon-detail.html`, `frontend/src/app/features/pokemon/pokemon-detail/pokemon-detail.ts`, `frontend/src/app/core/services/auth.service.ts`
**Verify:** Manual check: `USER` cannot see admin-only buttons; `ADMIN` can see and use register/delete actions

### T7: Apply Pokedex Visual Redesign
**What:** Replace generic UI styling with a coherent classic Pokedex interface aesthetic across navbar, grid, cards, and detail views.
**Files:** `frontend/src/app/core/components/navbar/navbar.html`, `frontend/src/app/core/components/navbar/navbar.css`, `frontend/src/app/features/pokemon/pokedex-grid/pokedex-grid.css`, `frontend/src/app/features/pokemon/pokemon-card/pokemon-card.css`, `frontend/src/app/features/pokemon/pokemon-detail/pokemon-detail.css`
**Verify:** Manual check: pages render with Pokedex-style shell, screens, controls, and responsive behavior on mobile and desktop

## Validation

End-to-end validation after Phase 3 implementation:

- `docker-compose up --build -d`
- `cd frontend && npm install && ng serve`
- `cd frontend && npm run build`
- Manual check: register user, login, logout/login again, navigate grid pagination, open Pokemon detail, update capture status, verify admin-only actions with an admin account
- Manual check: verify responsive behavior on narrow and wide viewport sizes
- Manual check: admin register action creates new Pokemon and appears in listing after refresh

