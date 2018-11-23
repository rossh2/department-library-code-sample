package library.model;

import com.opencsv.bean.CsvBindByName;

public class Book implements Comparable<Book> {

    @CsvBindByName(column = "Title")
    private String title;

    @CsvBindByName(column = "Author")
    private String author;

    @CsvBindByName(column = "ISBN")
    private long isbn;

    public Book() {
    }

    public Book(String title, String author, long isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public long getIsbn() {
        return isbn;
    }

    @Override
    public int compareTo(Book o) {
        return compareByAuthor(o);
    }

    public int compareByAuthor(Book o) {
        return author.toLowerCase().compareTo(o.author.toLowerCase());
    }

    public int compareByISBN(Book o) {
        return Long.compare(isbn, o.isbn);
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %d", title, author, isbn);
    }

}
