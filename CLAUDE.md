# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build
mvn clean package

# Run (development)
mvn spring-boot:run

# Run with production profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=BoxlyBackendApplicationTests

# Package without tests
mvn clean package -DskipTests
```

## Architecture

**Spring Boot 4.0.3** REST API for a lunch reservation management system. Java 17, PostgreSQL, Keycloak (OAuth2/JWT), MapStruct, Lombok.

**Layered structure:** `controllers/ → services/ → repository/ → entites/` with `dto/` for request/response shapes and `mapper/` for entity↔DTO conversion.

### Key Domain Concepts

- **MenuWeek** → has many **MenuWeekDay** → each day has many **MenuWeekDayOption** (food choices)
- **Employe** can create a **WeeklySubscription** (subscribes to a full week)
- **LunchReservation** — a specific employee reservation for a day/option
- **CancellationRequest** — cancellation of a reservation, tracked with status

Entities extend `Auditable` which injects `createdAt`/`updatedAt` via JPA auditing.

### Authentication

Keycloak (OpenID Connect) is the identity provider. The app runs as an OAuth2 Resource Server:
- `SecurityConfig` configures stateless JWT auth; currently all endpoints use `.permitAll()` at the HTTP level
- `JwtConverter` extracts roles from `resource_access.<client-id>.roles` in the JWT claim and converts them to Spring `GrantedAuthority`
- Method-level security (`@EnableMethodSecurity`) is enabled for role-based access on service/controller methods

### External Integration

**RHNA API** (`rhna.api.*` properties) — external HR system. `EmployeService` syncs employees and positions from it via `RestTemplate`. Properties are bound via `RhnaApiProperties` registered in `BoxlyBackendApplication`.

### Configuration Profiles

| Profile | Keycloak realm | Notes |
|---------|---------------|-------|
| default (dev) | `master` @ `192.168.12.85:8080` | local dev |
| `prod` | `cms` @ `192.168.1.160:8080` | production |

Database: PostgreSQL on `localhost:5432/boxly`, `ddl-auto=update` (schema auto-managed by Hibernate).

### Code Generation

Lombok and MapStruct use annotation processing at compile time. The `lombok-mapstruct-binding` dependency ensures correct processor ordering. When adding new mappers, annotate with `@Mapper(componentModel = "spring")`.
