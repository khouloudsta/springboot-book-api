package de.khouloudsta.bookapi;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Handles HTTP requests for creating and listing authors.
 * Delegates operations to AuthorService and maps entities to response DTOs.
 */
@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    /**
     * Retrieves all authors.
     *
     * @return author response DTOs, or an empty list if none exist
     */
    @GetMapping
    public List<AuthorResponse> getAll() {
        return service.getAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Creates an author from a validated request.
     *
     * @param request the author name; must not be blank
     * @return the saved author's identifier and name
     */
    @PostMapping
    public AuthorResponse create(@Valid @RequestBody AuthorRequest request) {
        return toResponse(service.create(request));
    }

    /**
     * Converts an author entity into its API representation.
     *
     * @param author the author to convert
     * @return a response containing the identifier and name
     */
    private AuthorResponse toResponse(Author author) {
        return new AuthorResponse(
                author.getId(),
                author.getName()
        );
    }
}