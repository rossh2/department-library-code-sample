package library.controller;

import library.model.Book;
import library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;

    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @RequestMapping("/searchByAuthor")
    public Book searchByAuthor(@RequestParam(value = "authorName") String authorName) {
        return libraryService.searchByAuthor(authorName);
    }

    @RequestMapping("/searchByISBN")
    public Book searchByIsbn(@RequestParam(value = "isbn") Long isbn) {
        return libraryService.searchByIsbn(isbn);
    }

    @PostMapping("/borrow")
    public void borrowBook(@RequestBody Book book) {
        libraryService.borrowBook(book);
    }

    @PostMapping("/return")
    public void returnBook(@RequestBody Book book) {
        libraryService.returnBook(book);
    }
}
