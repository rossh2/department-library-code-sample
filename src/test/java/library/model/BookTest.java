package library.model;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BookTest {

    @Test
    public void toString_returnsTitleAuthorAndIsbn() {
        // Given
        Book book = new Book("Example Title", "Example Author", 12345678);
        String expectedOutput = "Example Title, Example Author, 12345678";

        // When
        String output = book.toString();

        // Then
        assertThat(output, is(equalTo(expectedOutput)));
    }

    @Test
    public void compareByAuthor_sortsAlphabeticallyByAuthorFirstName() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);
        Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 9780262660716L);

        // When
        int order = book1.compareByAuthor(book2);

        // Then
        assertThat(order, is(lessThan(0)));
    }

    @Test
    public void compareByAuthor_isSymmetric() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);
        Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 9780262660716L);

        // When
        int order1 = book1.compareByAuthor(book2);
        int order2 = book2.compareByAuthor(book1);

        // Then
        assertThat(order1, is(lessThan(0)));
        assertThat(order2, is(greaterThan(0)));
    }

    @Test
    public void compareByAuthor_returnsZeroIfEqual() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);

        // When
        int order = book1.compareByAuthor(book1);

        // Then
        assertThat(order, is(equalTo(0)));
    }

    @Test
    public void compareByISBN_sortsAscendingByIsbn() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 2L);
        Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 1L);

        // When
        int order = book1.compareByISBN(book2);

        // Then
        assertThat(order, is(greaterThan(0)));
    }

    @Test
    public void compareByISBN_isSymmetric() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 2L);
        Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 1L);

        // When
        int order1 = book1.compareByISBN(book2);
        int order2 = book2.compareByISBN(book1);

        // Then
        assertThat(order1, is(greaterThan(0)));
        assertThat(order2, is(lessThan(0)));
    }

    @Test
    public void compareByISBN_returnsZeroIfEqual() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);

        // When
        int order = book1.compareByISBN(book1);

        // Then
        assertThat(order, is(equalTo(0)));
    }
}