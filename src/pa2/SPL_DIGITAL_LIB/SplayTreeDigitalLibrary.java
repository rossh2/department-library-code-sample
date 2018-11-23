package pa2.SPL_DIGITAL_LIB;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Name: Hayley Ross
 * Email: hayleyross@brandeis.edu
 * Assignment: PA2_Digital_Library
 *
 * The main class of our digital library.
 *
 * Handles all aspects of IO: both reading and writing the saved trees to/from the file system, and also
 * IO with the user, by gathering input from the user and communicating what is happening by printing strings.
 *
 * Note that as per the starter pack, this class is not runnable as a command line application in its own right;
 * it must be instantiated by some other class and then the main() method must be called.
 */
public class SplayTreeDigitalLibrary {

    // File paths for loading and saving trees
    private static final String BASE_LIBRARY_PATH = "src/pa2/SPL_DIGITAL_LIB/spltree_digi_lib_baselib.txt";
    public static final String AUTHOR_TREE_PATH = "src/pa2/SPL_DIGITAL_LIB/spltree_digi_lib_auth.txt";
    public static final String ISBN_TREE_PATH = "src/pa2/SPL_DIGITAL_LIB/spltree_digi_lib_isbn.txt";
    public static final String BORROWED_TREE_PATH = "src/pa2/SPL_DIGITAL_LIB/spltree_digi_lib_borrowed.txt";

    // Prefixes for converting trees and books to and from strings
    // Stored here so that the to and from methods use the same constants
    private static final String DATA_PREFIX = "data: ";
    private static final String LEFT_PREFIX = ", left: ";
    private static final String RIGHT_PREFIX = ", right: ";
    private static final String TITLE_PREFIX = "title: ";
    private static final String AUTHOR_PREFIX = ", author: ";
    private static final String ISBN_PREFIX = ", ISBN: ";

    // In-memory representation of the available books, stored in splay trees
    public SplayTreeNode<Book> authorSplayTree = null;
    public SplayTreeNode<Book> isbnSplayTree = null;
    public SplayTreeNode<Book> borrowedSplayTree = null;

    // Fields for handling input by either reading args or prompting user
    private Scanner userInputScanner;
    private String[] args = null;
    private int nextArgumentToRead;

    // Store all output in addition to printing to console, so it can be returned
    private String consoleOutput;

    // Input-related constants
    private final String EXIT_CHOICE = "exit";
    private final int MAX_INPUT_ATTEMPTS = 3;

    /**
     * The main (and only proper) entry point to this class. (While other methods may be public, they are only public
     * for testing purposes, as specified by the assignment guidelines.)
     *
     * As per the starter pack, this method is not static and does not return void unlike traditional Java main methods.
     * Because of this, the class must be instantiated before this method can be called, and the class is not runnable
     * on its own.
     *
     * Running time: At most O((n+M)*log n) where M is the number of operations performed and n is the number of nodes.
     * Note that this is not O((n+M)*log n) on average (with a worse worst case), unlike each individual method:
     * because we start from an empty splay tree we guarantee that over time, this will amortize to O((n+M)*log n).
     * The reason it is not O(M*log n) is because we start by reading in the n nodes, then perform M operations.
     *
     * @param args an optional array of arguments. If provided, then these will be read from instead of waiting for the user
     * to supply input using System.in
     * @return a copy of all output that was printed to System.out
     */
    public String main(String[] args) {
        initialiseInputOutput(args);

        writeLineToConsole("Welcome to the SPLTREE_DIGITAL_LIBRARY.");
        try {
            loadLibrary();
            showAndHandleMenu();
        } catch (Exception e) {
            // Fail (somewhat) gracefully
            writeLineToConsole("Unexpected error: " + e.getMessage());
            writeLineToConsole("Aborting library.");
        }
        return consoleOutput;
    }

