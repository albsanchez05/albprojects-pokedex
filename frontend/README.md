<!-- File: frontend/README.md; Purpose: Provides project, phase, or operational documentation. -->

# Pokédex Frontend

Angular frontend for the Pokédex project. It consumes the local Spring Boot API and never calls PokeAPI directly.

## Prerequisites

- Node.js 20+
- npm 10+
- Backend API running on `http://localhost:8082`

## Local Development

Install dependencies:

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex\frontend"
npm install
```

Start the frontend development server:

```powershell
npm start
```

Access the application at `http://localhost:4200`.

## Build

```powershell
cd "C:\Users\aasanchez\OneDrive - Sopra Steria\Documents\JAVA\albprojects-pokedex\frontend"
npm run build
```

## What PO / QA Should Verify

### Shared checks

- Login works with valid credentials
- Pokédex list loads correctly
- Pokemon detail and capture flows work with the expected role restrictions

### Admin checks

- `+ Register Pokemon` button is visible
- Manual registration still works
- External search with `pikachu` or `25` prefills the register form
- External import stores the Pokemon in the local database
- Importing the same Pokemon twice shows a duplicate error message

### User checks

- Register/import panel is not visible
- Read and capture flows remain available
- Direct access to admin-only backend endpoints returns `403`

## Notes

- If the backend runs in a corporate network with HTTPS inspection, the backend JVM may require Windows trust store options as documented in the root `README.md`.
- The frontend relies on the reverse proxy configuration in `proxy.conf.json` during development and `nginx.conf` in Docker.
