package com.example.demo;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class BookService {
    private final AuthorRepository authorRepo;
    private final BookRepository repo;

    public BookService(AuthorRepository authorRepo, BookRepository repo) {
        this.authorRepo = authorRepo;
        this.repo = repo; }

    public List<Book> getAll() { return repo.findAll(); }

    public Book getById(Long id) {
        return repo.findById(id).orElseThrow(() -> new NoSuchElementException("Book not found"));
    }

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

    public void delete(Long id) {
        Book book = getById(id);
        repo.delete(book);
    }

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

