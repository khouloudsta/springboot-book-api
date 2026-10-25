package de.khouloudsta.bookapi;

import jakarta.validation.constraints.NotBlank;

/**
 * Input DTO for creating or updating a book.
 * The title must not be blank. A non-null authorId must reference an existing
 * author. Omitting authorId on update removes the current author association.
 */
public class BookRequest {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private Long authorId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}