package library.service;

import library.exception.BorrowingException;
import library.model.Book;
import library.model.SplayTreeNode;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class LibraryServiceTest {

    private MockFileService mockFileService;
    private BookSplayTreeService mockBookSplayTreeService = new BookSplayTreeService();

    @Before
    public void setUp() {
        mockFileService = new MockFileService();
    }

    @Test
    public void initialise_whenBaseLibraryEmpty_setsEmptyTrees() {
        // Given
        mockFileService.setBaseLibraryString("");

        // When
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        // Then
        SplayTreeNode<Book> authorRoot = libraryService.getAuthorSplayTree();
        assertThat(authorRoot, is(nullValue()));
        SplayTreeNode<Book> isbnRoot = libraryService.getIsbnSplayTree();
        assertThat(isbnRoot, is(nullValue()));
    }

    @Test
    public void initialise_setsBookFields() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Algorithm Design Manual\tSteven Skiena\t9781849967204");

        // When
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        // Then
        SplayTreeNode<Book> authorRoot = libraryService.getAuthorSplayTree();
        assertThat(authorRoot, is(not(nullValue())));
        Book book = authorRoot.data;
        assertThat(book.getTitle(), is(equalTo("The Algorithm Design Manual")));
        assertThat(book.getAuthor(), is(equalTo("Steven Skiena")));
        assertThat(book.getIsbn(), is(equalTo(9781849967204L)));
    }

    @Test
    public void initialise_assemblesAuthorTree() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");

        // When
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        // Then
        SplayTreeNode<Book> root = libraryService.getAuthorSplayTree();
        assertThat(root, is(not(nullValue())));
        assertThat(root.data.getAuthor(), is(equalTo("David Hare")));
        assertThat(root.parent, is(nullValue()));

        SplayTreeNode<Book> leftChild = root.left;
        assertThat(leftChild, is(not(nullValue())));
        assertThat(leftChild.data.getAuthor(), is(equalTo("Brian Christian")));
        assertThat(leftChild.parent, is(equalTo(root)));
        assertThat(leftChild.left, is(nullValue()));
        assertThat(leftChild.right, is(nullValue()));

        SplayTreeNode<Book> rightChild = root.right;
        assertThat(rightChild, is(not(nullValue())));
        assertThat(rightChild.data.getAuthor(), is(equalTo("Steven Skiena")));
        assertThat(rightChild.parent, is(equalTo(root)));
        assertThat(rightChild.left, is(nullValue()));
        assertThat(rightChild.right, is(nullValue()));
    }

    @Test
    public void initialise_assemblesIsbnTree() {
        // Given: christianIsbn < skienaIsbn < hareIsbn
        long skienaIsbn = 9781849967204L;
        long christianIsbn = 9781250118363L;
        long hareIsbn = 9783642272653L;

        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Algorithm Design Manual\tSteven Skiena\t" + skienaIsbn + "\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t" + christianIsbn + "\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t" + hareIsbn);

        // When
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        // Then
        SplayTreeNode<Book> root = libraryService.getIsbnSplayTree();
        // TODO migrate the rest of this file to Hamcrest matchers
        assertNotNull(root);
        assertEquals(hareIsbn, root.data.getIsbn());
        assertNull(root.parent);
        assertNull(root.right);

        SplayTreeNode<Book> leftChild = root.left;
        assertNotNull(leftChild);
        assertEquals(skienaIsbn, leftChild.data.getIsbn());
        assertSame(root, leftChild.parent);
        assertNull(leftChild.right);

        SplayTreeNode<Book> leftleftChild = leftChild.left;
        assertNotNull(leftleftChild);
        assertEquals(christianIsbn, leftleftChild.data.getIsbn());
        assertSame(leftChild, leftleftChild.parent);
        assertNull(leftleftChild.left);
        assertNull(leftleftChild.right);
    }

    @Test
    public void initialise_whenLineInvalid_skipsItAndReadsOthers() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Engineering: A Compiler\t2nd Edition\tKeith Cooper & Linda Torczon\t9780120884780\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");

        // When
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        // Then
        SplayTreeNode<Book> root = libraryService.getAuthorSplayTree();
        assertNotNull(root);
        assertEquals("David Hare", root.data.getAuthor());
        assertNull(root.parent);

        SplayTreeNode<Book> leftChild = root.left;
        assertNotNull(leftChild);
        assertEquals("Brian Christian", leftChild.data.getAuthor());
        assertSame(root, leftChild.parent);
        assertNull(leftChild.left);
        assertNull(leftChild.right);

        SplayTreeNode<Book> rightChild = root.right;
        assertNotNull(rightChild);
        assertEquals("Steven Skiena", rightChild.data.getAuthor());
        assertSame(root, rightChild.parent);
        assertNull(rightChild.left);
        assertNull(rightChild.right);
    }

    @Test
    public void searchByAuthor_whenTreeEmpty_returnsNull() {
        // Given
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        // When
        Book foundBook = libraryService.searchByAuthor("John Skeet");

        // Then
        assertNull(foundBook);
    }

    @Test
    public void searchByAuthor_whenSearchedNodeAtRoot_doesNotChangeTree() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Effective Engineer\tEdmond Lau\t9780996128100");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);
        String authorName = "Edmond Lau";

        // When
        SplayTreeNode<Book> previousAuthorTree = libraryService.getAuthorSplayTree();
        Book foundBook = libraryService.searchByAuthor(authorName);

        // Then
        assertNotNull(foundBook);
        assertEquals(foundBook.getAuthor(), authorName);
        SplayTreeNode<Book> newAuthorTree = libraryService.getAuthorSplayTree();
        assertEquals(newAuthorTree.data, foundBook);
        assertEquals(previousAuthorTree, newAuthorTree);
    }

    @Test
    public void searchByAuthor_whenSearchedNodeNotRoot_updatesTreeRoot() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        String authorName = "Brian Christian";

        // When
        Book foundBook = libraryService.searchByAuthor(authorName);

        // Then
        assertNotNull(foundBook);
        assertEquals(foundBook.getAuthor(), authorName);
        assertEquals(libraryService.getAuthorSplayTree().data, foundBook);
    }

    @Test
    public void searchByAuthor_whenSearchedNodeNotThere_updatesTreeRoot() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" + "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        String authorName = "Torben Mogensen";
        String closestAuthorName = "Steven Skiena";

        // When
        Book foundBook = libraryService.searchByAuthor(authorName);

        // Then
        assertNull(foundBook);
        assertEquals(libraryService.getAuthorSplayTree().data.getAuthor(), closestAuthorName);
    }

    @Test
    public void searchByIsbn_whenTreeEmpty_returnsNull() {
        // Given
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        // When
        Book foundBook = libraryService.searchByIsbn(9783319669656L);

        // Then
        assertNull(foundBook);
    }

    @Test
    public void searchByIsbn_whenSearchedNodeAtRoot_doesNotChangeTree() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Effective Engineer\tEdmond Lau\t9780996128100");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        long isbn = 9780996128100L;

        // When
        SplayTreeNode<Book> previousIsbnTree = libraryService.getIsbnSplayTree();
        Book foundBook = libraryService.searchByIsbn(isbn);

        // Then
        assertNotNull(foundBook);
        assertEquals(foundBook.getIsbn(), isbn);
        SplayTreeNode<Book> newIsbnTree = libraryService.getIsbnSplayTree();
        assertEquals(newIsbnTree.data, foundBook);
        assertEquals(previousIsbnTree, newIsbnTree);
    }

    @Test
    public void searchByIsbn_whenSearchedNodeNotRoot_updatesTreeRoot() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        long isbn = 9781250118363L;

        // When
        Book foundBook = libraryService.searchByIsbn(isbn);

        // Then
        assertNotNull(foundBook);
        assertEquals(foundBook.getIsbn(), isbn);
        assertEquals(libraryService.getIsbnSplayTree().data, foundBook);
    }

    @Test
    public void searchByIsbn_whenSearchedNodeNotThere_updatesTreeRoot() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" +
                "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        long isbn = 9783642272655L;
        long closestIsbn = 9783642272653L;

        // When
        Book foundBook = libraryService.searchByIsbn(isbn);

        // Then
        assertNull(foundBook);
        assertEquals(libraryService.getIsbnSplayTree().data.getIsbn(), closestIsbn);
    }

    @Test
    public void borrowBook_updatesAllThreeTrees() {
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" + "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        Book bookToBorrow = libraryService.getAuthorSplayTree().data;

        // When
        libraryService.borrowBook(bookToBorrow);

        // Then
        assertNotEquals(bookToBorrow.getAuthor(), libraryService.getAuthorSplayTree().data.getAuthor());
        assertNotEquals(bookToBorrow.getAuthor(), libraryService.getIsbnSplayTree().data.getAuthor());
        assertEquals(bookToBorrow.getAuthor(), libraryService.getBorrowedSplayTree().data.getAuthor());
    }

    @Test(expected = BorrowingException.class)
    public void borrowBook_whenBookNotBorrowed_throws() {
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" + "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        Book book = new Book("A book", "Bob Jones", 1248457854L);

        // When
        libraryService.borrowBook(book);

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = BorrowingException.class)
    public void returnBook_whenBookNotBorrowed_throws() {
        // Given
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" + "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        Book bookToReturn = libraryService.getAuthorSplayTree().data;

        // When
        libraryService.returnBook(bookToReturn);

        // Then
        fail("Should have thrown an exception");
    }


    @Test
    public void returnBook_whenBookFound_updatesAllThreeTrees() {
        mockFileService.setBaseLibraryString("Title\tAuthor\tISBN\n" + "The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        LibraryService libraryService = new LibraryService(mockBookSplayTreeService, mockFileService);

        Book bookToReturn = libraryService.getAuthorSplayTree().data;
        libraryService.borrowBook(bookToReturn);

        // When
        libraryService.returnBook(bookToReturn);

        // Then
        assertEquals(bookToReturn.getAuthor(), libraryService.getAuthorSplayTree().data.getAuthor());
        assertEquals(bookToReturn.getAuthor(), libraryService.getIsbnSplayTree().data.getAuthor());
        assertNull(libraryService.getBorrowedSplayTree());
    }
}