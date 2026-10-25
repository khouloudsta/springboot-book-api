package com.example.demo;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.validation.Valid;

/**
 * Handles HTTP requests to create and list authors at /api/authors.
 * Validates request DTOs and maps persisted authors to response DTOs.
 * This controller currently accesses AuthorRepository directly.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorRepository repo;

    public AuthorController(AuthorRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<AuthorResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @PostMapping
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());

        Author savedAuthor = repo.save(author);
        return toResponse(savedAuthor);
    }

    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }
}
