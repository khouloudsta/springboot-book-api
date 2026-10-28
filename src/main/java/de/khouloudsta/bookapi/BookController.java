package de.khouloudsta.bookapi;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

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

    /**
     * Retrieves books with optional title filtering, pagination, and sorting.
     *
     * @param title optional title fragment to search for, ignoring case
     * @param pageable pagination and sorting settings; defaults to 20 books
     *                 per page, sorted by identifier
     * @return matching book responses with pagination metadata
     */
    @Operation(
            summary = "Search and list books",
            description = "Returns a page of books, optionally filtered by title"
    )
    @GetMapping
    public Page<BookResponse> getAll(
            @RequestParam(required = false) String title,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        return service.search(title, pageable)
                .map(this::toResponse);
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
