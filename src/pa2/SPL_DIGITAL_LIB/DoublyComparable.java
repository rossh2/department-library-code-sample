package pa2.SPL_DIGITAL_LIB;

/**
 * Name: Hayley Ross
 * Email: hayleyross@brandeis.edu
 * Assignment: PA2_Digital_Library
 *
 * An interface specifying that the class implementing it has a default and an alternative way
 * to compare two objects of that class.
 */
public interface DoublyComparable<T> extends Comparable<T> {
    int compareToAlt(T o);
}
