# Phase 2 - Authentication & Authorization Evidence

## 1. Objective
Validate that authentication and role-based authorization are correctly enforced for Phase 2.

- Public endpoints are accessible without token.
- Protected endpoints reject missing/invalid tokens.
- `USER` and `ADMIN` permissions match business rules.

---

## 2. Test Context

- **Project:** Pokedex REST API
- **Phase:** 2 (Authentication)
- **Environment:** Local / QA / Staging (select one)
- **Base URL:** `http://localhost:8082`
- **Build version / commit:** `........................................`
- **Execution date:** `YYYY-MM-DD`
- **Executed by:** `........................................`

---

## 3. Users And Roles Used In Validation

| User | Role | Source | Expected Usage |
|---|---|---|---|
| `admin` | `ADMIN` | Seeded user / DB script | Backup admin for setup and recovery |
| `po-admin` | `ADMIN` | Flyway seed (`V8`) | PO UAT for admin-only flows |
| `qa-user` | `USER` | Flyway seed (`V8`) | QA validation for read and capture flows |
| _No user_ | Anonymous | No token | Should be blocked from protected resources |

Credentials are intentionally omitted from this evidence file.

---

## 4. Authorization Matrix (Expected)

| ID | Method | Endpoint | No Token | USER Token | ADMIN Token | Notes |
|---|---|---|---|---|---|---|
| A1 | POST | `/api/auth/register` | `200/201` | `200/201` | `200/201` | Public endpoint |
| A2 | POST | `/api/auth/login` | `200` | `200` | `200` | Public endpoint |
| A3 | GET | `/api/pokemons` | `401` | `200` | `200` | Read allowed for USER + ADMIN |
| A4 | GET | `/api/pokemons/{pokedexId}` | `401` | `200` | `200` | Read allowed for USER + ADMIN |
| A5 | POST | `/api/pokemons` | `401` | `403` | `200/201` | Write restricted to ADMIN |
| A6 | POST | `/api/pokemons/{id}` | `401` | `200` | `200` | Capture path; allowed for USER + ADMIN |
| A7 | DELETE | `/api/pokemons/{pokedexId}` | `401` | `403` | `204` | Delete restricted to ADMIN |
| A8 | GET | `/api/pokemons` with invalid token | `401` | `401` | `401` | Invalid JWT should be rejected |

---

## 5. Execution Evidence Log

> Fill one row per executed test.

| Run ID | Test ID | Request (method + path) | Token Type (none/invalid/USER/ADMIN) | Expected Status | Actual Status | Result (PASS/FAIL) | Evidence (screenshot/log ref) |
|---|---|---|---|---|---|---|---|
| 1 | A1 | `POST /api/auth/register` | none | 200/201 |  |  |  |
| 2 | A2 | `POST /api/auth/login` | none | 200 |  |  |  |
| 3 | A3 | `GET /api/pokemons` | none | 401 |  |  |  |
| 4 | A3 | `GET /api/pokemons` | USER | 200 |  |  |  |
| 5 | A5 | `POST /api/pokemons` | USER | 403 |  |  |  |
| 6 | A5 | `POST /api/pokemons` | ADMIN | 200/201 |  |  |  |
| 7 | A7 | `DELETE /api/pokemons/{id}` | USER | 403 |  |  |  |
| 8 | A7 | `DELETE /api/pokemons/{id}` | ADMIN | 204 |  |  |  |
| 9 | A8 | `GET /api/pokemons` | invalid | 401 |  |  |  |

---

## 6. Defect Log

| Defect ID | Related Test ID | Summary | Severity (Low/Med/High/Critical) | Status | Owner | Notes |
|---|---|---|---|---|---|---|
| D-001 |  |  |  | Open |  |  |

---

## 7. Acceptance Criteria Checklist

- [ ] Unauthenticated access to protected endpoints returns `401`.
- [ ] Invalid token is rejected with `401`.
- [ ] `USER` can access read endpoints.
- [ ] `USER` can access capture endpoint.
- [ ] `USER` is denied (`403`) on the other write endpoints.
- [ ] `ADMIN` can execute write endpoints.
- [ ] Public auth endpoints are reachable without token.
- [ ] No authorization regression observed in test cycle.

---

## 8. Sign-off

- **QA/Tester:** `........................................`
- **Tech Lead:** `........................................`
- **Product Owner:** `........................................`
- **Decision:** `GO / NO-GO`
- **Comments:**

```text
................................................................
................................................................
```

---

## 9. Credential Handling Runbook (QA/PO)

- Temporary plain passwords are shared only through the approved secret channel (vault/password manager/secure chat).
- Never store plain passwords in Git, README files, or evidence documents.
- QA and PO accounts are dedicated (`qa-user`, `po-admin`) to keep test evidence traceable.
- Rotate test passwords at least once per sprint and immediately after UAT sign-off.
- If credentials are exposed, rotate passwords and invalidate active sessions/tokens.
