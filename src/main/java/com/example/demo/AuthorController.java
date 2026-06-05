package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {
    private final AuthorRepository repo;

    public AuthorController(AuthorRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Author> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Author create(@RequestBody Author author) {
        return repo.save(author);
    }
}
