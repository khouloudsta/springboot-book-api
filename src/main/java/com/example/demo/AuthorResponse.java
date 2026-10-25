package com.example.demo;

/**
 * Response DTO exposing an author identifier and name.
 * Excludes the entity books collection from the JSON response.
 */
public class AuthorResponse {

    private Long id;
    private String name;

    public AuthorResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
