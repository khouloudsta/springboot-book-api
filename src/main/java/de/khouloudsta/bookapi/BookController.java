package de.khouloudsta.bookapi;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.HttpStatus;

/**
 * Handles book CRUD requests at /api/books.
 * Validates incoming request DTOs, delegates operations to BookService,
 * and maps entities to response DTOs.
 */
@RestController
@RequestMapping("/api/books")

public class BookController {
    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get all books",
            description = "Returns a list of all books"
    )
    @GetMapping
    public List<BookResponse> getAll() {
        return service.getAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public BookResponse  getById(@PathVariable Long id) {

        Book book = service.getById(id);
        return toResponse(book);
    }

    @Operation(
            summary = "Create a book",
            description = "Creates a new book"
    )
    @PostMapping
    public BookResponse create(@Valid @RequestBody BookRequest request) {
        Book createdBook = service.create(request);
        return toResponse(createdBook);
    }

    @Operation(
            summary = "Update a book",
            description = "Updates a book by ID"
    )
    @PutMapping("/{id}")
    public BookResponse update(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        Book updatedBook = service.update(id, request);
        return toResponse(updatedBook);
    }

    @Operation(
            summary = "Delete a book",
            description = "Deletes a book by ID"
    )

    /** Deletes a book and returns HTTP 204 with no response body. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    /** Maps a book to its API representation, allowing a missing author. */
    private BookResponse toResponse(Book book) {
        String authorName = null;

        if (book.getAuthor() != null) {
            authorName = book.getAuthor().getName();
        }

        return new BookResponse(
                book.getId(),
                book.getTitle(),
                authorName
        );
    }
}
