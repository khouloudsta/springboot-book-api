package com.example.demo;
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

    public Book create(Book book) {

        if (book.getAuthor() != null) {

            Author author =
                    authorRepo.findById(
                                    book.getAuthor().getId()
                            )
                            .orElseThrow(
                                    () -> new NoSuchElementException(
                                            "Author not found"
                                    )
                            );

            book.setAuthor(author);
        }

        return repo.save(book);
    }

    public void delete(Long id) { repo.deleteById(id); }

    public Book update(Long id, Book book) {
        Book existingBook = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        existingBook.setTitle(book.getTitle());
        if (book.getAuthor() != null) {
            Author author = authorRepo.findById(
                    book.getAuthor().getId()
            ).orElseThrow(
                    () -> new NoSuchElementException("Author not found")
            );

            existingBook.setAuthor(author);
        }

        return repo.save(existingBook);
    }
}

