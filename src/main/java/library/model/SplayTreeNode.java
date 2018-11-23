package library.model;

/**
 * A generic class representing a node in a splay tree. It should implement comparable since
 * splay trees are binary search trees, i.e. need to be sorted.
 * @param <T> the type of the data in the node
 */
public class SplayTreeNode<T extends Comparable<T>> {

    public T data;
    public SplayTreeNode<T> left;
    public SplayTreeNode<T> right;
    public SplayTreeNode<T> parent;

    public SplayTreeNode(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        String thisData = data == null ? "NULL" : data.toString();
        String leftData = left == null || left.data == null ? "NULL" : left.data.toString();
        String rightData = right == null || right.data == null ? "NULL" : right.data.toString();
        return String.format("%s\nLEFT: %s\nRIGHT: %s",
                thisData, leftData, rightData);
    }
}