package library.service;

import library.model.Book;
import library.model.SplayTreeNode;
import org.springframework.stereotype.Service;

@Service
public class BookSplayTreeService {

    private static final int AUTHOR_MODE = 0;
    private static final int ISBN_MODE = 1;

    /**
     * Perform a zig operation on the given node, i.e. perform a single right rotation on its parent to move it up
     * into the position of its parent. Assumes that the node is the left child of its parent.
     *
     * Package-private for testing.
     *
     * @param node the node to move into its parent's position, which is a left child of its parent
     */
    /*package*/ void zig(SplayTreeNode<Book> node) {
        SplayTreeNode<Book> parent = node.parent;
        parent.left = node.right;
        if (node.right != null) {
            node.right.parent = parent;
        }
        node.parent = parent.parent;
        if (parent.parent != null && parent == parent.parent.right) {
            // parent was a right child
            parent.parent.right = node;
        } else if (parent.parent != null) {
            // parent was a left child
            parent.parent.left = node;
        }
        node.right = parent;
        parent.parent = node;
    }

    /**
     * Perform a zag operation on the given node, i.e. perform a single left rotation on its parent to move it up
     * into the position of its parent. Assumes that the node is the right child of its parent.
     *
     * Package-private for testing.
     *
     * @param node the node to move into its parent's position, which is a right child of its parent
     */
    /*package*/ void zag(SplayTreeNode<Book> node) {
        SplayTreeNode<Book> parent = node.parent;
        parent.right = node.left;
        if (node.left != null) {
            node.left.parent = parent;
        }
        node.parent = parent.parent;
        if (parent.parent != null && parent == parent.parent.left) {
            // parent was a left child
            parent.parent.left = node;
        } else if (parent.parent != null) {
            // parent was a right child
            parent.parent.right = node;
        }
        node.left = parent;
        parent.parent = node;
    }

    /**
     * Splay a given node to the root of the tree by performing zig, zag, zigzig, zigzag, zagzig or zagzag operations on
     * it until it is the root of the tree.
     *
     * @param node the node to splay to the root
     */
    private void splayToRoot(SplayTreeNode<Book> node) {
        // Case 1: node is root; do nothing
        while (node != null && node.parent != null) {
            if (node.parent.parent == null) {
                // Case 2: node is child of root
                if (node.parent.left == node) {
                    // Left child: do right rotation
                    zig(node);
                } else {
                    // Right child: do left rotation
                    zag(node);
                }
            } else {
                // Case 3: node has a parent and a grandparent
                if (node.parent.left == node && node.parent.parent.left == node.parent) {
                    // left child of a left child
                    zig(node.parent);
                    zig(node);
                } else if (node.parent.right == node && node.parent.parent.right == node.parent) {
                    // right child of a right child
                    zag(node.parent);
                    zag(node);
                } else if (node.parent.right == node && node.parent.parent.left == node.parent) {
                    // right child of a left child
                    zag(node);
                    zig(node);
                } else {
                    // left child of a right child
                    zig(node);
                    zag(node);
                }
            }
        }
    }

    /**
     * Search for the node which has data matching the search item. Compare nodes using the mode specified (see the mode
     * constants at the top of the class.
     *
     * The tree will be modified whether a matching node is found or not: either that node or the last node searched will
     * be splayed to the root.
     *
     * @param root the root of the tree
     * @param searchKey the data to search for
     * @return the new root of the tree
     */
    public SplayTreeNode<Book> searchByAuthor(SplayTreeNode<Book> root, Book searchKey) {
        return search(root, searchKey, AUTHOR_MODE);
    }

    /**
     * Search for the node which has data matching the search item. Compare nodes using the mode specified (see the mode
     * constants at the top of the class.
     *
     * The tree will be modified whether a matching node is found or not: either that node or the last node searched will
     * be splayed to the root.
     *
     * @param root the root of the tree
     * @param searchKey the data to search for
     * @return the new root of the tree
     */
    public SplayTreeNode<Book> searchByIsbn(SplayTreeNode<Book> root, Book searchKey) {
        return search(root, searchKey, ISBN_MODE);
    }

    /**
     * Search for the node which has data matching the search item. Compare nodes using the mode specified (see the mode
     * constants at the top of the class.
     *
     * The tree will be modified whether a matching node is found or not: either that node or the last node searched will
     * be splayed to the root.
     *
     * @param root the root of the tree
     * @param searchKey the data to search for
     * @param mode the mode by which to compare nodes
     * @return the new root of the tree
     */
    private SplayTreeNode<Book> search(SplayTreeNode<Book> root, Book searchKey, int mode) {
        SplayTreeNode<Book> currentParent = null;
        SplayTreeNode<Book> current = root;
        while (current != null) {
            currentParent = current;
            int comparison = mode == AUTHOR_MODE
                    ? searchKey.compareByAuthor(current.data)
                    : searchKey.compareByISBN(current.data);
            if (comparison < 0) {
                // item is smaller, so will be to the left
                current = current.left;
            } else if (comparison > 0) {
                // item is larger, so will be to the right
                current = current.right;
            } else {
                break; // item found
            }
        }
        if (current == null) {
            current = currentParent;
        }
        splayToRoot(current);
        return current;
    }

