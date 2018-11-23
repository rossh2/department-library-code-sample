package pa2.testSuite;

import org.junit.Before;
import org.junit.Test;
import pa2.SPL_DIGITAL_LIB.Book;
import pa2.SPL_DIGITAL_LIB.SplayTreeDigitalLibrary;
import pa2.SPL_DIGITAL_LIB.SplayTreeNode;
import pa2.SPL_DIGITAL_LIB.SplayTreeUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import static org.junit.Assert.*;

public class SplayTreeDigitalLibraryTests {

    private final String[] STORED_TREE_PATHS = new String[]{SplayTreeDigitalLibrary.AUTHOR_TREE_PATH, SplayTreeDigitalLibrary.ISBN_TREE_PATH,
            SplayTreeDigitalLibrary.BORROWED_TREE_PATH};

    @Before
    public void clean() {
        for (String path : STORED_TREE_PATHS) {
            File file = new File(path);
            if (file.exists()) {
                boolean deleted = file.delete();
                assert deleted;
            }
        }
    }

    @Test
    public void loadBaseLibrary_whenBaseLibraryEmpty() {
        // Given
        Scanner baseLibraryScanner = new Scanner("");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // When
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        // Then
        SplayTreeNode<Book> authorRoot = digitalLibrary.authorSplayTree;
        assertNull(authorRoot);
    }

    @Test
    public void loadBaseLibrary_setsBookFields() {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // When
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        // Then
        SplayTreeNode<Book> authorRoot = digitalLibrary.authorSplayTree;
        assertNotNull(authorRoot);
        assertEquals("The Algorithm Design Manual", authorRoot.data.title);
        assertEquals("Steven Skiena", authorRoot.data.author);
        assertEquals(9781849967204L, authorRoot.data.ISBN);
    }

    @Test
    public void loadBaseLibrary_assemblesAuthorTree() {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // When
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        // Then
        SplayTreeNode<Book> root = digitalLibrary.authorSplayTree;
        assertNotNull(root);
        assertEquals("David Hare", root.data.author);
        assertNull(root.parent);

        SplayTreeNode<Book> leftChild = root.left;
        assertNotNull(leftChild);
        assertEquals("Brian Christian", leftChild.data.author);
        assertSame(root, leftChild.parent);
        assertNull(leftChild.left);
        assertNull(leftChild.right);

        SplayTreeNode<Book> rightChild = root.right;
        assertNotNull(rightChild);
        assertEquals("Steven Skiena", rightChild.data.author);
        assertSame(root, rightChild.parent);
        assertNull(rightChild.left);
        assertNull(rightChild.right);
    }

    @Test
    public void loadBaseLibrary_assemblesIsbnTree() {
        // Given: christianIsbn < skienaIsbn < hareIsbn
        long skienaIsbn = 9781849967204L;
        long christianIsbn = 9781250118363L;
        long hareIsbn = 9783642272653L;

        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t" + skienaIsbn + "\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t" + christianIsbn + "\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t" + hareIsbn);
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // When
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        // Then
        SplayTreeNode<Book> root = digitalLibrary.isbnSplayTree;
        assertNotNull(root);
        assertEquals(hareIsbn, root.data.ISBN);
        assertNull(root.parent);
        assertNull(root.right);

        SplayTreeNode<Book> leftChild = root.left;
        assertNotNull(leftChild);
        assertEquals(skienaIsbn, leftChild.data.ISBN);
        assertSame(root, leftChild.parent);
        assertNull(leftChild.right);

        SplayTreeNode<Book> leftleftChild = leftChild.left;
        assertNotNull(leftleftChild);
        assertEquals(christianIsbn, leftleftChild.data.ISBN);
        assertSame(leftChild, leftleftChild.parent);
        assertNull(leftleftChild.left);
        assertNull(leftleftChild.right);
    }

