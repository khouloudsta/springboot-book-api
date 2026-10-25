package de.khouloudsta.bookapi;

/**
 * Response DTO exposing a book identifier, title, and author name.
 * The author name is null when the book has no author.
 */
public class BookResponse {
    private Long id;
    private String title;
    private String authorName;

    public BookResponse(Long id, String title, String authorName) {
        this.id = id;
        this.title = title;
        this.authorName = authorName;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorName() {
        return authorName;
    }
}
