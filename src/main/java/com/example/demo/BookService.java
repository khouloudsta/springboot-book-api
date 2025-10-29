package com.example.demo;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BookService {
    private final BookRepository repo;
    public BookService(BookRepository repo) { this.repo = repo; }

    public List<Book> getAll() { return repo.findAll(); }

    public Book getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    public Book create(Book book) { return repo.save(book); }

    public void delete(Long id) { repo.deleteById(id); }
}