    /**
     * Display the possible interactions with the library to the user and prompt them for a choice.
     * Parse the input and either call the appropriate method to carry out that interaction, or repeat.
     * Then repeat indefinitely, until exit is specified.
     *
     * Running time: O(1) + the running time of the selected method. Most of these are O(log n) on average where n is
     * the number of total books, but popular() is O(1), as is exit.
     *
     * @throws IOException some of the interactions save the trees resulting from the interaction to a file, which
     * could theoretically throw an IOException
     */
    private void showAndHandleMenu() throws IOException {
        writeToConsole("\nPlease enter ‘author’ to search by author name, ‘ISBN’ to search by " +
                "reference ISBN, ‘popular’ to see the top books, ‘return’ to return a book, " +
                "or ‘exit’ to leave the program: ");
        String choice = getNonEmptyInput();

        switch (choice.toLowerCase()) {
            case "author":
                promptForAuthorSearch();
                break;
            case "isbn":
                promptForIsbnSearch();
                break;
            case "popular":
                popular();
                break;
            case "return":
                promptToReturnBook();
                break;
            case EXIT_CHOICE:
                return;
            default:
                writeBlankLineToConsole();
                break;
        }
        showAndHandleMenu();
    }

    /**
     * Prompts the user for the author they would like to search for, and then calls author search with that author.
     * Outputs whether the book was found or not. If found, prompts the user to borrow the book.
     * When done, saves the resulting trees since searching (and possibly borrowing) will have modified them.
     *
     * Running time: O(log n) on average, where n is the number of not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @throws IOException saving the trees to files could theoretically throw an IOException
     */
    private void promptForAuthorSearch() throws IOException {
        writeToConsole("\nYou have selected Search by Author. Please enter the author name: ");
        String author = getNonEmptyInput();

        SplayTreeNode<Book> bookNode = authorSearch(author);

        if (bookNode == null) {
            writeNewLineToConsole("Sorry, no books were found with your search term.");
        } else {
            writeNewLineToConsole("The following entry matched your search term:");
            writeLineToConsole(bookNode.data.toString());

            promptToBorrowBook(bookNode, SplayTreeUtils.DEFAULT_COMPARISON_MODE);
        }

        saveTrees();
    }

    /**
     * Search the library for a given author name. Searches in unborrowed books only.
     * Modifies the author splay tree regardless whether the book was found or not. If the book was found, it will be
     * the new root of the tree. If the book was not found, a book close to its presumed position in the tree will be
     * the new root of the tree.
     *
     * Running time: O(log n) on average, where n is the number of not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @param authorName the full author name to search for
     * @return the node containing the book found, if one is found, or null if no book was found
     */
    public SplayTreeNode<Book> authorSearch(String authorName) {
        if (authorSplayTree == null) {
            return null;
        }

        Book mockBook = new Book(null, authorName, 0);
        authorSplayTree = SplayTreeUtils.search(authorSplayTree, mockBook, SplayTreeUtils.DEFAULT_COMPARISON_MODE);

        // Splay tree root will not be the right book if it is not found
        return authorSplayTree.data.author.equals(authorName) ? authorSplayTree : null;
    }

    /**
     * Prompts the user for the ISBN they would like to search for, and then calls ISBN search with that author.
     * Outputs whether the book was found or not. If found, prompts the user to borrow the book.
     * When done, saves the resulting trees since searching (and possibly borrowing) will have modified them.
     *
     * Running time: O(log n) on average, where n is the number of not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @throws IOException saving the trees to files could theoretically throw an IOException
     */
    private void promptForIsbnSearch() throws IOException {
        writeToConsole("\nYou have selected Search by ISBN. Please enter the ISBN: ");
        long isbn = getLongInput();

        SplayTreeNode<Book> bookNode = isbnSearch(isbn);

        if (bookNode == null) {
            writeNewLineToConsole("Sorry, no books were found with your search term.");
        } else {
            writeNewLineToConsole("The following entry matched your search term:");
            writeLineToConsole(bookNode.data.toString());

            promptToBorrowBook(bookNode, SplayTreeUtils.ALT_COMPARISON_MODE);
        }

        saveTrees();
    }

