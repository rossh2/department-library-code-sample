package pa2.SPL_DIGITAL_LIB;

/**
 * Name: Hayley Ross
 * Email: hayleyross@brandeis.edu
 * Assignment: PA2_Digital_Library
 *
 * A book that can be stored in the digital library.
 *
 * Implements DoublyComparable since the digital library allows for a default and an alternative way
 * of comparing (in particular searching for) its books.
 */
public class Book implements DoublyComparable<Book> {

    public String title;
    public String author;
    public long ISBN;

    /**
     * Constructs a book with all fields populated.
     *
     * Running time: O(1)
     *
     * @param title the title of the book
     * @param author the author of the book
     * @param ISBN the ISBN of the book (most ISBNs are long, but will also accept int)
     */
    public Book(String title, String author, long ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
    }

    /**
     * Running time: O(1)
     * @return a human-readable representation of the book including title, author and ISBN
     */
    @Override
    public String toString() {
        return String.format("%s, %s, %d", title, author, ISBN);
    }

    /**
     * Compare this book to another book by author, which is the primary comparison method.
     *
     * Compares using case-insensitive lexicographical order, as implemented by the String class
     * (with case-insensitivity added).
     *
     * Running time: O(1)
     *
     * @param o another book
     * @return a negative integer, zero, or a positive integer as this book's author
     * is less than, equal to, or greater than the author of book that was passed in
     */
    @Override
    public int compareTo(Book o) {
        return author.toLowerCase().compareTo(o.author.toLowerCase());
    }

    /**
     * Compare this book to another book by ISBN, which is the secondary comparison method.
     *
     * Compares using the default numerical comparison for longs.
     *
     * Running time: O(1)
     *
     * @param o another book
     * @return a negative integer, zero, or a positive integer as this book's ISBN
     * is less than, equal to, or greater than the ISBN of book that was passed in
     */
    public int compareToAlt(Book o) {
        return Long.compare(ISBN, o.ISBN);
    }
}
