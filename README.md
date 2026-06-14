# Cloud Storage API

A REST API for cloud file storage with session-based authentication, file management, and directory operations.

## Features

* User registration and authentication
* Session-based security
* File and directory upload
* File and directory download
* Resource search
* Move and rename resources
* Delete resources
* Directory management

## Tech Stack

* Java 25
* Spring Boot
* Spring Security
* Spring Session
* Redis
* Spring Data JPA
* PostgreSQL
* Liquibase
* MinIO
* OpenAPI / Swagger

## API Documentation

Swagger UI:

```text
/swagger-ui/index.html
```

OpenAPI specification:

```text
/v3/api-docs
```

## How to run

### Prod

Full environment via docker-compose.

1. Copy the example environment file and fill in your own values:
```text
cp .env.example .env
```
2. Start all services:
```text
docker compose -f docker-compose.prod.yml up -d
```
3. Open http://localhost (or your server IP).
4. To stop
```text
docker compose -f docker-compose.prod.yml down
```

### Dev
This runs only the infrastructure (frontend, PostgreSQL, Redis, MinIO) in Docker, 
with all credentials hardcoded for local development. The backend runs locally in your IDE.

1. Start containers
```text
docker compose -f docker-compose.dev.yml up -d
```
2. In your IDE, run the Spring Boot application with the `dev` profile active.
3. Open http://localhost
4. To stop
```text
docker compose -f docker-compose.dev.yml down
```