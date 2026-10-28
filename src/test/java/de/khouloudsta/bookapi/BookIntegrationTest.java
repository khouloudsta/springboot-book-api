package de.khouloudsta.bookapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests book persistence, retrieval, title filtering, pagination, and sorting
 * through real HTTP requests.
 * Uses an isolated PostgreSQL container instead of the development database.
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
     * and can be retrieved using its generated identifier.
     */
    @Test
    void createAndGetBook_persistsBookInPostgres() {
        Map<String, String> request = Map.of(
                "title", "Integration Test Book"
        );

        ResponseEntity<Map> createResponse =
                restTemplate.postForEntity(
                        "/api/books",
                        request,
                        Map.class
                );

        assertEquals(HttpStatus.OK, createResponse.getStatusCode());

        Map<?, ?> createdBook = createResponse.getBody();
        assertNotNull(createdBook);
        assertEquals("Integration Test Book", createdBook.get("title"));
        assertNull(createdBook.get("authorName"));

        Number generatedId = (Number) createdBook.get("id");
        assertNotNull(generatedId);
        Long bookId = generatedId.longValue();

        Book persistedBook = bookRepository.findById(bookId)
                .orElseThrow();

        assertEquals("Integration Test Book", persistedBook.getTitle());
        assertNull(persistedBook.getAuthor());

        ResponseEntity<Map> getResponse =
                restTemplate.getForEntity(
                        "/api/books/" + bookId,
                        Map.class
                );

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

    /**
     * Verifies case-insensitive title filtering and sorted pagination.
     * Unique search text prevents interference from other test data.
     * Expected ordering follows the PostgreSQL database collation.
     */
    @Test
    void search_filtersTitlesAndReturnsSortedPages() {
        String marker = UUID.randomUUID().toString();

        bookRepository.save(new Book(marker + " CLEAN B", null));
        bookRepository.save(new Book(marker + " clean A", null));
        bookRepository.save(new Book(marker + " Other", null));

        String url =
                "/api/books?title={title}&page={page}&size=1&sort=title,asc";

        ResponseEntity<Map> firstResponse =
                restTemplate.getForEntity(
                        url,
                        Map.class,
                        marker + " clean",
                        0
                );

        assertEquals(HttpStatus.OK, firstResponse.getStatusCode());

        Map<?, ?> firstPage = firstResponse.getBody();
        assertNotNull(firstPage);
        assertEquals(
                2,
                ((Number) firstPage.get("totalElements")).intValue()
        );
        assertEquals(
                2,
                ((Number) firstPage.get("totalPages")).intValue()
        );
        assertEquals(0, ((Number) firstPage.get("number")).intValue());
        assertEquals(1, ((Number) firstPage.get("size")).intValue());

        List<?> firstContent = (List<?>) firstPage.get("content");
        assertNotNull(firstContent);
        assertEquals(1, firstContent.size());

        ResponseEntity<Map> secondResponse =
                restTemplate.getForEntity(
                        url,
                        Map.class,
                        marker + " clean",
                        1
                );

        assertEquals(HttpStatus.OK, secondResponse.getStatusCode());

        Map<?, ?> secondPage = secondResponse.getBody();
        assertNotNull(secondPage);
        assertEquals(1, ((Number) secondPage.get("number")).intValue());

        List<?> secondContent = (List<?>) secondPage.get("content");
        assertNotNull(secondContent);
        assertEquals(1, secondContent.size());

        List<String> expectedTitles = bookRepository
                .findAll(Sort.by(Sort.Direction.ASC, "title"))
                .stream()
                .map(Book::getTitle)
                .filter(title -> title.startsWith(marker))
                .filter(title -> title.toLowerCase(Locale.ROOT)
                        .contains("clean"))
                .toList();

        assertEquals(2, expectedTitles.size());
        assertEquals(
                expectedTitles.get(0),
                ((Map<?, ?>) firstContent.get(0)).get("title")
        );
        assertEquals(
                expectedTitles.get(1),
                ((Map<?, ?>) secondContent.get(0)).get("title")
        );
    }
}