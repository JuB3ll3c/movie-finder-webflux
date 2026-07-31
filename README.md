# Movie Finder

A reactive REST API to browse a movie catalog, built with Spring WebFlux and R2DBC.

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- Spring WebFlux (reactive HTTP)
- Spring Data R2DBC (reactive database access) with H2 in-memory
- MapStruct (DTO mapping)
- Lombok
- JUnit 5, Mockito, Reactor Test, WebTestClient, JaCoCo (coverage)

## Project Structure

```
src/main/java/com/example/movie_finder/
├── controller/   REST endpoints (MovieController)
├── service/      Business logic (MovieService)
├── repository/   R2DBC reactive repositories
├── entity/       Domain entities (Movie, Actor, MovieActor, Genre)
├── model/        DTOs (MovieDto, ActorDto)
└── mapper/       MapStruct mappers
```

## API

| Method | URL           | Description                        |
|--------|---------------|------------------------------------|
| GET    | `/movies`     | Returns all movies with their actors |
| GET    | `/movies/{id}`| Returns a single movie or 404       |
| POST   | `/movies`     | Creates a movie, reusing or creating actors |

### Example

Create a movie:

```bash
curl -X POST http://localhost:8080/movies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Inception",
    "genre": "ACTION",
    "publicationDate": "2010-07-16",
    "actors": [{ "firstName": "Leonardo", "lastName": "DiCaprio" }]
  }'
```

## Getting Started

```bash
./mvnw spring-boot:run
```

The application starts with an in-memory H2 database seeded by `schema.sql` and `data.sql`. The H2 console is available at `/h2-console`.

## Tests

```bash
./mvnw test
```

Unit tests cover `MovieService` (with Mockito + StepVerifier) and `MovieController` (with `@WebFluxTest` + WebTestClient).

## Coverage

JaCoCo generates a coverage report during the `verify` phase:

```bash
./mvnw verify
```

Open `target/site/jacoco/index.html` to view the report.