    /**
     * Search the library for a given ISBN. Searches in unborrowed books only.
     * Modifies the ISBN splay tree regardless whether the book was found or not. If the book was found, it will be
     * the new root of the tree. If the book was not found, a book close to its presumed position in the tree will be
     * the new root of the tree.
     *
     * Running time: O(log n) on average, where n is the number of not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @param isbn the full ISBN to search for
     * @return the node containing the book found, if one is found, or null if no book was found
     */
    public SplayTreeNode<Book> isbnSearch(long isbn) {
        if (isbnSplayTree == null) {
            return null;
        }
        Book mockBook = new Book(null, null, isbn);
        isbnSplayTree = SplayTreeUtils.search(isbnSplayTree, mockBook, SplayTreeUtils.ALT_COMPARISON_MODE);

        // Splay tree root will not be the right book if it is not found
        return isbnSplayTree.data.ISBN == isbn ? isbnSplayTree : null;
    }

    /**
     * Prompts the user to borrow a found book. If they answer yes, borrow the book.
     *
     * Running time: O(log n) on average, where n is the total number of either borrowed or not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @param bookNode the splay tree node corresponding to the book to borrow
     * @param searchMode the mode that was used to find the book, i.e. the mode corresponding to author or ISBN; this
     * implies which tree it was found in
     */
    private void promptToBorrowBook(SplayTreeNode<Book> bookNode, int searchMode) {
        writeToConsole("\nWould you like to borrow this book? (y/n) ");

        boolean shouldBorrow = getYesNoInput();
        if (shouldBorrow) {
            borrowBook(bookNode, searchMode);
        }
        writeBlankLineToConsole();
    }

    /**
     * Borrow a given book by removing it from both the author and ISBN trees, and adding it to the borrowed books tree.
     * Updates all three trees.
     *
     * Running time: O(log n) on average, where n is the total number of either borrowed or not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @param bookNode the splay tree node corresponding to the book to borrow
     * @param searchMode the mode that was used to find the book, i.e. the mode corresponding to author or ISBN; this
     * implies which tree it was found in
     */
    public void borrowBook(SplayTreeNode<Book> bookNode, int searchMode) {
        if (searchMode == SplayTreeUtils.DEFAULT_COMPARISON_MODE) {
            authorSplayTree = SplayTreeUtils.delete(authorSplayTree, bookNode);

            SplayTreeNode<Book> nodeInIsbnTree = SplayTreeUtils.search(isbnSplayTree, bookNode.data, SplayTreeUtils.ALT_COMPARISON_MODE);
            isbnSplayTree = SplayTreeUtils.delete(nodeInIsbnTree, nodeInIsbnTree);
        } else {
            isbnSplayTree = SplayTreeUtils.delete(isbnSplayTree, bookNode);

            SplayTreeNode<Book> nodeInAuthorTree = SplayTreeUtils.search(authorSplayTree, bookNode.data, SplayTreeUtils.DEFAULT_COMPARISON_MODE);
            authorSplayTree = SplayTreeUtils.delete(nodeInAuthorTree, nodeInAuthorTree);
        }

        SplayTreeNode<Book> borrowedNode = new SplayTreeNode<>(bookNode.data);
        borrowedSplayTree = SplayTreeUtils.insert(borrowedSplayTree, borrowedNode, SplayTreeUtils.DEFAULT_COMPARISON_MODE);
    }

    /**
     * Displays the most recently accessed books by author and ISBN respectively.
     * If no books are available in the author and ISBN trees, it instead prints a message to that effect.
     *
     * It's possible that if a book was recently returned or if the same book was search for by author and ISBN but not
     * borrowed, the same book may be displayed twice. This is as per the assignment specifications.
     *
     * Running time: O(1)
     */
    public void popular() {
        String authorString = authorSplayTree != null ? authorSplayTree.data.toString() : "";
        String isbnString = isbnSplayTree != null ? isbnSplayTree.data.toString() : "";
        if (authorString.isEmpty() && isbnString.isEmpty()) {
            writeNewLineToConsole("Sorry, no popular books were found that are not already borrowed.");
        } else {
            writeNewLineToConsole(authorString);
            writeLineToConsole(isbnString);
        }
    }

