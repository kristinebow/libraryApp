package org.library.libraryback.service;

import org.library.libraryback.dto.Book;
import org.library.libraryback.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookService {
    @Autowired
    public BookRepository bookRepository;

    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public List<Book> findAllBook() {
        return bookRepository.findAll();
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public void changeRecievedStatus(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getBookedUntil() != null) {
                book.setReceived(true);
                bookRepository.save(book); // Save the updated book
            }
        } else {
            throw new NoSuchElementException("Book not found with ID: " + id);
        }
    }

    public void reserveBook(Long id, Long userId) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            LocalDate currentDate = LocalDate.now();
            LocalDate dateIn4Weeks = currentDate.plusWeeks(4);
            book.setBookedUntil(dateIn4Weeks);
            book.setBookedByUserId(userId);
            bookRepository.save(book); // Save the updated book
        } else {
            throw new NoSuchElementException("Book not found with ID: " + id);
        }
    }

    public void cancelBookReservation(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getBookedUntil() != null) {
                book.setBookedUntil(null);
                book.setBookedByUserId(null);
                bookRepository.save(book); // Save the updated book
            }
        } else {
            throw new NoSuchElementException("Book not found with ID: " + id);
        }
    }

    public void returnBook(Long id) {
        Optional<Book> optionalBook = bookRepository.findById(id);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getReceived()) {
                book.setReceived(false);
                book.setBookedUntil(null);
                book.setBookedByUserId(null);
                bookRepository.save(book); // Save the updated book
            }
        } else {
            throw new NoSuchElementException("Book not found with ID: " + id);
        }
    }

    public Page<Book> searchBooks(String author, String title, int page, int size) {
        if ((author == null || author.isEmpty()) && (title == null || title.isEmpty())) {
            // Fetch all books if both parameters are empty
            return bookRepository.findAllSortedById(PageRequest.of(page, size));
        }
        return bookRepository.findByAuthorContainingIgnoreCaseOrTitleContainingIgnoreCaseOrderByIdAsc(author, title, PageRequest.of(page, size));
    }

}
