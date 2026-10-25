package de.khouloudsta.bookapi;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides Spring Data JPA persistence operations for Book entities
 * identified by Long values. Spring generates the implementation at runtime.
 */
public interface BookRepository extends JpaRepository<Book, Long> {}
