# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run

Maven is not on PATH. Use the cached wrapper binary:

```bash
MVN="/c/Users/de_a_/.m2/wrapper/dists/apache-maven-3.9.10-bin/53h08a94dg6djh6umvruv7q564/apache-maven-3.9.10/bin/mvn"
JAVA_HOME="/c/Program Files/Java/jdk-17"
```

```bash
# Compile
JAVA_HOME="$JAVA_HOME" "$MVN" compile

# Run (creates ./data/vitalink.mv.db on first start)
JAVA_HOME="$JAVA_HOME" "$MVN" spring-boot:run

# Package JAR
JAVA_HOME="$JAVA_HOME" "$MVN" package -DskipTests

# Run tests
JAVA_HOME="$JAVA_HOME" "$MVN" test
```

## Architecture

**Spring Boot 3.3.5 / Java 17** REST API for the VitaLink Angular 19 frontend.

Base URL: `http://localhost:8080/api/v1`

### Layer flow

```
Controller → Service interface → ServiceImpl → Repository (JPA) → H2 file DB
```

### Key design decisions

- **No Spring Security / JWT.** Auth is handled by `AuthServiceImpl` with SHA-256 password hashing (`MessageDigest`). `securityAccount` is a SHA-256 derived token used as a public identifier for QR lookups.
- **H2 file database** at `./data/vitalink` (relative to the working directory when the app starts). `ddl-auto=update` so schema evolves automatically.
- **Profile fields are JSON in TEXT columns.** `PersonalData`, `MedicalData`, `List<Contact>`, and `ProfileVisibility` are serialized/deserialized via `AttributeConverter` implementations using Jackson's `ObjectMapper`. This avoids extra tables for nested structures.
- **`Profile.update()` is upsert.** `ProfileServiceImpl.update()` creates the profile if it doesn't exist for the given `accountId`, rather than throwing 404.
- **`Profile.getByAccountId()` returns null** (not 404) when no profile exists — intentional for the frontend to detect first-time profile setup.
- **CORS** allows `localhost:4200`, `localhost:4201`, `localhost:3000`.

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/auth/register` | Creates account, returns `Account` (201) |
| POST | `/api/v1/auth/login` | Validates credentials, returns `Account` (200) |
| GET | `/api/v1/accounts` | List all accounts |
| GET | `/api/v1/accounts/{id}` | Get by UUID |
| GET | `/api/v1/accounts/security/{securityAccount}` | Get by security token (used for QR scan) |
| PUT | `/api/v1/accounts/{id}` | Partial update (null fields ignored) |
| DELETE | `/api/v1/accounts/{id}` | Delete account |
| GET | `/api/v1/profiles/{accountId}` | Get profile (returns null body if not found) |
| POST | `/api/v1/profiles` | Create profile (409 if already exists) |
| PUT | `/api/v1/profiles/{accountId}` | Upsert profile |

### Exception mapping

| Exception | HTTP |
|-----------|------|
| `ResourceNotFoundException` | 404 |
| `ConflictException` | 409 |
| `BadCredentialsException` | 401 |
| `Exception` (fallback) | 500 |

## Database

- **H2 Console:** `http://localhost:8080/h2-console` (server must be running)
- **JDBC URL:** `jdbc:h2:file:./data/vitalink`
- **User:** `sa` / **Password:** *(empty)*
- Tables: `ACCOUNTS`, `PROFILES`

## Package structure

```
com.vitalink.backend
├── config/          CorsConfig
├── controller/      AuthController, AccountController, ProfileController
├── dto/             LoginRequest, RegisterRequest
├── entity/          Account, Profile, PersonalData, MedicalData, Contact, ProfileVisibility
│   └── converter/   AttributeConverters (JSON ↔ TEXT)
├── exception/       GlobalExceptionHandler, ResourceNotFoundException, ConflictException, BadCredentialsException
├── repository/      AccountRepository, ProfileRepository
└── service/
    ├── (interfaces) AuthService, AccountService, ProfileService
    └── impl/        AuthServiceImpl, AccountServiceImpl, ProfileServiceImpl
```