    /**
     * Prompts the user for the author name of a book they would like to return. Calls returnBook with that name to
     * return the book.
     *
     * Running time: O(log n) on average, where n is the total number of either borrowed or not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @throws IOException returning the book will cause the updated trees to be saved to files which could
     * theoretically throw an IOException
     */
    private void promptToReturnBook() throws IOException {
        writeToConsole("\nPlease enter the author for the book you are returning: ");
        String authorName = getNonEmptyInput();
        returnBook(authorName);
    }

    /**
     * Searches the borrowed book tree to see if the supplied author name corresponds to a borrowed book. If it does,
     * return the book by removing it from the borrowed books tree and adding it back to the author and ISBN trees.
     *
     * Saves the updated trees to file. Note that the trees will be updated by searching for the author even if the
     * author is not found.
     *
     * Running time: O(log n) on average, where n is the total number of either borrowed or not borrowed books.
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @param authorName the author name of the book to return.
     * @throws IOException saving the trees to file could theoretically throw an IOException
     */
    public void returnBook(String authorName) throws IOException {
        SplayTreeNode<Book> borrowedNode = borrowedSearch(authorName);
        if (borrowedNode == null) {
            writeNewLineToConsole("Sorry, no books were borrowed with that author.");
        } else {
            writeNewLineToConsole("Thank you for returning this book.");
            borrowedSplayTree = SplayTreeUtils.delete(borrowedSplayTree, borrowedNode);

            SplayTreeNode<Book> authorNode = new SplayTreeNode<>(borrowedNode.data);
            authorSplayTree = SplayTreeUtils.insert(authorSplayTree, authorNode, SplayTreeUtils.DEFAULT_COMPARISON_MODE);

            SplayTreeNode<Book> isbnNode = new SplayTreeNode<>(borrowedNode.data);
            isbnSplayTree = SplayTreeUtils.insert(isbnSplayTree, isbnNode, SplayTreeUtils.ALT_COMPARISON_MODE);
        }
        saveTrees();
    }

    /**
     * Searches the tree of borrowed books for the given author name.
     * Modifies the borrowed splay tree regardless whether the book was found or not. If the book was found, it will be
     * the new root of the tree. If the book was not found, a book close to its presumed position in the tree will be
     * the new root of the tree.
     *
     * Running time: O(log n) on average, where n is the number of borrowed books
     * O(n) worst case (see SplayTreeUtils for details)
     *
     * @param authorName the author name to search the borrowed books for
     * @return the node containing the book found, if one is found, or null if no book was found
     */
    private SplayTreeNode<Book> borrowedSearch(String authorName) {
        if (borrowedSplayTree == null) {
            return null;
        }

        Book mockBook = new Book(null, authorName, 0);
        borrowedSplayTree = SplayTreeUtils.search(borrowedSplayTree, mockBook, SplayTreeUtils.DEFAULT_COMPARISON_MODE);

        // Splay tree root will not be the right book if it is not found
        return borrowedSplayTree.data.author.equals(authorName) ? borrowedSplayTree : null;
    }

    /**
     * Loads the library from the saved tree files if possible, or from the base library file if not. If loading from
     * the base library, immediately save those trees to file.
     *
     * Running time: O(n) if loading from saved trees, or O(n*log n) if loading from the base library.
     *
     * @throws IOException reading and writing to these files could theoretically throw an IOException (e.g. if the
     * reading / writing is interrupted; the files existing or not is handled below.)
     */
    private void loadLibrary() throws IOException {
        writeToConsole("Loading library...  ");
        try {
            loadSavedTrees();
        } catch (FileNotFoundException e) {
            loadBaseLibrary(new Scanner(new File(BASE_LIBRARY_PATH)));
            saveTrees();
        }
        writeLineToConsole("DONE.");
    }

