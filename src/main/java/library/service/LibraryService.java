package library.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import library.exception.BorrowingException;
import library.model.Book;
import library.model.SplayTreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Reader;
import java.util.List;

@Service
public class LibraryService {

    private Logger logger = LoggerFactory.getLogger(LibraryService.class);

    // In-memory representation of the available books, stored in splay trees
    private SplayTreeNode<Book> authorSplayTree = null;
    private SplayTreeNode<Book> isbnSplayTree = null;
    private SplayTreeNode<Book> borrowedSplayTree = null;

    // Autowired services
    private BookSplayTreeService bookSplayTreeService;
    private FileService fileService;

    @Autowired
    public LibraryService(BookSplayTreeService bookSplayTreeService, FileService fileService) {
        this.bookSplayTreeService = bookSplayTreeService;
        this.fileService = fileService;
        initialise();
    }

    private void initialise() {
        loadBaseLibraryFromFile(fileService.getBaseLibraryReader());
    }

    private void loadBaseLibraryFromFile(Reader reader) {
        CsvToBean<Book> csvToBean = new CsvToBeanBuilder<Book>(reader)
                .withType(Book.class)
                .withSeparator('\t')
                .withThrowExceptions(false)
                .build();
        List<Book> books = csvToBean.parse();
        handleBaseLibraryExceptions(csvToBean);

        for (Book book : books) {
            authorSplayTree = bookSplayTreeService.insertByAuthor(authorSplayTree, new SplayTreeNode<>(book));
            isbnSplayTree = bookSplayTreeService.insertByISBN(isbnSplayTree, new SplayTreeNode<>(book));
        }
    }

    private void handleBaseLibraryExceptions(CsvToBean<Book> csvToBean) {
        List<CsvException> capturedExceptions = csvToBean.getCapturedExceptions();
        for (CsvException exception : capturedExceptions) {
            logger.warn("Error reading base library. " + exception.getMessage());
        }
    }

    /**
     * Search the library for a given author name. Searches in unborrowed books only.
     * Modifies the author splay tree regardless whether the book was found or not. If the book was found, it will be
     * the new root of the tree. If the book was not found, a book close to its presumed position in the tree will be
     * the new root of the tree.
     *
     * @param authorName the full author name to search for
     * @return the node containing the book found, if one is found, or null if no book was found
     */
    public Book searchByAuthor(String authorName) {
        if (authorSplayTree == null) {
            return null;
        }

        Book mockBook = new Book(null, authorName, 0);
        authorSplayTree = bookSplayTreeService.searchByAuthor(authorSplayTree, mockBook);

        // Splay tree root will not be the right book if it is not found
        Book foundBook = authorSplayTree.data;
        return foundBook.getAuthor().equals(authorName) ? foundBook : null;
    }

    /**
     * Search the library for a given ISBN. Searches in unborrowed books only.
     * Modifies the ISBN splay tree regardless whether the book was found or not. If the book was found, it will be
     * the new root of the tree. If the book was not found, a book close to its presumed position in the tree will be
     * the new root of the tree.
     *
     * @param isbn the full ISBN to search for
     * @return the node containing the book found, if one is found, or null if no book was found
     */
    public Book searchByIsbn(Long isbn) {
        if (isbnSplayTree == null) {
            return null;
        }
        Book mockBook = new Book(null, null, isbn);
        isbnSplayTree = bookSplayTreeService.searchByIsbn(isbnSplayTree, mockBook);

        // Splay tree root will not be the right book if it is not found
        Book foundBook = isbnSplayTree.data;
        return foundBook.getIsbn() == isbn ? foundBook : null;
    }

    /**
     * Borrow a given book by removing it from both the author and ISBN trees, and adding it to the borrowed books tree.
     * Updates all three trees.
     *
     * @param book the book to borrow
     */
    public void borrowBook(Book book) {
        SplayTreeNode<Book> nodeInAuthorTree = bookSplayTreeService.searchByAuthor(authorSplayTree, book);
        if (!nodeInAuthorTree.data.getAuthor().equals(book.getAuthor())) {
            throw new BorrowingException("Oops! That book is not available to borrow.");
        }

        SplayTreeNode<Book> nodeInIsbnTree = bookSplayTreeService.searchByIsbn(isbnSplayTree, book);
        if (nodeInIsbnTree.data.getIsbn() != book.getIsbn()) {
            throw new BorrowingException("Oops! That book is not available to borrow.");
        }

        authorSplayTree = bookSplayTreeService.delete(authorSplayTree, nodeInAuthorTree);
        isbnSplayTree = bookSplayTreeService.delete(isbnSplayTree, nodeInIsbnTree);

        SplayTreeNode<Book> borrowedNode = new SplayTreeNode<>(book);
        borrowedSplayTree = bookSplayTreeService.insertByAuthor(borrowedSplayTree, borrowedNode);
    }

    /**
     * Searches the borrowed book tree to see if the supplied book corresponds to a borrowed book. If it does,
     * return the book by removing it from the borrowed books tree and adding it back to the author and ISBN trees.
     *
     * Note that the trees will be updated by searching for the author even if the
     * author is not found.
     *
     * @param book the book to return.
     */
    public void returnBook(Book book) {
        SplayTreeNode<Book> borrowedNode = searchBorrowedByAuthor(book.getAuthor());
        if (borrowedNode == null) {
            throw new BorrowingException("Oops! That book hasn't been borrowed, so can't be returned.");
        } else {
            borrowedSplayTree = bookSplayTreeService.delete(borrowedSplayTree, borrowedNode);

            SplayTreeNode<Book> authorNode = new SplayTreeNode<>(borrowedNode.data);
            authorSplayTree = bookSplayTreeService.insertByAuthor(authorSplayTree, authorNode);

            SplayTreeNode<Book> isbnNode = new SplayTreeNode<>(borrowedNode.data);
            isbnSplayTree = bookSplayTreeService.insertByISBN(isbnSplayTree, isbnNode);
        }
    }

    /**
     * Searches the tree of borrowed books for the given author name.
     * Modifies the borrowed splay tree regardless whether the book was found or not. If the book was found, it will be
     * the new root of the tree. If the book was not found, a book close to its presumed position in the tree will be
     * the new root of the tree.
     *
     * @param authorName the author name to search the borrowed books for
     * @return the node containing the book found, if one is found, or null if no book was found
     */
    private SplayTreeNode<Book> searchBorrowedByAuthor(String authorName) {
        if (borrowedSplayTree == null) {
            return null;
        }

        Book mockBook = new Book(null, authorName, 0);
        borrowedSplayTree = bookSplayTreeService.searchByAuthor(borrowedSplayTree, mockBook);

        // Splay tree root will not be the right book if it is not found
        Book foundBook = borrowedSplayTree.data;
        return foundBook.getAuthor().equals(authorName) ? borrowedSplayTree : null;
    }

    /**
     * For unit tests only
     * @return the root of the author splay tree
     */
    /*package*/ SplayTreeNode<Book> getAuthorSplayTree() {
        return authorSplayTree;
    }

    /**
     * For unit tests only
     * @return the root of the ISBN splay tree
     */
    /*package*/ SplayTreeNode<Book> getIsbnSplayTree() {
        return isbnSplayTree;
    }

    /**
     * For unit tests only
     * @return the root of the borrowed splay tree
     */
    /*package*/ SplayTreeNode<Book> getBorrowedSplayTree() {
        return borrowedSplayTree;
    }
}
