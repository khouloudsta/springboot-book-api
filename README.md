# Spring Boot Book API

A backend REST API for managing books and authors developed using Java and Spring Boot.

## Features

- Create a new book
- Get all books
- Get a book by ID
- Update books
- - Validate book title
- Delete a book
- Manage authors
- Exception handling

## Technologies

* Java
* Spring Boot
* Maven
* REST API
* Spring Data JPA
* Git
* GitHub

## Project Structure

src
├── controller
├── service
├── repository
├── model
├── exception

## API Endpoints

### Books

GET /api/books
→ Get all books

GET /api/books/{id}
→ Get book by ID

POST /api/books
→ Create a new book

PUT /api/books/{id}
→ Update book

DELETE /api/books/{id}
→ Delete a book

## Run the project

Clone repository:

git clone <repository-url>

Run:

./mvnw spring-boot:run

Server:

http://localhost:8080

## Future Improvements

* Update endpoint (PUT)
* Validation
* DTO pattern
* PostgreSQL integration
* Swagger documentation
* Unit tests
