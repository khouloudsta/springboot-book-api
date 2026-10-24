package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(BookController.class)
@Import(ApiExceptionHandler.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

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

    @Test
    void getById_returns404_whenBookDoesNotExist() throws Exception {
        when(bookService.getById(999L))
                .thenThrow(new NoSuchElementException("Book not found"));

        mockMvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Book not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

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
}