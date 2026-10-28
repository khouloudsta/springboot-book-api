package de.khouloudsta.bookapi;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests book HTTP responses, request validation, and pagination.
 * The service is mocked, so these tests do not require a database.
 */
@WebMvcTest(BookController.class)
@Import(ApiExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    /**
     * Verifies that a blank title is rejected before calling the service.
     */
    @Test
    void create_returns400_whenTitleIsEmpty() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.title")
                        .value("Title cannot be empty"));

        verifyNoInteractions(bookService);
    }

    /**
     * Verifies that a missing book produces a structured HTTP 404 response.
     */
    @Test
    void getById_returns404_whenBookDoesNotExist() throws Exception {
        when(bookService.getById(999L))
                .thenThrow(new NoSuchElementException("Book not found"));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    /**
     * Verifies that an existing book is returned with its author name.
     */
    @Test
    void getById_returns200WithBook_whenBookExists() throws Exception {
        Author author = new Author();
        author.setId(1L);
        author.setName("Robert C. Martin");

        Book book = new Book("Clean Code", author);
        book.setId(10L);

        when(bookService.getById(10L)).thenReturn(book);

        mockMvc.perform(get("/api/books/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Clean Code"))
                .andExpect(jsonPath("$.authorName")
                        .value("Robert C. Martin"));
    }

    /**
     * Verifies that search text, pagination, and sorting reach the service,
     * and that the response contains book data and pagination metadata.
     */
    @Test
    void getAll_passesSearchAndPaginationToService() throws Exception {
        Book book = new Book("Clean Code", null);
        book.setId(10L);

        when(bookService.search(eq("clean"), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    Pageable pageable = invocation.getArgument(1);

                    return new PageImpl<>(
                            List.of(book),
                            pageable,
                            1
                    );
                });

        mockMvc.perform(get("/api/books")
                        .param("title", "clean")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Clean Code"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));

        ArgumentCaptor<Pageable> captor =
                ArgumentCaptor.forClass(Pageable.class);

        verify(bookService).search(eq("clean"), captor.capture());

        Pageable pageable = captor.getValue();

        assertEquals(0, pageable.getPageNumber());
        assertEquals(5, pageable.getPageSize());
        assertEquals(
                Sort.by(Sort.Direction.ASC, "title"),
                pageable.getSort()
        );
    }
}