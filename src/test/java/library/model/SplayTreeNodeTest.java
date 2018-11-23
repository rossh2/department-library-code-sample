package library.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class SplayTreeNodeTest {

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
                "LEFT: Another Book, Raphael, 87654312\n" +
                "RIGHT: A Third Book, Allison, 90871234";

        // When
        String output = node.toString();

        // Then
        assertThat(output, is(equalTo(expectedOutput)));
    }
}