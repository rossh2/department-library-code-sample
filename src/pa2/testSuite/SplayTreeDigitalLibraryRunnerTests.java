package pa2.testSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pa2.SPL_DIGITAL_LIB.SplayTreeDigitalLibraryRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

/**
 * Name: Hayley Ross
 * Email: hayleyross@brandeis.edu
 * Assignment: PA2_Digital_Library
 */
public class SplayTreeDigitalLibraryRunnerTests {

    private ByteArrayOutputStream outContent;
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void main_passesArgumentsToLibrary() {
        // Given
        String[] args = {"exit"};

        // When
        SplayTreeDigitalLibraryRunner.main(args);

        // Then
        String expectedOutput = "Welcome to the SPLTREE_DIGITAL_LIBRARY.\n" +
                "Loading library...  DONE.\n\n" +
                "Please enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: exit";
        String actualOutput = outContent.toString();
        // Depending whether this is run on Windows or Linux/Unix/Mac it will have different line endings
        String normalisedActualOutput = actualOutput.replaceAll("\\r\\n?", "\n");
        assertEquals(expectedOutput, normalisedActualOutput);
    }
}