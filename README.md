# Itsme Backend

Spring Boot 3.5 API server (MySQL, Redis, JWT with Firebase/Google login).

## Quickstart
### Docker (recommended)
```bash
docker compose up -d --build
```
- Port: `8080` (override with `SERVER_PORT`)
- Health: `GET /health`
- Swagger: `http://localhost:8080/swagger-ui/index.html`

### Local run
```bash
./gradlew bootRun
```
MySQL/Redis must be running (e.g., `docker compose up db redis`).

## Environment (.env.example)
- DB: `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `SPRING_DATASOURCE_URL`
- Redis: `REDIS_HOST`, `REDIS_PORT`
- JWT: `JWT_SECRET`, `JWT_ACCESS_EXPIRATION_MS`, `JWT_REFRESH_EXPIRATION_MS`
- Firebase: `FIREBASE_CREDENTIALS_BASE64` or `FIREBASE_CREDENTIALS_PATH`
- Google: `GOOGLE_CLIENT_ID`
- RateLimit: `RATE_LIMIT_ENABLED`, `RATE_LIMIT_REQUESTS`, `RATE_LIMIT_WINDOW_SECONDS`

## Sample Accounts (for testing/demo)
- ADMIN: `admin@example.com / adminpass`
- USER: `user1@example.com / password`

## Auth / RBAC
- Login: email/password (`POST /api/auth/login`), Firebase ID token (`/api/auth/firebase-login`), Google ID token (`/api/auth/google-login`)
- Refresh: `POST /api/auth/refresh`
- Roles
  - ADMIN: full management (users/stores/categories/menus/coupons/payments delete/create, etc.)
  - USER: own orders/carts/notifications/payments; public lists

## Endpoint & Role Summary (major)
| URL | Method | Description | Role |
| --- | --- | --- | --- |
| /health | GET | Health check | Public |
| /api/auth/login | POST | Email/password login | Public |
| /api/auth/refresh | POST | Refresh token | Public |
| /api/auth/firebase-login | POST | Firebase login | Public |
| /api/auth/google-login | POST | Google login | Public |
| /api/users | GET/POST | List/create users | ADMIN |
| /api/users/{id} | GET/PUT/DELETE | Get/update/delete user | USER (self) / ADMIN |
| /api/stores | GET | List stores | Public |
| /api/stores | POST/PUT/DELETE | Manage stores | ADMIN |
| /api/menu-categories | GET | List categories | Public |
| /api/menu-categories | POST/PUT/DELETE | Manage categories | ADMIN |
| /api/menus | GET | List/search menus (pagination) | Public |
| /api/menus | POST/PUT/DELETE | Manage menus | ADMIN |
| /api/orders | GET/POST | List/create orders | USER/ADMIN |
| /api/orders/{id}/status | PUT | Update order status | USER/ADMIN |
| /api/payments | GET/POST | Payments | USER/ADMIN |
| /api/coupons | GET | Coupons | USER/ADMIN |
| /api/coupons | POST/PUT/DELETE | Manage coupons | ADMIN |
| /api/notifications | GET/POST/DELETE | Notifications | USER/ADMIN |

## Global features
- Swagger/OpenAPI via springdoc; JWT bearer scheme and common error responses (400/401/403/404/422/429/500) auto-applied to `/api/**` (except auth)
- Rate limit: Redis-based, default 100 req / 60s (configurable)
- Error schema: `ApiErrorResponse { timestamp, path, status, code, message, details }`

## Postman
- Collection: `postman/itsme.postman_collection.json`
- Env vars: `baseUrl`, `userEmail`, `userPassword`, `accessToken`, `refreshToken`

## Migrations / Seed
- Flyway SQL: `migrations/V1__init.sql` (schema snapshot)
  - Run:  
    ```bash
    ./gradlew flywayMigrate \
      -Dflyway.url=jdbc:mysql://db:3306/itsme \
      -Dflyway.user=$MYSQL_USER \
      -Dflyway.password=$MYSQL_PASSWORD \
      -Dflyway.locations=filesystem:./migrations
    ```
- Seed data: `seed/seed.sql` (200+ rows; clears FKs then inserts users/stores/categories/menus/carts/orders/payments/notifications/coupons)
  - Apply: `mysql -h <host> -u <user> -p<pass> itsme < seed/seed.sql`

## Deployment (JCloud guide, manual)
1) Install Docker/Compose on server.
2) Copy `.env` (do not commit secrets).
3) `docker compose up -d --build` then `docker compose logs -f app`.
4) Open firewall/security group for `SERVER_PORT` (default 8080).
5) Health check: `curl http://<ip>:<port>/health` -> 200.
6) Swagger: `http://<ip>:<port>/swagger-ui/index.html`.
7) Postman: set `baseUrl` to `<ip>:<port>`.
8) Submission: capture screenshots (health 200, Swagger UI).

## Git history secret cleanup (BFG)
1) Backup repo, check out clean branch.
2) Run: `java -jar bfg.jar --replace-text replacements.txt .`
3) `git reflog expire --expire=now --all && git gc --prune=now --aggressive`
4) Force push: `git push --force`
5) Deliver real `.env` only via secure channel.

## Tests
- Run: `./gradlew test` (uses H2 + mocked Firebase, active profile `test`).
- Coverage: MockMvc integration/RBAC/auth flows; total 20+ test cases.
