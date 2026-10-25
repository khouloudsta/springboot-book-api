package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Provides Spring Data JPA persistence operations for Author entities
 * identified by Long values. Spring generates the implementation at runtime.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {}
