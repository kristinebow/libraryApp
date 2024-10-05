package org.library.libraryback.controller;

import org.library.libraryback.dto.Book;
import org.library.libraryback.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BookController {
    @Autowired
    private BookService bookService;

    @PostMapping("/save")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        List<Book> allBooks = bookService.findAllBook();
        return new ResponseEntity<>(allBooks, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}/receive")
    public ResponseEntity<Void> changeReceivedStatus(@PathVariable Long id) {
        bookService.changeRecievedStatus(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reserve")
    public ResponseEntity<Void> reserveBook(@PathVariable Long id, @RequestParam Long userId) {
        bookService.reserveBook(id, userId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelBookReservation(@PathVariable Long id) {
        bookService.cancelBookReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<Void> returnBook(@PathVariable Long id) {
        bookService.returnBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Book>> searchBooks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Book> books = bookService.searchBooks(author, title, page, size);
        return ResponseEntity.ok(books);
    }
}
