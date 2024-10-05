package org.library.libraryback;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.library.libraryback.controller.BookController;
import org.library.libraryback.dto.Book;
import org.library.libraryback.service.BookService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
public class BookControllerTests {

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("test")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }

    private MockMvc mockMvc;

    @InjectMocks
    private BookController bookController;

    @Mock
    private BookService bookService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    public void testCreateBook() throws Exception {
        Book book = new Book(); // Initialize with appropriate fields
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAuthor("Author Name");

        when(bookService.saveBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"Test Book\", \"author\": \"Author Name\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Test Book"));
    }

    @Test
    public void testGetAllBooks() throws Exception {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book One");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book Two");

        List<Book> books = Arrays.asList(book1, book2);
        when(bookService.findAllBook()).thenReturn(books);

        mockMvc.perform(get("/api/books"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
    }

    @Test
    public void testDeleteBook() throws Exception {
        long bookId = 1L;

        Mockito.doNothing().when(bookService).deleteBook(bookId);

        mockMvc.perform(delete("/api/{id}", bookId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testChangeReceivedStatus() throws Exception {
        long bookId = 1L;

        Mockito.doNothing().when(bookService).changeRecievedStatus(bookId);

        mockMvc.perform(patch("/api/{id}/receive", bookId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testReserveBook() throws Exception {
        long bookId = 1L;
        long userId = 1L;
        
        Mockito.doNothing().when(bookService).reserveBook(bookId, userId);

        mockMvc.perform(patch("/api/{id}/reserve?userId={userId}", bookId, userId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testCancelBookReservation() throws Exception {
        long bookId = 1L;

        Mockito.doNothing().when(bookService).cancelBookReservation(bookId);

        mockMvc.perform(patch("/api/{id}/cancel", bookId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testReturnBook() throws Exception {
        long bookId = 1L;

        Mockito.doNothing().when(bookService).returnBook(bookId);

        mockMvc.perform(patch("/api/{id}/return", bookId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}