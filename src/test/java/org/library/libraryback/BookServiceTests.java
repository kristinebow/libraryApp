package org.library.libraryback;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.libraryback.dto.Book;
import org.library.libraryback.repository.BookRepository;
import org.library.libraryback.service.BookService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookServiceTests {

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

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        book = new Book();
        book.setId(1L);
        book.setBookedUntil(LocalDate.of(2024, 9, 22)); // Example date
    }

    @Test
    public void testChangeReceivedStatus_BookExists_BookedUntilNotNull() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.changeRecievedStatus(1L);

        assertTrue(book.getReceived());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testChangeReceivedStatus_BookExists_BookedUntilNull() {
        book.setBookedUntil(null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.changeRecievedStatus(1L);

        assertNull(book.getReceived());
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void testChangeReceivedStatus_BookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.changeRecievedStatus(1L);
        });

        assertEquals("Book not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testReserveBook_BookExists() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.reserveBook(1L);

        LocalDate expectedDate = LocalDate.now().plusWeeks(4);
        assertEquals(expectedDate, book.getBookedUntil());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testReserveBook_BookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.reserveBook(1L);
        });

        assertEquals("Book not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testCancelBookReservation_BookExists_BookedUntilNotNull() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.cancelBookReservation(1L);

        assertNull(book.getBookedUntil());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testCancelBookReservation_BookExists_BookedUntilNull() {
        book.setBookedUntil(null);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.cancelBookReservation(1L);

        assertNull(book.getBookedUntil()); // Should remain null
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void testCancelBookReservation_BookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.cancelBookReservation(1L);
        });

        assertEquals("Book not found with ID: 1", exception.getMessage());
    }

    @Test
    public void testReturnBook_BookExists_ReceivedTrue() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        book.setReceived(true);
        bookService.returnBook(1L);

        assertFalse(book.getReceived());
        assertNull(book.getBookedUntil());
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    public void testReturnBook_BookExists_ReceivedFalse() {
        book.setReceived(false);
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.returnBook(1L);

        assertFalse(book.getReceived()); // Should remain false
        assertNotNull(book.getBookedUntil()); // Should remain null
        verify(bookRepository, never()).save(book);
    }

    @Test
    public void testReturnBook_BookDoesNotExist() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NoSuchElementException.class, () -> {
            bookService.returnBook(1L);
        });

        assertEquals("Book not found with ID: 1", exception.getMessage());
    }
}