package pa2.SPL_DIGITAL_LIB;

/**
 * Name: Hayley Ross
 * Email: hayleyross@brandeis.edu
 * Assignment: PA2_Digital_Library
 *
 * A node in in a splay tree.
 * @param <T> the type of its data T, which must implement Comparable so that the node can be inserted into a splay tree
 */
public class SplayTreeNode<T extends Comparable<T>> {

    public T data;
    public SplayTreeNode<T> left;
    public SplayTreeNode<T> right;
    public SplayTreeNode<T> parent;

    /**
     * Construct a splay tree node with null data and no children.
     *
     * Running time: O(1)
     */
    public SplayTreeNode() {
    }

    /**
     * Construct a splay tree node with the data prepopulated
     *
     * Running time: O(1)
     *
     * @param data the data to give the node
     */
    public SplayTreeNode(T data) {
        this.data = data;
    }

    /**
     * Running time: O(1)
     *
     * @return a human-readable representation of the node. Displays its data and the data of its children, but not its
     * children's children.
     */
    @Override
    public String toString() {
        String thisData = data == null ? "NULL" : data.toString();
        String leftData = left == null || left.data == null ? "NULL" : left.data.toString();
        String rightData = right == null || right.data == null ? "NULL" : right.data.toString();
        return String.format("%s\nLEFT    |    %s\nRIGHT   |    %s",
                thisData, leftData, rightData);
    }
}