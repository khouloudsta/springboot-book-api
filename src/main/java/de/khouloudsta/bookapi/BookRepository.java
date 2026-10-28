package de.khouloudsta.bookapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides persistence operations and paginated title searches for books.
 * Spring Data JPA generates the implementation at runtime.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Finds books whose titles contain the given text, ignoring case.
     *
     * @param title the text to search for within book titles
     * @param pageable the requested page, page size, and sorting
     * @return matching books with pagination metadata
     */
    Page<Book> findByTitleContainingIgnoreCase(
            String title,
            Pageable pageable
    );
}