    /**
     * Initialise/reset all the variables related to reading user input and writing output. We store the passed args
     * so that we can conveniently access them whenever an input is called for, rather than needing to pass them around.
     *
     * Running time: O(1)
     *
     * @param args an optional array of arguments. If provided, then these will be read from instead of waiting for the
     * user to supply input using System.in
     */
    private void initialiseInputOutput(String[] args) {
        this.args = args;
        userInputScanner = new Scanner(System.in);
        nextArgumentToRead = 0;
        consoleOutput = "";
    }

    /**
     * Append a string as-is both to System.out and to the tracked console output variable.
     *
     * Running time: O(1)
     *
     * @param string the string to append
     */
    private void writeToConsole(String string) {
        System.out.print(string);
        consoleOutput = consoleOutput + string;
    }

    /**
     * Append a string followed by a new line both to System.out and to the tracked console output variable.
     *
     * Running time: O(1)
     *
     * @param string the string to append
     */
    private void writeLineToConsole(String string) {
        System.out.println(string);
        consoleOutput = consoleOutput + string + "\n";
    }

    /**
     * Append a new line character to both System.out and to the tracked console output variable.
     * Convenience method/overload.
     *
     * Running time: O(1)
     */
    private void writeBlankLineToConsole() {
        writeLineToConsole("");
    }

    /**
     * Append a new line, followed by a string,  followed by a new line both to System.out and to the tracked console
     * output variable. Convenience method/overload.
     *
     * Running time: O(1)
     *
     * @param string the string to append.
     */
    private void writeNewLineToConsole(String string) {
        writeLineToConsole("\n" + string);
    }

    /**
     * Prompt the user for an input, or retrieve the next input from the passed arguments, and parse it as a long.
     * Repeat this until a long is successfully parsed, or max attempts reached. Default to zero in that case.
     *
     * Running time: O(1) per attempt parsed; blocks on user input if no arguments passed
     *
     * @return the user's input as a long
     */
    private long getLongInput() {
        Long input = null;
        for (int i = 0; i < MAX_INPUT_ATTEMPTS && input == null; i++) {
            String inputString = getNonEmptyInput();
            try {
                input = Long.parseLong(inputString);
            } catch (NumberFormatException e) {
                writeToConsole("\nOops! Please enter a number: ");
            }
        }
        return input == null ? 0 : input;
    }

    /**
     * Prompt the user for an input, or retrieve the next input from the passed arguments, and parse it as a boolean.
     * 'y' translates to true, 'n' translates to false. No other inputs (e.g. 'yes'/'no'/...) are accepted.
     * Repeat this until a boolean is successfully parsed, or max attempts reached. Default to false in that case.
     *
     * Running time: O(1) per attempt parsed; blocks on user input if no arguments passed
     *
     * @return the user's input as a boolean, assuming they entered 'y' or 'n'
     */
    private boolean getYesNoInput() {
        Boolean input = null;
        for (int i = 0; i < MAX_INPUT_ATTEMPTS && input == null; i++) {
            String inputString = getNonEmptyInput();
            if (inputString.equals("y")) {
                input = true;
            } else if (inputString.equals("n")) {
                input = false;
            } else {
                writeToConsole("\nOops! Please enter y or n: ");
            }
        }
        // Default to false if no input could be gathered after
        return input == null ? false : input;
    }

    /**
     * Prompt the user for an input, or retrieve the next input from the passed arguments.
     * Repeat this until a non-empty String is entered, or max attempts is reached. Return empty input in that case.
     *
     * Running time: O(1) per attempt parsed; blocks on user input if no arguments passed
     *
     * @return the user's input as a string
     */
    private String getNonEmptyInput() {
        String input = getInput();
        for (int i = 0; i < MAX_INPUT_ATTEMPTS && input.isEmpty(); i++) {
            writeToConsole("\nOops! Please enter what you would like to do: ");
            input = getInput();
        }
        return input;
    }

