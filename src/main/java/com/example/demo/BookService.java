package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BookService {

    private final List<Book> books = new ArrayList<>();
    private Long nextId = 1L;

    public List<Book> getAll() {
        return books;
    }

    public Book getById(Long id) {
        return books.stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

    public Book create(Book book) {
        book.setId(nextId++);
        books.add(book);
        return book;
    }

    public void delete(Long id) {
        books.removeIf(b -> b.getId().equals(id));
    }
}
