<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/PostgreSQL-17-336791?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/JWT-JJWT%200.13-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JWT" />
  <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
</p>

<h1 align="center">🔐 JWT Auth</h1>

<p align="center">
  Stateless JWT authentication service with user registration, login, and access/refresh token management.<br/>
  Supports two token delivery modes: HTTP-only cookies (v1) and JSON body (v2).
</p>

---

## Quick Start

```bash
cp example.env .env        # fill in your values
docker compose up -d       # start PostgreSQL
./gradlew bootRun          # run the app on :8080
```

### Environment Variables

```env
POSTGRES_DB=jwt_auth
POSTGRES_USER=postgres
POSTGRES_PASSWORD=secret
POSTGRES_HOST=localhost
JWT_SECRET_KEY=your_base64_secret
JWT_COOKIE_DOMAIN=localhost
```

---

## API Versioning

Endpoints are versioned via the `API-Version` header. Default is `1`.

```
API-Version: 2
```

---

## Endpoints

### `POST /api/users` <img src="https://img.shields.io/badge/v1-blue?style=flat-square" /> <img src="https://img.shields.io/badge/public-green?style=flat-square" />

Register a new user.

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"username": "john_doe", "password": "Str0ng!Pass"}'
```

```json
{
  "message": "Success",
  "data": { "id": "uuid", "username": "john_doe", "roles": ["USER"] }
}
```

| Field      | Rules                                                                    |
|------------|--------------------------------------------------------------------------|
| `username` | 3–16 chars, letters / numbers / `._-`                                    |
| `password` | 8–72 chars, must include uppercase, lowercase, digit, special char       |

---

### `POST /api/auth/tokens` <img src="https://img.shields.io/badge/v1-blue?style=flat-square" /> <img src="https://img.shields.io/badge/public-green?style=flat-square" />

Get tokens as **HTTP-only cookies**. Supports `PASSWORD` and `REFRESH_TOKEN` grant types.

```bash
# Login
curl -X POST http://localhost:8080/api/auth/tokens \
  -H "Content-Type: application/json" \
  -c cookies.txt \
  -d '{"grantType": "PASSWORD", "username": "john_doe", "password": "Str0ng!Pass"}'

# Refresh
curl -X POST http://localhost:8080/api/auth/tokens \
  -H "Content-Type: application/json" \
  -b cookies.txt -c cookies.txt \
  -d '{"grantType": "REFRESH_TOKEN"}'
```

---

### `POST /api/auth/tokens` <img src="https://img.shields.io/badge/v2-blue?style=flat-square" /> <img src="https://img.shields.io/badge/public-green?style=flat-square" />

Get tokens in the **response body**. Refresh token is sent via the `refresh` request header.

```bash
# Login
curl -X POST http://localhost:8080/api/auth/tokens \
  -H "Content-Type: application/json" \
  -H "API-Version: 2" \
  -d '{"grantType": "PASSWORD", "username": "john_doe", "password": "Str0ng!Pass"}'

# Refresh
curl -X POST http://localhost:8080/api/auth/tokens \
  -H "Content-Type: application/json" \
  -H "API-Version: 2" \
  -H "refresh: <your_refresh_token>" \
  -d '{"grantType": "REFRESH_TOKEN"}'
```

```json
{
  "message": "Success",
  "data": { "accessToken": "eyJ...", "refreshToken": "eyJ..." }
}
```

---

### `DELETE /api/auth/tokens/current` <img src="https://img.shields.io/badge/v1-blue?style=flat-square" /> <img src="https://img.shields.io/badge/public-green?style=flat-square" />

Logout — revokes the refresh token and clears cookies.

```bash
curl -X DELETE http://localhost:8080/api/auth/tokens/current -b cookies.txt
```

---

### `GET /api/users/me` <img src="https://img.shields.io/badge/v1-blue?style=flat-square" /> <img src="https://img.shields.io/badge/auth-required-red?style=flat-square" />

Get the authenticated user's profile.

```bash
curl http://localhost:8080/api/users/me -b cookies.txt
```

```json
{
  "message": "Success",
  "data": { "id": "uuid", "username": "john_doe", "roles": ["USER"] }
}
```

---

## Summary

| Method   | Path                       | Ver | Auth | Description        |
|----------|----------------------------|-----|------|--------------------|
| `POST`   | `/api/users`               | 1   | ❌   | Register user      |
| `POST`   | `/api/auth/tokens`         | 1   | ❌   | Tokens via cookies |
| `POST`   | `/api/auth/tokens`         | 2   | ❌   | Tokens via body    |
| `DELETE` | `/api/auth/tokens/current` | 1   | ❌   | Logout             |
| `GET`    | `/api/users/me`            | 1   | ✅   | Current user       |
