package edu.grinnell.csc207.compression;

/**
 * BinaryTreeClass
 */
public class BinaryTree {

    private TreeNode root;

    /**
     * BinaryTree Constructor
     */
    public BinaryTree() {
        root = null;
    }

    /**
     * @return root of tree
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * Sets the root of tree
     * 
     * @param root the new root of tree
     */
    public void setRoot(TreeNode root) {
        this.root = root;
    }



}
