package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void getById_returnsBook_whenBookExists() {
        // Vorbereitung
        Book book = new Book("Clean Code", null);
        book.setId(1L);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        // Ausführung
        Book result = bookService.getById(1L);

        // Prüfung
        assertSame(book, result);
    }

    @Test
    void getById_throwsException_whenBookDoesNotExist() {
        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> bookService.getById(999L)
        );

        assertEquals("Book not found", exception.getMessage());
    }

    @Test
    void create_doesNotSaveBook_whenAuthorDoesNotExist() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code");
        request.setAuthorId(999L);

        when(authorRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                NoSuchElementException.class,
                () -> bookService.create(request)
        );

        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void create_savesBookWithAuthor_whenAuthorExists() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code");
        request.setAuthorId(1L);

        Author author = new Author();
        author.setId(1L);
        author.setName("Robert C. Martin");

        when(authorRepository.findById(1L))
                .thenReturn(Optional.of(author));

        bookService.create(request);

        ArgumentCaptor<Book> captor =
                ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(captor.capture());

        Book savedBook = captor.getValue();

        assertEquals("Clean Code", savedBook.getTitle());
        assertSame(author, savedBook.getAuthor());
    }

    @Test
    void create_savesBookWithoutAuthor_whenAuthorIdIsMissing() {
        BookRequest request = new BookRequest();
        request.setTitle("Clean Code");

        bookService.create(request);

        ArgumentCaptor<Book> captor =
                ArgumentCaptor.forClass(Book.class);

        verify(bookRepository).save(captor.capture());

        Book savedBook = captor.getValue();

        assertEquals("Clean Code", savedBook.getTitle());
        assertNull(savedBook.getAuthor());

        verify(authorRepository, never()).findById(any());
    }
}