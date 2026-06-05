package com.example.demo;

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
    public List<Book> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Book getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Book create(@RequestBody Book book) {
        return service.create(book);
    }

    @PutMapping("/{id}")
    public Book update(
            @PathVariable Long id,
            @RequestBody Book book
    ) {
        return service.update(id, book);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
