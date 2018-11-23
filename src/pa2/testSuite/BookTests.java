package pa2.testSuite;

import org.junit.Test;
import pa2.SPL_DIGITAL_LIB.Book;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BookTests {

	@Test
	public void create_setsTitleAuthorAndIsbn() {
	    // Given
        String title = "Example Title";
        String author = "Example Author";
        long isbn = 9780262660716L;

		// When
        Book book = new Book(title, author, isbn);

		// Then
        assertEquals(title, book.title);
        assertEquals(author, book.author);
		assertEquals(isbn, book.ISBN);
	}

	@Test
    public void create_givenIntIsbn_setsIsbn() {
        // Given
        String title = "Example Title";
        String author = "Example Author";
        int isbn = 12345678;

        // When
        Book book = new Book(title, author, isbn);

        // Then
        assertEquals(12345678L, book.ISBN);
    }

	@Test
	public void toString_returnsTitleAuthorAndIsbn() {
	    // Given
		Book book = new Book("Example Title", "Example Author", 12345678);
		String expectedOutput = "Example Title, Example Author, 12345678";

		// When
		String output = book.toString();

		// Then
		assertEquals(expectedOutput, output);
	}

	@Test
    public void compareTo_sortsAlphabeticallyByAuthorFirstName() {
	    // Given
	    Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);
	    Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 9780262660716L);

        // When
        int order = book1.compareTo(book2);

        // Then
        assertTrue(order < 0);
    }

    @Test
    public void compareTo_isSymmetric() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);
        Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 9780262660716L);

        // When
        int order = book2.compareTo(book1);

        // Then
        assertTrue(order > 0);
    }

    @Test
    public void compareTo_returnsZeroIfEqual() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);

        // When
        int order = book1.compareTo(book1);

        // Then
        assertEquals(0, order);
    }

    @Test
    public void compareToAlt_sortsAscendingByIsbn() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 2L);
        Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 1L);

        // When
        int order = book1.compareToAlt(book2);

        // Then
        assertTrue(order > 0);
    }

    @Test
    public void compareToAlt_isSymmetric() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 2L);
        Book book2 = new Book("Basic Category Theory for Computer Scientists", "Benjamin Pierce", 1L);

        // When
        int order = book2.compareToAlt(book1);

        // Then
        assertTrue(order < 0);
    }

    @Test
    public void compareToAlt_returnsZeroIfEqual() {
        // Given
        Book book1 = new Book("Modern Compiler Implementation in C", "Andrew Appel", 9780521607650L);

        // When
        int order = book1.compareToAlt(book1);

        // Then
        assertEquals(0, order);
    }

}