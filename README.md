# Spring Boot Book API

[![Java CI](https://github.com/khouloudsta/springboot-book-api/actions/workflows/ci.yml/badge.svg)](https://github.com/khouloudsta/springboot-book-api/actions/workflows/ci.yml)

A REST API for managing books and authors, built with Java 21,
Spring Boot and PostgreSQL.

The project includes validated request DTOs, response DTOs,
centralized exception handling, automated tests, Docker Compose
and a GitHub Actions CI pipeline.

## Features

- Create, list, retrieve, update and delete books
- Create and list authors
- Associate books with existing authors
- Store books without an author
- Validate book titles and author names
- Return structured error responses
- Persist data in PostgreSQL using a Docker volume
- Run the application and database with Docker Compose
- Build and test automatically with GitHub Actions
- Explore the API through Swagger UI

## Technologies

- Java 21
- Spring Boot 3.5.6
- Spring Web and Bean Validation
- Spring Data JPA / Hibernate
- PostgreSQL 17
- Maven Wrapper
- JUnit 5, Mockito and MockMvc
- Docker and Docker Compose
- GitHub Actions
- Springdoc OpenAPI / Swagger UI

## Architecture

The application separates HTTP handling, business logic and
database access:

- Controllers handle HTTP requests and responses.
- BookService implements book-related business logic.
- Repositories provide database access through Spring Data JPA.
- Book and Author represent database entities.
- Request DTOs define accepted input.
- Response DTOs define returned data.
- ApiExceptionHandler converts exceptions into HTTP responses.

Java classes currently share the package `de.khouloudsta.bookapi`.

## Run with Docker Compose

### Requirements

- Git
- Docker with Docker Compose, for example Docker Desktop
- Port 8080 available

A local Java or Maven installation is not required for this option.
The Docker build compiles the application.

### 1. Clone the repository

```bash
git clone https://github.com/khouloudsta/springboot-book-api.git
cd springboot-book-api
```

### 2. Set the database password

PowerShell:

```powershell
$env:DB_PASSWORD = "bookpass"
```

Linux / macOS:

```bash
export DB_PASSWORD=bookpass
```

This is an example password for local development.
For an existing database volume, use the password configured
when that database was first initialized.

### 3. Build and start

```bash
docker compose up --build -d
```

Compose starts PostgreSQL, waits for its health check,
and then starts the application.

### 4. Access the application

- Books: http://localhost:8080/api/books
- Authors: http://localhost:8080/api/authors
- Swagger UI: http://localhost:8080/swagger
- OpenAPI JSON: http://localhost:8080/v3/api-docs
- Health: http://localhost:8080/actuator/health

### Useful commands

Check containers:

```bash
docker compose ps
```

Read application logs:

```bash
docker compose logs --tail=50 app
```

Stop containers:

```bash
docker compose stop
```

Start existing containers again:

```bash
docker compose up -d
```

Database data is stored in the named volume `book-api-pgdata`
and survives container restarts.

PostgreSQL is accessible to the application through the internal
Compose network. Its port is not published to the host.

## Configuration

| Variable | Default | Purpose |
|---|---|---|
| DB_URL | jdbc:postgresql://localhost:5432/bookdb | JDBC database URL |
| DB_USERNAME | bookuser | Database user |
| DB_PASSWORD | No default; required | Database password |

Docker Compose sets `DB_URL` to
`jdbc:postgresql://db:5432/bookdb`, where `db` is the database
service name.

Database tables are currently managed through Hibernate
with `spring.jpa.hibernate.ddl-auto=update`.

## API Endpoints

| Method | Endpoint | Description | Success |
|---|---|---|---|
| GET | /api/books | Search and list books with pagination and sorting | 200 |
| GET | /api/books/{id} | Retrieve a book | 200 |
| POST | /api/books | Create a book | 200 |
| PUT | /api/books/{id} | Update a book | 200 |
| DELETE | /api/books/{id} | Delete a book | 204 |
| GET | /api/authors | List authors | 200 |
| POST | /api/authors | Create an author | 200 |

### Search, pagination and sorting

```http
GET /api/books?title=clean&page=0&size=5&sort=title,asc
```

Title filtering is case-insensitive and matches partial titles.
Page numbering starts at 0. The default page size is 20,
with books sorted by ID.

The response contains books in `content` and pagination metadata.

### Create an author

```http
POST /api/authors
Content-Type: application/json

{
  "name": "Robert C. Martin"
}
```

Example response:

```json
{
  "id": 1,
  "name": "Robert C. Martin"
}
```

### Create a book with an author

Use the ID returned when creating the author.

```http
POST /api/books
Content-Type: application/json

{
  "title": "Clean Code",
  "authorId": 1
}
```

Example response:

```json
{
  "id": 1,
  "title": "Clean Code",
  "authorName": "Robert C. Martin"
}
```

### Create a book without an author

```http
POST /api/books
Content-Type: application/json

{
  "title": "Independent Book"
}
```

The response contains `"authorName": null`.

### Update a book

```http
PUT /api/books/1
Content-Type: application/json

{
  "title": "Clean Code Updated",
  "authorId": 1
}
```

For PUT requests, omitting `authorId` or setting it to `null`
removes the existing author association.

### Validation and errors

Book titles and author names must not be missing, empty
or contain only whitespace.

Example validation response:

```json
{
  "status": 400,
  "error": "Validation failed",
  "fields": {
    "title": "Title cannot be empty"
  }
}
```

| Status | Meaning |
|---|---|
| 400 | Validation failure or unreadable JSON / incompatible field type |
| 404 | Requested book or referenced author does not exist |
| 500 | Unexpected internal error |

Deleting an existing book returns 204 without a response body.
Deleting it again returns 404.

Additional manual requests are available in `test.http`.

## Automated Tests

The current suite contains:

- 11 BookService unit tests using JUnit and Mockito
- 4 BookController tests using MockMvc
- 2 BookIntegrationTest tests using Testcontainers and PostgreSQL
- 1 Spring Boot application context test


The integration test requires Docker and starts its own PostgreSQL
container. It does not use the developent database.

Service and controller tests do not require a running database.

Run them in PowerShell:

```powershell
.\mvnw.cmd "-Dtest=BookServiceTest,BookControllerTest" test
```

Linux / macOS:

```bash
bash ./mvnw "-Dtest=BookServiceTest,BookControllerTest" test
```

### Full build

The application context test requires a reachable PostgreSQL
database and the database environment variables.

Use a local JDK 21 and a separately accessible
PostgreSQL database, set `DB_URL`, `DB_USERNAME` and `DB_PASSWORD`,
then run:

```powershell
.\mvnw.cmd clean verify
```

On Linux / macOS:

```bash
bash ./mvnw --batch-mode clean verify
```

The Dockerfile skips test execution during image creation.
The GitHub Actions pipeline executes the complete test suite.

## Continuous Integration

The `Java CI` workflow runs on pushes and pull requests,
and can also be triggered manually.

It:

1. Starts a temporary PostgreSQL 17 database.
2. Checks out the repository.
3. Sets up Java 21.
4. Runs `clean verify` through the Maven Wrapper.

The CI database is separate from local development data.

## Planned Improvements

- Database migrations with Flyway
- Search, pagination and sorting
- Additional test coverage
- Authentication and authorization
- A frontend using TypeScript and React