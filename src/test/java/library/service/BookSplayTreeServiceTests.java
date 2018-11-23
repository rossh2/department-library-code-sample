package library.service;

import library.model.Book;
import library.model.SplayTreeNode;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BookSplayTreeServiceTests {
    
    private final BookSplayTreeService bookSplayTreeService = new BookSplayTreeService();

    private final Book micaelaBook = new Book("A Book", "Micaela", 12345678);
    private final Book benBook = new Book("Another Book", "Ben", 87654312);
    private final Book shuaiBook = new Book("A Third Book", "Shuai", 90871234);
    private final Book andreBook = new Book("A Fourth Book", "Andre", 89502312);
    private final Book ellisBook = new Book("A Fifth Book", "Ellis", 83029539);
    private final Book shanshanBook = new Book("A Sixth Book", "Shanshan", 84920452);
    private final Book tomBook = new Book("A Seventh Book", "Tom", 88784712);
    private final Book antonellaBook = new Book("A New Book", "Antonella", 70694712);

    private SplayTreeNode<Book> node1;
    private SplayTreeNode<Book> node2;
    private SplayTreeNode<Book> node3;
    private SplayTreeNode<Book> node4;
    private SplayTreeNode<Book> node5;
    private SplayTreeNode<Book> node6;

    private SplayTreeNode<Book> node1byIsbn;
    private SplayTreeNode<Book> node2byIsbn;
    private SplayTreeNode<Book> node3byIsbn;

    @Before
    public void setupNodesSortedByAuthor() {
        node1 = new SplayTreeNode<>(micaelaBook);
        node2 = new SplayTreeNode<>(benBook);
        node3 = new SplayTreeNode<>(shuaiBook);
        node4 = new SplayTreeNode<>(andreBook);
        node5 = new SplayTreeNode<>(ellisBook);
        node6 = new SplayTreeNode<>(shanshanBook);

        node1.left = node2;
        node2.parent = node1;
        node1.right = node3;
        node3.parent = node1;

        node2.left = node4;
        node4.parent = node2;
        node2.right = node5;
        node5.parent = node2;

        node3.left = node6;
        node6.parent = node3;

        node1byIsbn = new SplayTreeNode<>(benBook);
        assert benBook.getIsbn() > ellisBook.getIsbn(); // Protect against ISBNs accidentally getting changed
        node2byIsbn = new SplayTreeNode<>(ellisBook);
        assert andreBook.getIsbn() > benBook.getIsbn(); // Protect against ISBNs accidentally getting changed
        node3byIsbn = new SplayTreeNode<>(andreBook);

        node1byIsbn.left = node2byIsbn;
        node2byIsbn.parent = node1byIsbn;
        node1byIsbn.right = node3byIsbn;
        node3byIsbn.parent = node1byIsbn;
    }

    @Test
    public void zig_performsRightRotation() {
        // Given
        // When
        bookSplayTreeService.zig(node2);

        // Then
        assertNull(node2.parent);
        assertSame(node4, node2.left);
        assertSame(node1, node2.right);
        assertSame(node2, node4.parent);
        assertNull(node4.left);
        assertNull(node4.right);
        assertSame(node2, node1.parent);
        assertSame(node5, node1.left);
        assertSame(node3, node1.right);
        assertSame(node1, node5.parent);
        assertNull(node5.left);
        assertNull(node5.right);
        assertSame(node1, node3.parent);
        assertSame(node6, node3.left);
        assertNull(node3.right);
        assertSame(node3, node6.parent);
        assertNull(node6.left);
        assertNull(node6.right);
    }

    @Test
    public void zag_performsLeftRotation() {
        // Given
        // When
        bookSplayTreeService.zag(node3);

        // Then
        assertNull(node3.parent);
        assertSame(node1, node3.left);
        assertNull(node3.right);
        assertSame(node3, node1.parent);
        assertSame(node2, node1.left);
        assertSame(node6, node1.right);
        assertSame(node1, node2.parent);
        assertSame(node4, node2.left);
        assertSame(node5, node2.right);
        assertSame(node2, node4.parent);
        assertNull(node4.left);
        assertNull(node4.right);
        assertSame(node2, node5.parent);
        assertNull(node5.left);
        assertNull(node5.right);
        assertSame(node1, node6.parent);
        assertNull(node6.left);
        assertNull(node6.right);
    }

    @Test
    public void insert_givenEmptyTree_insertsAtRoot() {
        // Given
        SplayTreeNode<Book> node = new SplayTreeNode<>(antonellaBook);

        // When
        SplayTreeNode<Book> root = bookSplayTreeService.insertByAuthor(null, node);

        // Then
        assertSame(root, node);
        assertNull(node.parent);
        assertNull(node.left);
        assertNull(node.right);
    }

    @Test
    public void insert_givenOnlyRootAndSmallerNode_insertsOnLeftThenZigs() {
        // Given
        SplayTreeNode<Book> root = new SplayTreeNode<>(micaelaBook);
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(antonellaBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByAuthor(root, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.left);
        assertSame(root, newNode.right);
        assertSame(newNode, root.parent);
        assertNull(root.left);
        assertNull(root.right);
    }

    @Test
    public void insert_givenOnlyRootAndLargerNode_insertsOnRightThenZags() {
        // Given
        SplayTreeNode<Book> root = new SplayTreeNode<>(benBook);
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(micaelaBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByAuthor(root, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.right);
        assertSame(root, newNode.left);
        assertSame(newNode, root.parent);
        assertNull(root.left);
        assertNull(root.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndSmallestNode_insertsOnLeftThenZigZigs() {
        // Given
        node2.left = null;
        node2.right = null;
        node3.left = null;
        node3.right = null;

        SplayTreeNode<Book> newNode = new SplayTreeNode<>(andreBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByAuthor(node1, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.left);
        assertSame(node2, newNode.right);
        assertSame(node2.parent, newNode);
        assertNull(node2.left);
        assertSame(node1, node2.right);
        assertSame(node2, node1.parent);
        assertNull(node1.left);
        assertSame(node3, node1.right);
        assertSame(node1, node3.parent);
        assertNull(node3.left);
        assertNull(node3.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndLowerMiddleNode_insertsInMiddleThenZagZigs() {
        // Given
        node2.left = null;
        node2.right = null;
        node3.left = null;
        node3.right = null;

        SplayTreeNode<Book> newNode = new SplayTreeNode<>(ellisBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByAuthor(node1, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertSame(node2, newNode.left);
        assertSame(newNode, node2.parent);
        assertNull(node2.left);
        assertNull(node2.right);
        assertSame(node1, newNode.right);
        assertSame(node1.parent, newNode);
        assertNull(node1.left);
        assertSame(node3, node1.right);
        assertSame(node1, node3.parent);
        assertNull(node3.left);
        assertNull(node3.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndUpperMiddleNode_insertsInMiddleThenZigZags() {
        // Given
        node2.left = null;
        node2.right = null;
        node3.left = null;
        node3.right = null;

        SplayTreeNode<Book> newNode = new SplayTreeNode<>(shanshanBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByAuthor(node1, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertSame(node1, newNode.left);
        assertSame(newNode, node1.parent);
        assertSame(node2, node1.left);
        assertSame(node1, node2.parent);
        assertNull(node2.left);
        assertNull(node2.right);
        assertNull(node1.right);
        assertSame(node3, newNode.right);
        assertSame(node3.parent, newNode);
        assertNull(node3.left);
        assertNull(node3.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndLargestNode_insertsOnRightThenZagZags() {
        // Given
        node2.left = null;
        node2.right = null;
        node3.left = null;
        node3.right = null;

        SplayTreeNode<Book> newNode = new SplayTreeNode<>(tomBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByAuthor(node1, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.right);
        assertSame(node3, newNode.left);
        assertSame(node3.parent, newNode);
        assertNull(node3.right);
        assertSame(node1, node3.left);
        assertSame(node3, node1.parent);
        assertNull(node1.right);
        assertSame(node2, node1.left);
        assertSame(node1, node2.parent);
        assertNull(node2.right);
        assertNull(node2.left);
    }

    @Test
    public void insert_givenTwoLevelTree_insertsAsLeafAndSplays() {
        // Given
        SplayTreeNode<Book> node7 = new SplayTreeNode<>(antonellaBook);

        // When
        bookSplayTreeService.insertByAuthor(node1, node7);

        // Then
        assertNull(node7.parent);
        assertSame(node7.left, node4);
        assertSame(node7.right, node1);
        assertSame(node4.parent, node7);
        assertNull(node4.left);
        assertNull(node4.right);
        assertSame(node1.parent, node7);
        assertSame(node1.left, node2);
        assertSame(node1.right, node3);
        assertSame(node2.parent, node1);
        assertNull(node2.left);
        assertSame(node2.right, node5);
        assertSame(node5.parent, node2);
        assertNull(node5.left);
        assertNull(node5.right);
        assertSame(node3.parent, node1);
        assertSame(node3.left, node6);
        assertNull(node3.right);
        assertSame(node6.parent, node3);
        assertNull(node6.left);
        assertNull(node6.right);
    }

    @Test
    public void insert_givenOnlyRootAndSmallerNodeByAlt_insertsOnLeftThenZigs() {
        // Given
        SplayTreeNode<Book> root = new SplayTreeNode<>(benBook);
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(ellisBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByISBN(root, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.left);
        assertSame(root, newNode.right);
        assertSame(newNode, root.parent);
        assertNull(root.left);
        assertNull(root.right);
    }

    @Test
    public void insert_givenOnlyRootAndLargerNodeByAlt_insertsOnRightThenZags() {
        // Given
        SplayTreeNode<Book> root = new SplayTreeNode<>(benBook);
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(andreBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByISBN(root, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.right);
        assertSame(root, newNode.left);
        assertSame(newNode, root.parent);
        assertNull(root.left);
        assertNull(root.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndSmallestNodeByAlt_insertsOnLeftThenZigZigs() {
        // Given
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(antonellaBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByISBN(node1byIsbn, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.left);
        assertSame(node2byIsbn, newNode.right);
        assertSame(node2byIsbn.parent, newNode);
        assertNull(node2byIsbn.left);
        assertSame(node1byIsbn, node2byIsbn.right);
        assertSame(node2byIsbn, node1byIsbn.parent);
        assertNull(node1byIsbn.left);
        assertSame(node3byIsbn, node1byIsbn.right);
        assertSame(node1byIsbn, node3byIsbn.parent);
        assertNull(node3byIsbn.left);
        assertNull(node3byIsbn.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndLowerMiddleNodeByAlt_insertsInMiddleThenZagZigs() {
        // Given
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(shanshanBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByISBN(node1byIsbn, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertSame(node2byIsbn, newNode.left);
        assertSame(newNode, node2byIsbn.parent);
        assertNull(node2byIsbn.left);
        assertNull(node2byIsbn.right);
        assertSame(node1byIsbn, newNode.right);
        assertSame(node1byIsbn.parent, newNode);
        assertNull(node1byIsbn.left);
        assertSame(node3byIsbn, node1byIsbn.right);
        assertSame(node1byIsbn, node3byIsbn.parent);
        assertNull(node3byIsbn.left);
        assertNull(node3byIsbn.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndUpperMiddleNodeByAlt_insertsInMiddleThenZigZags() {
        // Given
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(tomBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByISBN(node1byIsbn, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertSame(node1byIsbn, newNode.left);
        assertSame(newNode, node1byIsbn.parent);
        assertSame(node2byIsbn, node1byIsbn.left);
        assertSame(node1byIsbn, node2byIsbn.parent);
        assertNull(node2byIsbn.left);
        assertNull(node2byIsbn.right);
        assertNull(node1byIsbn.right);
        assertSame(node3byIsbn, newNode.right);
        assertSame(node3byIsbn.parent, newNode);
        assertNull(node3byIsbn.left);
        assertNull(node3byIsbn.right);
    }

    @Test
    public void insert_givenRootWithChildrenAndLargestNodeByAlt_insertsOnRightThenZagZags() {
        // Given
        SplayTreeNode<Book> newNode = new SplayTreeNode<>(shuaiBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.insertByISBN(node1byIsbn, newNode);

        // Then
        assertSame(newNode, newRoot);
        assertNull(newNode.parent);
        assertNull(newNode.right);
        assertSame(node3byIsbn, newNode.left);
        assertSame(node3byIsbn.parent, newNode);
        assertNull(node3byIsbn.right);
        assertSame(node1byIsbn, node3byIsbn.left);
        assertSame(node3byIsbn, node1byIsbn.parent);
        assertNull(node1byIsbn.right);
        assertSame(node2byIsbn, node1byIsbn.left);
        assertSame(node1byIsbn, node2byIsbn.parent);
        assertNull(node2byIsbn.right);
        assertNull(node2byIsbn.left);
    }

    @Test
    public void search_givenEmptyTree_returnsNull() {
        // Given
        // When
        SplayTreeNode<Book> foundNode = bookSplayTreeService.searchByAuthor(null, antonellaBook);

        // Then
        assertNull(foundNode);
    }

    @Test
    public void search_whenSeekingRoot_doesNotChangeTree() {
        // Given
        // When
        SplayTreeNode<Book> foundNode = bookSplayTreeService.searchByAuthor(node1, micaelaBook);

        // Then
        assertSame(node1, foundNode);
        assertNull(node1.parent);
        assertSame(node2, node1.left);
        assertSame(node3, node1.right);
        // Assume that if the children and parent of the found node remain the same, we don't need to check the rest
    }

    @Test
    public void search_whenSeekingLeftChildOfRootByAuthor_splaysIt() {
        // Given
        // When
        SplayTreeNode<Book> foundNode = bookSplayTreeService.searchByAuthor(node1, benBook);

        // Then
        assertSame(node2, foundNode);
        assertNull(node2.parent);
        assertSame(node2.left, node4);
        assertSame(node2.right, node1);
        assertSame(node4.parent, node2);
        assertNull(node4.left);
        assertNull(node4.right);
        assertSame(node1.parent, node2);
        assertSame(node1.left, node5);
        assertSame(node1.right, node3);
        assertSame(node5.parent, node1);
        assertNull(node5.left);
        assertNull(node5.right);
        assertSame(node3.parent, node1);
        assertSame(node3.left, node6);
        assertNull(node3.right);
        assertSame(node6.parent, node3);
        assertNull(node6.left);
        assertNull(node6.right);

    }

    @Test
    public void search_whenSeekingRightChildOfRootByAuthor_splaysIt() {
        // Given
        // When
        SplayTreeNode<Book> foundNode = bookSplayTreeService.searchByAuthor(node1, shuaiBook);

        // Then
        assertSame(node3, foundNode);
        assertNull(node3.parent);
        assertSame(node3.left, node1);
        assertNull(node3.right);
        assertSame(node1.parent, node3);
        assertSame(node1.left, node2);
        assertSame(node1.right, node6);
        assertSame(node2.parent, node1);
        assertSame(node2.left, node4);
        assertSame(node2.right, node5);
        assertSame(node4.parent, node2);
        assertNull(node4.left);
        assertNull(node4.right);
        assertSame(node5.parent, node2);
        assertNull(node5.left);
        assertNull(node5.right);
        assertSame(node6.parent, node1);
        assertNull(node6.left);
        assertNull(node6.right);
    }

    @Test
    public void search_whenWouldBeChildOfLeftChildByAuthor_splaysLeftChild() {
        // Given
        node2.left = null;
        node2.right = null;
        node3.left = null;
        node3.right = null;

        // When
        SplayTreeNode<Book> foundNode = bookSplayTreeService.searchByAuthor(node1, andreBook);

        // Then
        assertNotSame(andreBook, foundNode.data);

        assertSame(node2, foundNode);
        assertNull(node2.parent);
        assertNull(node2.left);
        assertSame(node1, node2.right);
        assertSame(node2, node1.parent);
        assertNull(node1.left);
        assertSame(node3, node1.right);
        assertSame(node1, node3.parent);
        assertNull(node3.left);
        assertNull(node3.right);
    }

    @Test
    public void search_whenWouldBeChildOfRightChildByAuthor_splaysRightChild() {
        // Given
        node2.left = null;
        node2.right = null;
        node3.left = null;
        node3.right = null;

        // When
        SplayTreeNode<Book> foundNode = bookSplayTreeService.searchByAuthor(node1, shanshanBook);

        // Then
        assertNotSame(shanshanBook, foundNode.data);

        assertSame(node3, foundNode);
        assertNull(node3.parent);
        assertNull(node3.right);
        assertSame(node1, node3.left);
        assertSame(node3, node1.parent);
        assertNull(node1.right);
        assertSame(node2, node1.left);
        assertSame(node1, node2.parent);
        assertNull(node2.left);
        assertNull(node2.right);
    }

    @Test
    public void delete_whenDeletingLastNode_returnsNull() {
        // Given
        SplayTreeNode<Book> node = new SplayTreeNode<>(antonellaBook);

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.delete(node, node);

        // Then
        assertNull(newRoot);
    }

    @Test
    public void delete_whenOnlyLeftChildRemaining_returnsIt() {
        // Given
        SplayTreeNode<Book> root = new SplayTreeNode<>(antonellaBook);
        SplayTreeNode<Book> child = new SplayTreeNode<>(andreBook);
        root.left = child;
        child.parent = root;

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.delete(root, root);

        // Then
        assertSame(newRoot, child);
        assertNull(child.parent);
        assertNull(child.left);
        assertNull(child.right);
    }

    @Test
    public void delete_whenOnlyRightChildRemaining_returnsIt() {
        // Given
        SplayTreeNode<Book> root = new SplayTreeNode<>(antonellaBook);
        SplayTreeNode<Book> child = new SplayTreeNode<>(benBook);
        root.right = child;
        child.parent = root;

        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.delete(root, root);

        // Then
        assertSame(newRoot, child);
        assertNull(child.parent);
        assertNull(child.left);
        assertNull(child.right);
    }

    @Test
    public void delete_whenDeletingRoot_findsMaxOfLeftSubtree() {
        // Given
        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.delete(node1, node1);

        // Then
        assertSame(node5, newRoot);
        assertNull(node5.parent);
        assertSame(node2, node5.left);
        assertSame(node5, node2.parent);
        assertNull(node2.right);
        assertSame(node4, node2.left);
        assertSame(node4.parent, node2);
        assertNull(node4.left);
        assertNull(node4.right);
        assertSame(node3, node5.right);
        assertSame(node5, node3.parent);
        assertSame(node6, node3.left);
        assertSame(node3, node6.parent);
        assertNull(node6.left);
        assertNull(node6.right);
        assertNull(node3.right);
    }

    @Test
    public void delete_whenDeletingLeftChildOfRootByAuthor_splaysItAndFindsMaxOfLeftSubtree() {
        // Given
        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.delete(node1, node2);

        // Then
        assertSame(node4, newRoot);
        assertNull(node4.parent);
        assertNull(node4.left);
        assertSame(node1, node4.right);
        assertSame(node4, node1.parent);
        assertSame(node5, node1.left);
        assertSame(node1, node5.parent);
        assertNull(node5.left);
        assertNull(node5.right);
        assertSame(node3, node1.right);
        assertSame(node1, node3.parent);
        assertSame(node6, node3.left);
        assertSame(node3, node6.parent);
        assertNull(node6.left);
        assertNull(node6.right);
    }

    @Test
    public void delete_whenDeletingRightChildOfRootByAuthor_splaysItAndFindsMaxOfLeftSubtree() {
        // Given
        // When
        SplayTreeNode<Book> newRoot = bookSplayTreeService.delete(node1, node3);

        // Then
        assertSame(node6, newRoot);
        assertNull(node6.parent);
        assertNull(node6.right);
        assertSame(node1, node6.left);
        assertSame(node6, node1.parent);
        assertNull(node1.right);
        assertSame(node2, node1.left);
        assertSame(node1, node2.parent);
        assertSame(node4, node2.left);
        assertSame(node2, node4.parent);
        assertNull(node4.left);
        assertNull(node4.right);
        assertSame(node5, node2.right);
        assertSame(node2, node5.parent);
        assertNull(node5.left);
        assertNull(node5.right);
    }
}