    @Test
    public void loadBaseLibrary_whenLineInvalid_skipsItAndReadsOthers() {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Engineering: A Compiler, 2nd Edition, Keith Cooper & Linda Torczon\t9780120884780\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // When
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        // Then
        SplayTreeNode<Book> root = digitalLibrary.authorSplayTree;
        assertNotNull(root);
        assertEquals("David Hare", root.data.author);
        assertNull(root.parent);

        SplayTreeNode<Book> leftChild = root.left;
        assertNotNull(leftChild);
        assertEquals("Brian Christian", leftChild.data.author);
        assertSame(root, leftChild.parent);
        assertNull(leftChild.left);
        assertNull(leftChild.right);

        SplayTreeNode<Book> rightChild = root.right;
        assertNotNull(rightChild);
        assertEquals("Steven Skiena", rightChild.data.author);
        assertSame(root, rightChild.parent);
        assertNull(rightChild.left);
        assertNull(rightChild.right);
    }

    @Test
    public void library_whenLoaded_savesTrees() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"exit"};

        // When
        digitalLibrary.main(args);

        // Then
        for (String path : STORED_TREE_PATHS) {
            File file = new File(path);
            assertTrue(file.exists());
        }
    }

    @Test
    public void library_whenLoaded_readsSavedTrees() throws IOException {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);
        digitalLibrary.saveTrees();

        SplayTreeDigitalLibrary digitalLibrary2 = new SplayTreeDigitalLibrary();
        String[] args = {"exit"};

        // When
        digitalLibrary2.main(args);

        // Then
        SplayTreeNode<Book> root = digitalLibrary2.authorSplayTree;
        assertNotNull(root);
        assertEquals("David Hare", root.data.author);
        assertNull(root.parent);

        SplayTreeNode<Book> leftChild = root.left;
        assertNotNull(leftChild);
        assertEquals("Brian Christian", leftChild.data.author);
        assertSame(root, leftChild.parent);
        assertNull(leftChild.left);
        assertNull(leftChild.right);

        SplayTreeNode<Book> rightChild = root.right;
        assertNotNull(rightChild);
        assertEquals("Steven Skiena", rightChild.data.author);
        assertSame(root, rightChild.parent);
        assertNull(rightChild.left);
        assertNull(rightChild.right);
    }

    @Test
    public void nodeToString_whenNoChildren_writesDataAndNulls() {
        // Given
        SplayTreeNode<Book> node = new SplayTreeNode<>(new Book("A Book", "Antonella", 123456789));

        // When
        String nodeString = SplayTreeDigitalLibrary.nodeToString(node);

        // Then
        assertEquals("{data: {title: \"A Book\", author: \"Antonella\", ISBN: 123456789}, left: {}, right: {}}", nodeString);
    }

    @Test
    public void nodeToString_whenChildren_recurses() {
        // Given
        SplayTreeNode<Book> node1 = new SplayTreeNode<>(new Book("A CL Book", "James", 123456789));
        SplayTreeNode<Book> node2 = new SplayTreeNode<>(new Book("A CS Book", "Antonella", 123456790));
        SplayTreeNode<Book> node3 = new SplayTreeNode<>(new Book("A LING Book", "Lotus", 123456791));
        node1.left = node2;
        node2.parent = node1;
        node1.right = node3;
        node3.parent = node1;

        // When
        String nodeString = SplayTreeDigitalLibrary.nodeToString(node1);

        // Then
        assertEquals("{data: " +
                        "{title: \"A CL Book\", author: \"James\", ISBN: 123456789}, " +
                        "left: {data: " +
                        "{title: \"A CS Book\", author: \"Antonella\", ISBN: 123456790}, " +
                        "left: {}, right: {}}, " +
                        "right: {data: " +
                        "{title: \"A LING Book\", author: \"Lotus\", ISBN: 123456791}, " +
                        "left: {}, right: {}}" +
                        "}",
                nodeString);
    }

    @Test
    public void bookToString_writesFields() {
        // Given
        String title = "A CS Book";
        String author = "Antonella";
        long isbn = 12345678901L;
        Book book = new Book(title, author, isbn);

        // When
        String bookString = SplayTreeDigitalLibrary.bookToString(book);

        // Then
        String expectedString = "{title: \"A CS Book\", author: \"Antonella\", ISBN: 12345678901}";
        assertEquals(expectedString, bookString);
    }

    @Test
    public void bookToString_whenNull_returnsEmptyObject() {
        // Given
        Book book = null;

        // When
        String bookString = SplayTreeDigitalLibrary.bookToString(book);

        // Then
        String expectedString = "{}";
        assertEquals(expectedString, bookString);
    }

    @Test
    public void stringToBook_readsFields() throws IOException {
        // Given
        String bookString = "{title: \"A CS Book\", author: \"Antonella\", ISBN: 12345678901}";

        // When
        Book parsedBook = SplayTreeDigitalLibrary.stringToBook(new StringReader(bookString));

        // Then
        assertEquals("A CS Book", parsedBook.title);
        assertEquals("Antonella", parsedBook.author);
        assertEquals(12345678901L, parsedBook.ISBN);
    }

    @Test
    public void stringToBook_whenEmptyObject_returnsNull() throws IOException {
        // Given
        String bookString = "{}";

        // When
        Book parsedBook = SplayTreeDigitalLibrary.stringToBook(new StringReader(bookString));

        // Then
        assertNull(parsedBook);
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToBook_whenEmpty_throws() throws IOException {
        // Given
        String bookString = "";

        // When
        SplayTreeDigitalLibrary.stringToBook(new StringReader(bookString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToBook_whenNotClosed_throws() throws IOException {
        // Given
        String bookString = "{title: \"A CS Book\", author: \"Antonella\", ISBN: 12345678901";

        // When
        SplayTreeDigitalLibrary.stringToBook(new StringReader(bookString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToBook_whenTitleMissing_throws() throws IOException {
        // Given
        String bookString = "{author: \"Antonella\", ISBN: 12345678901}";

        // When
        SplayTreeDigitalLibrary.stringToBook(new StringReader(bookString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToBook_whenAuthorMissing_throws() throws IOException {
        // Given
        String bookString = "{title: \"A CS Book\", author: \"Antonella\"}";

        // When
        SplayTreeDigitalLibrary.stringToBook(new StringReader(bookString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToBook_whenISBNMissing_throws() throws IOException {
        // Given
        String bookString = "{title: \"A CS Book\", ISBN: 12345678901}";

        // When
        SplayTreeDigitalLibrary.stringToBook(new StringReader(bookString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test
    public void stringToNode_whenNoChildren_parsesSameNode() throws IOException {
        // Given
        SplayTreeNode<Book> node = new SplayTreeNode<>(new Book("A Book", "Antonella", 123456789));
        String nodeString = SplayTreeDigitalLibrary.nodeToString(node);

        // When
        SplayTreeNode<Book> parsedNode = SplayTreeDigitalLibrary.stringToNode(new StringReader(nodeString));

        // Then
        assertNull(parsedNode.left);
        assertNull(parsedNode.right);
        assertNull(parsedNode.parent);

        Book parsedBook = parsedNode.data;
        assertNotNull(parsedBook);
        assertEquals(node.data.title, parsedBook.title);
        assertEquals(node.data.author, parsedBook.author);
        assertEquals(node.data.ISBN, parsedBook.ISBN);
    }

    @Test
    public void stringToNode_whenChildren_parsesSameNodes() throws IOException {
        // Given
        SplayTreeNode<Book> node1 = new SplayTreeNode<>(new Book("A CL Book", "James", 123456789));
        SplayTreeNode<Book> node2 = new SplayTreeNode<>(new Book("A CS Book", "Antonella", 123456790));
        SplayTreeNode<Book> node3 = new SplayTreeNode<>(new Book("A LING Book", "Lotus", 123456791));
        node1.left = node2;
        node2.parent = node1;
        node1.right = node3;
        node3.parent = node1;

        String nodeString = SplayTreeDigitalLibrary.nodeToString(node1);

        // When
        SplayTreeNode<Book> parsedNode = SplayTreeDigitalLibrary.stringToNode(new StringReader(nodeString));

        // Then
        assertNull(parsedNode.parent);

        Book parsedBook = parsedNode.data;
        assertNotNull(parsedBook);
        assertEquals(node1.data.title, parsedBook.title);
        assertEquals(node1.data.author, parsedBook.author);
        assertEquals(node1.data.ISBN, parsedBook.ISBN);

        SplayTreeNode<Book> parsedLeft = parsedNode.left;
        assertNotNull(parsedLeft);
        assertEquals(parsedNode, parsedLeft.parent);
        Book parsedLeftBook = parsedLeft.data;
        assertNotNull(parsedLeftBook);
        assertEquals(node2.data.title, parsedLeftBook.title);
        assertEquals(node2.data.author, parsedLeftBook.author);
        assertEquals(node2.data.ISBN, parsedLeftBook.ISBN);

        SplayTreeNode<Book> parsedRight = parsedNode.right;
        assertNotNull(parsedRight);
        assertEquals(parsedNode, parsedRight.parent);
        Book parsedRightBook = parsedRight.data;
        assertNotNull(parsedRightBook);
        assertEquals(node3.data.title, parsedRightBook.title);
        assertEquals(node3.data.author, parsedRightBook.author);
        assertEquals(node3.data.ISBN, parsedRightBook.ISBN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToNode_whenEmpty_throws() throws IOException {
        // Given
        String nodeString = "";

        // When
        SplayTreeDigitalLibrary.stringToNode(new StringReader(nodeString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToNode_whenNotClosed_throws() throws IOException {
        // Given
        String nodeString = "{data: {title: \"A Book\", author: \"Antonella\", ISBN: 123456789}, left: {}, right: {}";

        // When
        SplayTreeDigitalLibrary.stringToNode(new StringReader(nodeString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToNode_whenDataMissing_throws() throws IOException {
        // Given
        String nodeString = "{left: {}, right: {}}";

        // When
        SplayTreeDigitalLibrary.stringToNode(new StringReader(nodeString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToNode_whenLeftMissing_throws() throws IOException {
        // Given
        String nodeString = "{data: {title: \"A Book\", author: \"Antonella\", ISBN: 123456789}, right: {}}";

        // When
        SplayTreeDigitalLibrary.stringToNode(new StringReader(nodeString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringToNode_whenRightMissing_throws() throws IOException {
        // Given
        String nodeString = "{data: {title: \"A Book\", author: \"Antonella\", ISBN: 123456789}, left: {}}";

        // When
        SplayTreeDigitalLibrary.stringToNode(new StringReader(nodeString));

        // Then
        fail("Should have thrown an exception");
    }

    @Test
    public void authorSearch_whenTreeEmpty_returnsNull() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // When
        SplayTreeNode<Book> foundNode = digitalLibrary.authorSearch("John Skeet");

        // Then
        assertNull(foundNode);
        assertNull(digitalLibrary.authorSplayTree);
    }

    @Test
    public void authorSearch_whenSearchedNodeAtRoot_doesNotChangeTree() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(new Scanner("The Effective Engineer\tEdmond Lau\t9780996128100"));
        String authorName = "Edmond Lau";

        // When
        SplayTreeNode<Book> previousAuthorTree = digitalLibrary.authorSplayTree;
        SplayTreeNode<Book> foundNode = digitalLibrary.authorSearch(authorName);

        // Then
        assertNotNull(foundNode);
        assertEquals(foundNode.data.author, authorName);
        assertEquals(digitalLibrary.authorSplayTree, foundNode);
        assertEquals(previousAuthorTree, digitalLibrary.authorSplayTree);
    }

    @Test
    public void authorSearch_whenSearchedNodeNotRoot_updatesTreeRoot() {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);
        String authorName = "Brian Christian";

        // When
        SplayTreeNode<Book> foundNode = digitalLibrary.authorSearch(authorName);

        // Then
        assertNotNull(foundNode);
        assertEquals(foundNode.data.author, authorName);
        assertEquals(digitalLibrary.authorSplayTree, foundNode);
    }

    @Test
    public void authorSearch_whenSearchedNodeNotThere_updatesTreeRoot() {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);
        String authorName = "Torben Mogensen";
        String closestAuthorName = "Steven Skiena";

        // When
        SplayTreeNode<Book> foundNode = digitalLibrary.authorSearch(authorName);

        // Then
        assertNull(foundNode);
        assertEquals(digitalLibrary.authorSplayTree.data.author, closestAuthorName);
    }

    @Test
    public void isbnSearch_whenTreeEmpty_returnsNull() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // When
        SplayTreeNode<Book> foundNode = digitalLibrary.isbnSearch(9783319669656L);

        // Then
        assertNull(foundNode);
        assertNull(digitalLibrary.isbnSplayTree);
    }

    @Test
    public void isbnSearch_whenSearchedNodeAtRoot_doesNotChangeTree() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(new Scanner("The Effective Engineer\tEdmond Lau\t9780996128100"));
        long isbn = 9780996128100L;

        // When
        SplayTreeNode<Book> previousIsbnTree = digitalLibrary.isbnSplayTree;
        SplayTreeNode<Book> foundNode = digitalLibrary.isbnSearch(isbn);

        // Then
        assertNotNull(foundNode);
        assertEquals(foundNode.data.ISBN, isbn);
        assertEquals(digitalLibrary.isbnSplayTree, foundNode);
        assertEquals(previousIsbnTree, digitalLibrary.isbnSplayTree);
    }

    @Test
    public void isbnSearch_whenSearchedNodeNotRoot_updatesTreeRoot() {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);
        long isbn = 9781250118363L;

        // When
        SplayTreeNode<Book> foundNode = digitalLibrary.isbnSearch(isbn);

        // Then
        assertNotNull(foundNode);
        assertEquals(foundNode.data.ISBN, isbn);
        assertEquals(digitalLibrary.isbnSplayTree, foundNode);
    }

    @Test
    public void isbnSearch_whenSearchedNodeNotThere_updatesTreeRoot() {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);
        long isbn = 9783642272655L;
        long closestIsbn = 9783642272653L;

        // When
        SplayTreeNode<Book> foundNode = digitalLibrary.isbnSearch(isbn);

        // Then
        assertNull(foundNode);
        assertEquals(digitalLibrary.isbnSplayTree.data.ISBN, closestIsbn);
    }

    @Test
    public void borrowBook_whenBookInAuthorTree_updatesAllThreeTrees() {
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        String authorName = "David Hare";
        // Assert that our test setup is as we expect it to be; otherwise the tests below might not be meaningful
        assert digitalLibrary.authorSplayTree.data.author.equals(authorName);

        // When
        digitalLibrary.borrowBook(digitalLibrary.authorSplayTree, SplayTreeUtils.DEFAULT_COMPARISON_MODE);

        // Then
        assertNotEquals(authorName, digitalLibrary.authorSplayTree.data.author);
        assertNotEquals(authorName, digitalLibrary.isbnSplayTree.data.author);
        assertEquals(authorName, digitalLibrary.borrowedSplayTree.data.author);
    }

    @Test
    public void borrowBook_whenBookInISBNTree_updatesAllThreeTrees() {
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        long isbn = 9783642272653L;
        // Assert that our test setup is as we expect it to be; otherwise the tests below might not be meaningful
        assert digitalLibrary.isbnSplayTree.data.ISBN == isbn;

        // When
        digitalLibrary.borrowBook(digitalLibrary.isbnSplayTree, SplayTreeUtils.ALT_COMPARISON_MODE);

        // Then
        assertNotEquals(isbn, digitalLibrary.authorSplayTree.data.ISBN);
        assertNotEquals(isbn, digitalLibrary.isbnSplayTree.data.ISBN);
        assertEquals(isbn, digitalLibrary.borrowedSplayTree.data.ISBN);
    }

    @Test
    public void returnBook_whenBookNotFound_doesNotUpdateTrees() throws IOException {
        // Given
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);
        String authorName = "Torben Mogensen";

        // When
        SplayTreeNode<Book> previousAuthorTree = digitalLibrary.authorSplayTree;
        SplayTreeNode<Book> previousIsbnTree = digitalLibrary.isbnSplayTree;
        digitalLibrary.returnBook(authorName);

        // Then
        assertEquals(previousAuthorTree, digitalLibrary.authorSplayTree);
        assertEquals(previousIsbnTree, digitalLibrary.isbnSplayTree);
        assertNull(digitalLibrary.borrowedSplayTree);
    }


    @Test
    public void returnBook_whenBookFound_updatesAllThreeTrees() throws IOException {
        Scanner baseLibraryScanner = new Scanner("The Algorithm Design Manual\tSteven Skiena\t9781849967204\n" +
                "Algorithms to Live By: The Computer Science of Human Decisions\tBrian Christian\t9781250118363\n" +
                "Algorithmics — The Spirit of Computing\tDavid Hare\t9783642272653");
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.loadBaseLibrary(baseLibraryScanner);

        String authorName = "David Hare";
        // Assert that our test setup is as we expect it to be; otherwise the tests below might not be meaningful
        assert digitalLibrary.authorSplayTree.data.author.equals(authorName);
        digitalLibrary.borrowBook(digitalLibrary.authorSplayTree, SplayTreeUtils.DEFAULT_COMPARISON_MODE);

        // When
        digitalLibrary.returnBook(authorName);

        // Then
        assertEquals(authorName, digitalLibrary.authorSplayTree.data.author);
        assertEquals(authorName, digitalLibrary.isbnSplayTree.data.author);
        assertNull(digitalLibrary.borrowedSplayTree);
    }

    @Test
    public void library_whenLoadedAndSearched_findsBook() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"author", "Thomas H Cormen", "n", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Thomas H Cormen\n" +
                "The following entry matched your search term:\n" +
                "Introduction to Algorithms, Thomas H Cormen, 9780262033848\n\n" +
                "Would you like to borrow this book? (y/n) n\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenLoadedAndSearchedForMissingBook_findsNothing() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"author", "asdf", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: asdf\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenSearchingForBookByAuthor_allowsToBorrowAndReturnBook() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"author", "Alfred V Aho", "y", "author", "Alfred V Aho", "return", "Alfred V Aho", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Alfred V Aho\n" +
                "The following entry matched your search term:\n" +
                "Dragon book (Compilers: Principles, Techniques, and Tools), Alfred V Aho, 9780321486813\n\n" +
                "Would you like to borrow this book? (y/n) y\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Alfred V Aho\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: return\n" +
                "Please enter the author for the book you are returning: Alfred V Aho\n" +
                "Thank you for returning this book.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }


    @Test
    public void library_whenBookBorrowedByISBN_doesNotFindInAuthorTree() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"ISBN", "9780262039246", "y", "author", "Richard Sutton & Andrew Barto", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: ISBN\n" +
                "You have selected Search by ISBN. Please enter the ISBN: 9780262039246\n" +
                "The following entry matched your search term:\n" +
                "Reinforcement Learning: An Introduction, Richard Sutton & Andrew Barto, 9780262039246\n\n" +
                "Would you like to borrow this book? (y/n) y\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Richard Sutton & Andrew Barto\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenBookBorrowedByAuthor_doesNotFindInISBNTree() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"author", "Richard Sutton & Andrew Barto", "y", "isbn", "9780262039246", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Richard Sutton & Andrew Barto\n" +
                "The following entry matched your search term:\n" +
                "Reinforcement Learning: An Introduction, Richard Sutton & Andrew Barto, 9780262039246\n\n" +
                "Would you like to borrow this book? (y/n) y\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: isbn\n" +
                "You have selected Search by ISBN. Please enter the ISBN: 9780262039246\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenRequestingReturnButNothingBorrowed_returnsNotFound() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"return", "Richard Sutton & Andrew Barto", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: return\n" +
                "Please enter the author for the book you are returning: Richard Sutton & Andrew Barto\n" +
                "Sorry, no books were borrowed with that author.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenAllBooksBorrowed_returnsNothingForEitherSearchOrForPopular() {
        // Given
        String[] finalArgs = {"author", "Gayle Laakmann McDowell", "isbn", "9781133187790", "popular", "exit"};

        // When
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        digitalLibrary.main(new String[]{"exit"});
        String nextAuthor = digitalLibrary.authorSplayTree.data.author;

        while (nextAuthor != null) {
            digitalLibrary = new SplayTreeDigitalLibrary();
            digitalLibrary.main(new String[]{"author", nextAuthor, "y", "exit"});
            nextAuthor = digitalLibrary.authorSplayTree != null ? digitalLibrary.authorSplayTree.data.author : null;
        }

        digitalLibrary = new SplayTreeDigitalLibrary();
        String finalOutput = digitalLibrary.main(finalArgs);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Gayle Laakmann McDowell\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: isbn\n" +
                "You have selected Search by ISBN. Please enter the ISBN: 9781133187790\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: popular\n" +
                "Sorry, no popular books were found that are not already borrowed.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, finalOutput);
    }

    @Test
    public void library_whenLoadedAndReloaded_returnsPopularBooksFromPreviousQueries() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"author", "Keith Cooper & Linda Torczon", "n", "isbn", "9780262640688", "n", "exit"};
        SplayTreeDigitalLibrary digitalLibrary2 = new SplayTreeDigitalLibrary();
        String[] args2 = {"popular", "exit"};

        // When
        digitalLibrary.main(args);
        String secondOutput = digitalLibrary2.main(args2);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\nLoading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by reference ISBN, " +
                "‘popular’ to see the top books, ‘return’ to return a book, or ‘exit’ to leave the program: popular\n" +
                "Engineering: A Compiler, 2nd Edition, Keith Cooper & Linda Torczon, 9780120884780\n" +
                "The Elements of Computing Systems: Building a Modern Computer from First Principles, Noam Nisan & Shimon Schocken, 9780262640688\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by reference ISBN, " +
                "‘popular’ to see the top books, ‘return’ to return a book, or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, secondOutput);
    }

    @Test
    public void library_whenGivenUnknownInput_reprompts() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"foo", "ISBN", "boo", "9781558601918", "maybe", "y", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: foo\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: ISBN\n" +
                "You have selected Search by ISBN. Please enter the ISBN: boo\n" +
                "Oops! Please enter a number: 9781558601918\n" +
                "The following entry matched your search term:\n" +
                "Paradigms Of AI Programming: Case Studies in Common Lisp, Peter Norvig, 9781558601918\n\n" +
                "Would you like to borrow this book? (y/n) maybe\n" +
                "Oops! Please enter y or n: y\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenGivenIncompleteInputAtMenuChoice_exits() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"foo"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: foo\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: ";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenGivenIncompleteInputAtYesNoChoice_choosesNoAndExits() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"ISBN", "9781558601918"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: ISBN\n" +
                "You have selected Search by ISBN. Please enter the ISBN: 9781558601918\n" +
                "The following entry matched your search term:\n" +
                "Paradigms Of AI Programming: Case Studies in Common Lisp, Peter Norvig, 9781558601918\n\n" +
                "Would you like to borrow this book? (y/n) \n" +
                "Oops! Please enter y or n: \n" +
                "Oops! Please enter y or n: \n" +
                "Oops! Please enter y or n: \n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: ";
        assertEquals(expectedOutput, output);
    }

    @Test
    public void library_whenFollowingExampleSequence_producesExampleOutput() {
        // Given
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();
        String[] args = {"author", "asdf", "author", "Thomas H Cormen", "y", "author", "Thomas H Cormen", "return", "Thomas H Cormen", "foo", "exit"};

        // When
        String output = digitalLibrary.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\nPlease enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: asdf\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Thomas H Cormen\n" +
                "The following entry matched your search term:\n" +
                "Introduction to Algorithms, Thomas H Cormen, 9780262033848\n\n" +
                "Would you like to borrow this book? (y/n) y\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: author\n" +
                "You have selected Search by Author. Please enter the author name: Thomas H Cormen\n" +
                "Sorry, no books were found with your search term.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: return\n" +
                "Please enter the author for the book you are returning: Thomas H Cormen\n" +
                "Thank you for returning this book.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: foo\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        assertEquals(expectedOutput, output);
    }

}