    /**
     * Locate the parent of where a node with the given data would be inserted into the tree. If a node with that data
     * already exists in the tree, returns the deepest node with that data.
     *
     * Note that this method does not splay this node to the root, this can be done elsewhere if desired.
     *
     * @param root the root of the tree
     * @param searchKey the data to find the hypothetical parent of
     * @param mode  the mode by which to compare nodes
     * @return the hypothetical parent node
     */
    private SplayTreeNode<Book> locateParent(SplayTreeNode<Book> root, Book searchKey, int mode) {
        SplayTreeNode<Book> currentParent = null;
        SplayTreeNode<Book> current = root;
        while (current != null) {
            currentParent = current;
            int comparison = mode == AUTHOR_MODE
                    ? searchKey.compareByAuthor(current.data)
                    : searchKey.compareByISBN(current.data);
            if (comparison < 0) {
                // item is smaller, so it will be to the left
                current = current.left;
            } else {
                // item is larger, so it will be to the right
                current = current.right;
            }
        }
        return currentParent;
    }

    /**
     * Insert a given node that is not yet attached to the tree into the tree in the correct position, then splay this
     * new node to the root.
     *
     * @param root the root of the tree
     * @param node the node to insert
     * @return the new root of the tree
     */
    public SplayTreeNode<Book> insertByAuthor(SplayTreeNode<Book> root, SplayTreeNode<Book> node) {
        return insert(root, node, AUTHOR_MODE);
    }

    /**
     * Insert a given node that is not yet attached to the tree into the tree in the correct position, then splay this
     * new node to the root.
     *
     * @param root the root of the tree
     * @param node the node to insert
     * @return the new root of the tree
     */
    public SplayTreeNode<Book> insertByISBN(SplayTreeNode<Book> root, SplayTreeNode<Book> node) {
        return insert(root, node, ISBN_MODE);
    }

    /**
     * Insert a given node that is not yet attached to the tree into the tree in the correct position, then splay this
     * new node to the root.
     *
     * @param root the root of the tree
     * @param node the node to insert
     * @param mode the mode by which to compare nodes
     * @return the new root of the tree
     */
    private SplayTreeNode<Book> insert(SplayTreeNode<Book> root, SplayTreeNode<Book> node, int mode) {
        SplayTreeNode<Book> parentForNode = locateParent(root, node.data, mode);

        node.parent = parentForNode;
        if (parentForNode == null) {
            // node is new root
            return node;
        } else {
            int comparison = mode == AUTHOR_MODE
                    ? node.data.compareByAuthor(parentForNode.data)
                    : node.data.compareByISBN(parentForNode.data);
            if (comparison < 0) {
                // item is smaller, so will be to the left
                parentForNode.left = node;
            } else {
                // item is larger, so will be to the right
                parentForNode.right = node;
            }
            splayToRoot(node);
            return node;
        }
    }

    /**
     * Delete a given node in a tree from that tree. Splay it to the root as part of this process, thereby rearranging
     * the tree.
     *
     * @param root the root of the tree to delete it from (note: it is not validated that the node is actually in the
     * tree descended from this root; if this occurs, that other tree will be modified instead)
     * @param node the node to delete
     * @return the new root of the tree
     */
    public SplayTreeNode<Book> delete(SplayTreeNode<Book> root, SplayTreeNode<Book> node) {
        splayToRoot(node);
        SplayTreeNode<Book> left = node.left;
        SplayTreeNode<Book> right = node.right;

        if (left != null) {
            // Make the left subtree the new root
            left.parent = null;
            // To preserve the BST property when we re-attach the right subtree to the left subtree,
            // we need to make its max the new root
            SplayTreeNode<Book> leftMax = locateMax(left);
            splayToRoot(leftMax);
            leftMax.right = right;
            if (right != null) {
                // Attach the right subtree
                right.parent = leftMax;
            }
            return leftMax;
        } else {
            // There is no left subtree, so make the right subtree the new root (which may also be null)
            if (right != null) {
                right.parent = null;
            }
            return right;
        }
    }

    /**
     * Locate, but do not splay, the maximum of a given (sub)tree.
     *
     * @param root the root of the (sub)tree
     * @return the maximum node
     */
    private SplayTreeNode<Book> locateMax(SplayTreeNode<Book> root) {
        SplayTreeNode<Book> current = root;
        while (current.right != null) {
            // There is a larger element to the right
            current = current.right;
        }
        return current;
    }


}