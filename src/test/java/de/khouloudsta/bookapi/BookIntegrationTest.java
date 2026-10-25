package de.khouloudsta.bookapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests book creation and retrieval through real HTTP requests.
 * Uses a separate PostgreSQL container instead of the development database.
 */
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    /**
     * Verifies that a book created through the API is persisted in PostgreSQL
     * and can be retrieved through the API using its generated identifier.
     */
    @Test
    void createAndGetBook_persistsBookInPostgres() {
        // Arrange
        Map<String, String> request = Map.of(
                "title", "Integration Test Book"
        );

        // Act: create the book through HTTP.
        ResponseEntity<Map> createResponse =
                restTemplate.postForEntity(
                        "/api/books",
                        request,
                        Map.class
                );

        // Assert: creation succeeds and returns a generated identifier.
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Map<?, ?> createdBook = createResponse.getBody();
        assertNotNull(createdBook);
        assertEquals("Integration Test Book", createdBook.get("title"));
        assertNull(createdBook.get("authorName"));

        Number generatedId = (Number) createdBook.get("id");
        assertNotNull(generatedId);
        Long bookId = generatedId.longValue();

        // Assert: the book exists in the real test database.
        Book persistedBook = bookRepository.findById(bookId)
                .orElseThrow();

        assertEquals("Integration Test Book", persistedBook.getTitle());
        assertNull(persistedBook.getAuthor());

        // Act: retrieve the saved book through HTTP.
        ResponseEntity<Map> getResponse =
                restTemplate.getForEntity(
                        "/api/books/" + bookId,
                        Map.class
                );

        // Assert: the API returns the persisted book.
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());

        Map<?, ?> retrievedBook = getResponse.getBody();
        assertNotNull(retrievedBook);
        assertEquals(
                bookId.longValue(),
                ((Number) retrievedBook.get("id")).longValue()
        );
        assertEquals("Integration Test Book", retrievedBook.get("title"));
        assertNull(retrievedBook.get("authorName"));
    }
}