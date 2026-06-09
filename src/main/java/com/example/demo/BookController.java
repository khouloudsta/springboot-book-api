package com.example.demo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/books")

public class BookController {
    private final BookService service;
    private final AuthorRepository authorRepo;

    public BookController(BookService service, AuthorRepository authorRepo) {
        this.service = service;
        this.authorRepo = authorRepo;
    }

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

    @PostMapping
    public Book create(@Valid @RequestBody Book book) {
        return service.create(book);
    }

    @PutMapping("/{id}")
    public BookResponse  update(
            @PathVariable Long id,
            @Valid @RequestBody Book book
    ) {
        Book updatedBook = service.update(id, book);
        return toResponse(updatedBook);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

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
