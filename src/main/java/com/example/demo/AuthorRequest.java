package com.example.demo;

import jakarta.validation.constraints.NotBlank;

/**
 * Input DTO for creating an author.
 * The name must be present and contain at least one non-whitespace character.
 */
public class AuthorRequest {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
