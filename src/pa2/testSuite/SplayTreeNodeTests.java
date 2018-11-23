package pa2.testSuite;

import org.junit.Test;
import pa2.SPL_DIGITAL_LIB.Book;
import pa2.SPL_DIGITAL_LIB.SplayTreeNode;

import static org.junit.Assert.*;

public class SplayTreeNodeTests {

    @Test
    public void createAndSetIntegerData_setsData() {
        // Given
        // When
        SplayTreeNode<Integer> node = new SplayTreeNode<>();
        node.data = 9;

        // Then
        assertEquals(9, (int) node.data);
    }

    @Test
    public void createWithIntegerData_setsData() {
        // Given
        // When
        SplayTreeNode<Integer> node = new SplayTreeNode<>(9);

        // Then
        assertEquals(9, (int) node.data);
    }

    @Test
    public void setLeft_setsLeft() {
        // Given
        SplayTreeNode<Integer> node = new SplayTreeNode<>(8);
        SplayTreeNode<Integer> leftNode = new SplayTreeNode<>(7);

        // When
        node.left = leftNode;

        // Then
        assertSame(node.left, leftNode);
    }

    @Test
    public void setRight_setsRight() {
        // Given
        SplayTreeNode<Integer> node = new SplayTreeNode<>(3);
        SplayTreeNode<Integer> rightNode = new SplayTreeNode<>(4);

        // When
        node.right = rightNode;

        // Then
        assertSame(node.right, rightNode);
    }

    @Test
    public void setParent_setsParent() {
        // Given
        SplayTreeNode<Integer> node = new SplayTreeNode<>(6);
        SplayTreeNode<Integer> parentNode = new SplayTreeNode<>(8);

        // When
        node.parent = parentNode;

        // Then
        assertSame(node.parent, parentNode);
    }

    @Test
    public void toString_returnsTitleAuthorIsbnAndChildren() {
        // Given
        Book book1 = new Book("An Interesting Book", "Antonella", 12345678);
        Book book2 = new Book("Another Book", "Raphael", 87654312);
        Book book3 = new Book("A Third Book", "Allison", 90871234);

        SplayTreeNode<Book> node = new SplayTreeNode<>(book1);
        SplayTreeNode<Book> leftNode = new SplayTreeNode<>(book2);
        SplayTreeNode<Book> rightNode = new SplayTreeNode<>(book3);

        node.left = leftNode;
        node.right = rightNode;

        String expectedOutput = "An Interesting Book, Antonella, 12345678\n" +
                "LEFT    |    Another Book, Raphael, 87654312\n" +
                "RIGHT   |    A Third Book, Allison, 90871234";

        // When
        String output = node.toString();

        // Then
        assertEquals(expectedOutput, output);
    }
}