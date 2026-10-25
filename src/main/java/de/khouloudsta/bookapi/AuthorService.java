package de.khouloudsta.bookapi;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides operations for retrieving and creating authors.
 */
@Service
public class AuthorService {

    private final AuthorRepository repository;

    public AuthorService(AuthorRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all stored authors.
     *
     * @return all authors, or an empty list if none exist
     */
    public List<Author> getAll() {
        return repository.findAll();
    }

    /**
     * Creates and saves an author.
     *
     * @param request the author data validated by the controller
     * @return the saved author
     */
    public Author create(AuthorRequest request) {
        Author author = new Author();
        author.setName(request.getName());

        return repository.save(author);
    }
}