# Insurance Policy Management System

A secure REST API for managing insurance policies, built with **Spring Boot 3** and **Spring Security**. Users sign up, log in to receive a **JWT**, and manage their own policies; administrators can see every policy and every user. Access rules are enforced both at the URL level and on individual controller methods, and ownership is checked in the service layer so a user can never read or change someone else's policy.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4-brightgreen)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-blue)
![Database](https://img.shields.io/badge/DB-H2%20%7C%20PostgreSQL-lightgrey)

## Features

- **JWT authentication**: stateless login; every protected request carries a `Bearer` token
- **Role-based access control** with two roles, `USER` and `ADMIN`, enforced via `SecurityFilterChain` rules and `@PreAuthorize`
- **Ownership checks**: users can only view, update or delete policies they created; admins can view all
- **Password hashing** with BCrypt; password hashes are never returned in API responses
- **Meaningful HTTP status codes**: `401` for a missing or invalid token, `403` for insufficient rights, `404` for unknown policies
- **Zero-setup database**: runs on in-memory H2 by default, with a `postgres` profile for a real database
- **Unit tests** for controllers (MockMvc) and services (Mockito)

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.4 (Web, Data JPA, Validation) |
| Security | Spring Security 6, JJWT 0.11, BCrypt |
| Persistence | Hibernate / JPA, H2 (default), PostgreSQL (profile) |
| Testing | JUnit 5, Mockito, Spring MockMvc |
| Build | Maven (wrapper included) |

## Architecture

```mermaid
flowchart LR
    C[Client] -->|HTTP + Bearer JWT| F[JwtAuthenticationFilter]
    F --> S[SecurityFilterChain<br/>URL rules]
    S --> CT[Controllers<br/>@PreAuthorize]
    CT --> SV[Services<br/>ownership checks]
    SV --> R[Spring Data JPA<br/>repositories]
    R --> DB[(H2 / PostgreSQL)]
```

```
src/main/java/com/java/ipm
├── controller   # REST endpoints: auth, policies, users
├── service      # business logic and ownership checks
├── repository   # Spring Data JPA interfaces
├── entity       # JPA entities: User, InsurancePolicy
├── dto          # request/response objects for auth
├── domain       # Role enum
├── exception    # custom exceptions mapped to HTTP status codes
└── security     # JWT utility, JWT filter, security configuration
```

**Data model:** a `User` (username, email, BCrypt password, role) owns many `InsurancePolicy` records (name, type, premium amount).

## Getting started

**Prerequisites:** JDK 17 or newer. Maven is not required; use the included wrapper.

```bash
git clone https://github.com/sg07-gif/insurancepolicy-management-system.git
cd insurancepolicy-management-system
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```

The API starts on **http://localhost:8090**. The H2 console is at `/h2-console` (JDBC URL `jdbc:h2:mem:ipmdb`, user `sa`, empty password).

Run the tests with:

```bash
./mvnw test
```

### Using PostgreSQL

```bash
export DB_URL=jdbc:postgresql://localhost:5432/ipmdb
export DB_USERNAME=postgres
export DB_PASSWORD=postgres
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Configuration

| Variable | Default | Purpose |
|---|---|---|
| `JWT_SECRET` | development key | HMAC signing key, at least 32 characters. **Set this in any real deployment.** |
| `JWT_EXPIRATION_MS` | `86400000` (24 h) | Token lifetime |
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | local Postgres | Used only with the `postgres` profile |

## API reference

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Create an account |
| `POST` | `/api/auth/login` | Public | Log in and receive a JWT |
| `GET` | `/api/policies` | `ADMIN` | List all policies |
| `GET` | `/api/policies/{id}` | Owner or `ADMIN` | Get one policy |
| `POST` | `/api/policies` | `USER` | Create a policy for the logged-in user |
| `PUT` | `/api/policies/{id}` | `USER` (owner) | Update a policy |
| `DELETE` | `/api/policies/{id}` | `USER` (owner) | Delete a policy |
| `GET` | `/users` | `ADMIN` | List all users |

### Example session

```bash
# 1. Register a user
curl -X POST http://localhost:8090/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123","email":"john@example.com","role":"USER"}'

# 2. Log in and keep the token
TOKEN=$(curl -s -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"secret123"}' | sed 's/.*"token":"\([^"]*\)".*/\1/')

# 3. Create a policy
curl -X POST http://localhost:8090/api/policies \
  -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" \
  -d '{"policyName":"Family Health Plan","policyType":"Health","premiumAmount":5000}'

# 4. Read it back
curl http://localhost:8090/api/policies/1 -H "Authorization: Bearer $TOKEN"
```

Login response:

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

## Possible next steps

- Restrict self-registration to the `USER` role and add an admin-only endpoint for creating admins
- Add request validation (`@Valid`) and a uniform JSON error body
- Pagination and filtering on the policy list
- OpenAPI / Swagger UI documentation
- Docker Compose setup with PostgreSQL