    /**
     * Prompt the user for an input, or retrieve the next input from the passed arguments.
     * If the input is retrieved from the arguments, write it to the console as if the user entered it.
     *
     * Running time: Running time: O(1); blocks on user input if no arguments passed
     *
     * @return the user's input (as a string)
     */
    private String getInput() {
        String input;
        if (nextArgumentToRead < args.length) {
            input = args[nextArgumentToRead];
            nextArgumentToRead++;
            writeToConsole(input);
        } else if (args.length > 0) {
            // Assume that the program is being controlled by an args array but the args array didn't end in "exit"
            // Assume that there is no user who will provide additional input, so we should quit here.
            return EXIT_CHOICE;
        } else {
            input = userInputScanner.nextLine();
        }
        return input;
    }

    /**
     * Read a tab-separated input from a scanner. Parse each line as a book and insert the books into the author
     * and ISBN trees.
     *
     * Running time: O(n*log n) where n is the number of books in the base library, since each book costs O(log n) to
     * insert into each tree.
     *
     * @param scanner a scanner pointing at the tab-separated input
     */
    public void loadBaseLibrary(Scanner scanner) {
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            String[] bookFields = line.split("\\t");
            if (bookFields.length != 3) {
                writeLineToConsole("Badly formatted line found in base library, skipping: " + line);
                continue;
            }

            Book book = new Book(bookFields[0], bookFields[1], Long.parseLong(bookFields[2]));
            authorSplayTree = SplayTreeUtils.insert(authorSplayTree, new SplayTreeNode<>(book), 0);
            isbnSplayTree = SplayTreeUtils.insert(isbnSplayTree, new SplayTreeNode<>(book), 1);
        }
    }

    /**
     * Load the saved author, ISBN and borrowed trees from their respective files.
     *
     * Running time: O(n) where n is the number of nodes in all three trees together
     *
     * @throws IOException theoretically, accessing these files could throw an IOException
     */
    private void loadSavedTrees() throws IOException {
        authorSplayTree = loadSavedTree(AUTHOR_TREE_PATH);
        isbnSplayTree = loadSavedTree(ISBN_TREE_PATH);
        borrowedSplayTree = loadSavedTree(BORROWED_TREE_PATH);
    }

    /**
     * Load a given file and parse it as a tree of books. Assume that the file exists and is not empty (if the tree is
     * empty, the file should still contain that information).
     *
     * Running time: O(n) where n is the number of nodes in the tree
     *
     * @param treePath the path to the file containing the tree
     * @return the parsed tree
     * @throws IOException Accessing this file could throw an IOException. Also throws FileNotFoundException if the file
     * does not exist or is empty.
     */
    private SplayTreeNode<Book> loadSavedTree(String treePath) throws IOException {
        File file = new File(treePath);
        try (FileReader reader = new FileReader(file)) {
            if (!file.isFile() || file.length() == 0) {
                throw new FileNotFoundException();
            }
            return stringToNode(reader);
        }
    }

    /**
     * Saves the author, ISBN and splay trees to file.
     *
     * Running time: O(n) where n is the number of nodes in all three trees together
     *
     * @throws IOException theoretically, accessing these files could throw an IOException
     */
    public void saveTrees() throws IOException {
        saveTree(AUTHOR_TREE_PATH, authorSplayTree);
        saveTree(ISBN_TREE_PATH, isbnSplayTree);
        saveTree(BORROWED_TREE_PATH, borrowedSplayTree);
    }

    /**
     * Save a given tree to the file specified by the path. Overwrite its contents if the file exists.
     *
     * Running time: O(n) where n is the number of nodes in the tree.
     *
     * @param treePath the path to the file that should contain the tree
     * @param tree the tree to save
     * @throws IOException theoretically, accessing this file could throw an IOException
     */
    private void saveTree(String treePath, SplayTreeNode<Book> tree) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(treePath))) {
            writer.write(nodeToString(tree));
        }
    }

    /**
     * Create a machine-readable (and semi-human-readable) string representation of a node containing a book
     * and its children recursively, using post-order traversal.
     * Differs from the toString() method of the node firstly in that this format is unambiguous to parse and secondly
     * in that it contains all the node's children and their children.
     *
     * Example: "{data: {title: "A Book", author: "Antonella", ISBN: 123456789}, left: {}, right: {}}"
     *
     * Running time: O(n) where n is the number of nodes in this (sub)tree.
     *
     * @param node the node to write out
     * @return the string representation of that node for storage purposes
     */
    public static String nodeToString(SplayTreeNode<Book> node) {
        // Post-order traversal
        if (node == null) {
            return "{}";
        }
        String leftString = nodeToString(node.left);
        String rightString = nodeToString(node.right);
        String bookString = node.data == null ? "NULL" : bookToString(node.data);
        return String.format("{%s%s%s%s%s%s}",
                DATA_PREFIX, bookString, LEFT_PREFIX, leftString, RIGHT_PREFIX, rightString);
    }

    /**
     * Parse the string representation generated by nodeToString() as a node containing a book, including its children,
     * recursively.
     * Will not parse the input of the node's own toString() method.
     *
     * Expects input similar to the following example:
     * "{data: {title: "A Book", author: "Antonella", ISBN: 123456789}, left: {}, right: {}}"
     *
     * Running time: O(n) where n is the number of nodes in this (sub)tree.
     *
     * @param reader a reader containing the characters to read in. Does not close this reader - assumes that it will be
     * closed by whatever supplied the reader. May not read the entire contents of the reader.
     * @return the parsed node, with book data and children, or null if the input is "{}"
     * @throws IOException the reader may throw an IOException
     */
    public static SplayTreeNode<Book> stringToNode(Reader reader) throws IOException {
        char c = (char) reader.read();
        if (c == '{') {
            SplayTreeNode<Book> node = null;

            c = getNextChar(reader);
            if (c != '}') {
                char[] dataPrefix = new char[DATA_PREFIX.length()];
                dataPrefix[0] = c;
                // Ignore result of reader.read since if it's -1 then the array won't be filled so won't be equal
                reader.read(dataPrefix, 1, DATA_PREFIX.length() - 1);
                if (Arrays.equals(dataPrefix, DATA_PREFIX.toCharArray())) {
                    // assume node is not null (assume null otherwise)
                    node = new SplayTreeNode<>(stringToBook(reader));

                    char[] leftPrefix = new char[LEFT_PREFIX.length()];
                    // Ignore result of reader.read since if it's -1 then the array won't be filled so won't be equal
                    reader.read(leftPrefix);
                    if (Arrays.equals(leftPrefix, LEFT_PREFIX.toCharArray())) {
                        node.left = stringToNode(reader);
                        if (node.left != null) {
                            node.left.parent = node;
                        }
                    } else {
                        throw getFormatException();
                    }

                    char[] rightPrefix = new char[RIGHT_PREFIX.length()];
                    // Ignore result of reader.read since if it's -1 then the array won't be filled so won't be equal
                    reader.read(rightPrefix);
                    if (Arrays.equals(rightPrefix, RIGHT_PREFIX.toCharArray())) {
                        node.right = stringToNode(reader);
                        if (node.right != null) {
                            node.right.parent = node;
                        }
                    } else {
                        throw getFormatException();
                    }
                }
                c = getNextChar(reader);
                if (c != '}') {
                    throw getFormatException();
                }
            }
            return node;
        } else {
            throw getFormatException();
        }
    }

    /**
     * Create a machine-readable (and semi-human-readable) string representation of a book.
     * Differs from the toString() method of the book in that this format is unambiguous to parse.
     *
     * Example: "{title: "A Book", author: "Antonella", ISBN: 123456789}"
     *
     * Running time: O(1)
     *
     * @param book the book to write out
     * @return the string representation of that book for storage purposes
     */
    public static String bookToString(Book book) {
        if (book == null) {
            return "{}";
        }
        return String.format("{%s\"%s\"%s\"%s\"%s%d}",
                TITLE_PREFIX, book.title, AUTHOR_PREFIX, book.author, ISBN_PREFIX, book.ISBN);
    }

    /**
     * Parse the string representation generated by bookToString().
     * Will not parse the input of the book's own toString() method.
     *
     * Expects input similar to the following example:
     * "title: "A Book", author: "Antonella", ISBN: 123456789}"
     *
     * Running time: O(1)
     *
     * @param reader a reader containing the characters to read in. Does not close this reader - assumes that it will be
     * closed by whatever supplied the reader. May not read the entire contents of the reader.
     * @return the parsed book, or null if the input is "{}"
     * @throws IOException the reader may throw an IOException
     */
    public static Book stringToBook(Reader reader) throws IOException {
        char c = (char) reader.read();
        if (c == '{') {
            Book book = null;

            c = getNextChar(reader);
            if (c != '}') {
                char[] titlePrefix = new char[TITLE_PREFIX.length()];
                titlePrefix[0] = c;
                // Ignore result of reader.read since if it's -1 then the array won't be filled so won't be equal
                reader.read(titlePrefix, 1, TITLE_PREFIX.length() - 1);
                if (Arrays.equals(titlePrefix, TITLE_PREFIX.toCharArray())) {
                    // assume book is not null (assume null otherwise)
                    String title = "", author = "", isbnString = "";
                    c = getNextChar(reader);
                    if (c != '"') {
                        throw getFormatException();
                    }
                    c = getNextChar(reader);
                    while (c != '"') {
                        title += c;
                        c = getNextChar(reader);
                    }

                    char[] authorPrefix = new char[AUTHOR_PREFIX.length()];
                    // Ignore result of reader.read since if it's -1 then the array won't be filled so won't be equal
                    reader.read(authorPrefix);
                    if (Arrays.equals(authorPrefix, AUTHOR_PREFIX.toCharArray())) {
                        c = getNextChar(reader);
                        if (c != '"') {
                            throw getFormatException();
                        }

                        c = getNextChar(reader);
                        while (c != '"') {
                            author += c;
                            c = getNextChar(reader);
                        }
                    } else {
                        throw getFormatException();
                    }

                    char[] isbnPrefix = new char[ISBN_PREFIX.length()];
                    // Ignore result of reader.read since if it's -1 then the array won't be filled so won't be equal
                    reader.read(isbnPrefix);
                    if (Arrays.equals(isbnPrefix, ISBN_PREFIX.toCharArray())) {
                        c = getNextChar(reader);
                        while (Character.isDigit(c)) {
                            isbnString += c;
                            c = getNextChar(reader);
                        }
                    } else {
                        throw getFormatException();
                    }
                    book = new Book(title, author, Long.parseLong(isbnString));
                }
                // Check that last character read by ISBN was actually a closing bracket
                if (c != '}') {
                    throw getFormatException();
                }
            }
            return book;
        } else {
            throw getFormatException();
        }
    }

    /**
     * Helper method to either get the next character from a reader or throw an exception if there is no next character.
     *
     * Running time: O(1)
     *
     * @param reader the reader to read from
     * @return the next character of the reader
     * @throws IOException the reader may throw an IOException
     */
    private static char getNextChar(Reader reader) throws IOException {
        int nextCharAsInt = reader.read();
        if (nextCharAsInt == -1) {
            throw getFormatException();
        }
        return (char) nextCharAsInt;
    }

    /**
     * Helper method to construct a standard runtime exception that the input that is being parsed did not conform to
     * the expected format. If this input were being generated by the user, it would be nice to give a more specific
     * error message such as what was missing, but since the input and output are either fixed (in the case of the base
     * library) or being generated by this program, this exception is never actually going to be encountered or read by
     * a human in this project's current state. If that changes, this method can be replaced.
     *
     * Running time: O(1)
     *
     * @return a standard IllegalArgumentException
     */
    private static IllegalArgumentException getFormatException() {
        return new IllegalArgumentException("Input is not in expected format.");
    }
}
