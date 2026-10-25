package de.khouloudsta.bookapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Persistent book entity mapped to the books table.
 * Each book has a generated identifier, a non-blank title, and an optional author.
 * The author association is stored in the author_id foreign-key column.
 */
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    /** No-argument constructor required by JPA. */
    protected Book() {}

    public Book(String title, Author author) {
        this.title = title;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Author getAuthor() {
        return author;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
