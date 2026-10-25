package de.khouloudsta.bookapi;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * Implements book retrieval, creation, update, and deletion.
 * Resolves optional author references before saving books and throws
 * NoSuchElementException when a requested book or referenced author is missing.
 */
@Service
public class BookService {
    private final AuthorRepository authorRepo;
    private final BookRepository repo;

    public BookService(AuthorRepository authorRepo, BookRepository repo) {
        this.authorRepo = authorRepo;
        this.repo = repo; }

    /**
     * Retrieves all stored books.
     * @return all books, or an empty list when none exist
     */
    public List<Book> getAll() { return repo.findAll(); }

    /**
     * Retrieves a book by identifier.
     * @param id identifier of the book
     * @return the matching book
     * @throws NoSuchElementException if the book does not exist
     */
    public Book getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    /**
     * Saves a new book after resolving its optional author reference.
     * Request validation is performed by the HTTP controller.
     * @param request title and optional author identifier
     * @return the saved book
     * @throws NoSuchElementException if the supplied author does not exist
     */
    public Book create(@Valid BookRequest request) {
        Author author = null;

        if (request.getAuthorId() != null) {
            author = authorRepo.findById(request.getAuthorId())
                    .orElseThrow(
                            () -> new NoSuchElementException("Author not found")
                    );
        }

        Book book = new Book(request.getTitle(), author);

        return repo.save(book);
    }

    /**
     * Deletes an existing book.
     * @param id identifier of the book to delete
     * @throws NoSuchElementException if the book does not exist
     */
    public void delete(Long id) {
        Book book = getById(id);
        repo.delete(book);
    }

    /**
     * Replaces the title and author association of an existing book.
     * A null author identifier clears the existing association.
     * @param id identifier of the book to update
     * @param request replacement title and optional author identifier
     * @return the saved book
     * @throws NoSuchElementException if the book or supplied author does not exist
     */
    public Book update(Long id, BookRequest request) {
        Book existingBook = repo.findById(id)
                .orElseThrow(
                        () -> new NoSuchElementException("Book not found")
                );

        Author author = null;

        if (request.getAuthorId() != null) {
            author = authorRepo.findById(request.getAuthorId())
                    .orElseThrow(
                            () -> new NoSuchElementException("Author not found")
                    );
        }

        existingBook.setTitle(request.getTitle());
        existingBook.setAuthor(author);

        return repo.save(existingBook);
    }
}
