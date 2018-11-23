package pa2.SPL_DIGITAL_LIB;

/**
 * Name: Hayley Ross
 * Email: hayleyross@brandeis.edu
 * Assignment: PA2_Digital_Library
 *
 * A wrapper class to allow SplayTreeDigitalLibrary to be run as a command line application.
 */
public class SplayTreeDigitalLibraryRunner {

    /**
     * A main method with the correct signature to run as a command line application, unlike the main method of
     * SplayTreeDigitalLibrary which is not static.
     * @param args an optional array of arguments. If provided, then these will be read from instead of waiting for the user
     * to supply input using System.in
     */
    public static void main(String[] args) {
        SplayTreeDigitalLibrary digitalLibrary = new SplayTreeDigitalLibrary();

        // Ignore the return value of this method since it also prints to the console.
        digitalLibrary.main(args);
    }
}
