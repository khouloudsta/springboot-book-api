package de.khouloudsta.bookapi;

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

/**
 * Tests book lookup and creation using mocked repositories.
 * Checks missing resources and creation with or without an author
 * without starting Spring or connecting to a database.
 */
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
        // Arrange
        Book book = new Book("Clean Code", null);
        book.setId(1L);

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        // Act
        Book result = bookService.getById(1L);

        // Assert
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
    /**
     * Verifies that updating a book replaces its title and author.
     */
    @Test
    void update_changesTitleAndAuthor_whenBothExist() {
        Author oldAuthor = new Author();
        oldAuthor.setId(1L);

        Author newAuthor = new Author();
        newAuthor.setId(2L);

        Book existingBook = new Book("Old title", oldAuthor);
        existingBook.setId(10L);

        BookRequest request = new BookRequest();
        request.setTitle("Updated title");
        request.setAuthorId(2L);

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(2L))
                .thenReturn(Optional.of(newAuthor));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.update(10L, request);

        assertEquals(10L, result.getId());
        assertEquals("Updated title", result.getTitle());
        assertSame(newAuthor, result.getAuthor());
        verify(bookRepository).save(existingBook);
    }

    /**
     * Verifies that updating a missing book fails without saving data.
     */
    @Test
    void update_throwsException_whenBookDoesNotExist() {
        BookRequest request = new BookRequest();
        request.setTitle("Updated title");

        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> bookService.update(999L, request)
        );

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    /**
     * Verifies that an existing book is passed to the repository for deletion.
     */
    @Test
    void delete_deletesBook_whenBookExists() {
        Book book = new Book("Clean Code", null);
        book.setId(10L);

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(book));

        bookService.delete(10L);

        verify(bookRepository).delete(book);
    }

    /**
     * Verifies that deleting a missing book fails without deleting data.
     */
    @Test
    void delete_throwsException_whenBookDoesNotExist() {
        when(bookRepository.findById(999L))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> bookService.delete(999L)
        );

        assertEquals("Book not found", exception.getMessage());
        verify(bookRepository, never()).delete(any(Book.class));
    }

    /**
     * Verifies that omitting the author identifier removes the existing author.
     */
    @Test
    void update_removesAuthor_whenAuthorIdIsMissing() {
        Author author = new Author();
        author.setId(1L);

        Book existingBook = new Book("Old title", author);
        existingBook.setId(10L);

        BookRequest request = new BookRequest();
        request.setTitle("Updated title");

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.update(10L, request);

        assertEquals("Updated title", result.getTitle());
        assertNull(result.getAuthor());
        verify(bookRepository).save(existingBook);
        verify(authorRepository, never()).findById(any());
    }

    /**
     * Verifies that an unknown author prevents an update
     * and leaves the existing book unchanged.
     */
    @Test
    void update_doesNotChangeBook_whenAuthorDoesNotExist() {
        Author originalAuthor = new Author();
        originalAuthor.setId(1L);

        Book existingBook = new Book("Original title", originalAuthor);
        existingBook.setId(10L);

        BookRequest request = new BookRequest();
        request.setTitle("Updated title");
        request.setAuthorId(999L);

        when(bookRepository.findById(10L))
                .thenReturn(Optional.of(existingBook));
        when(authorRepository.findById(999L))
                .thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> bookService.update(10L, request)
        );

        assertEquals("Author not found", exception.getMessage());
        assertEquals("Original title", existingBook.getTitle());
        assertSame(originalAuthor, existingBook.getAuthor());
        verify(bookRepository, never()).save(any(Book.class));
    }
}
