package edu.grinnell.csc207.compression;

public class BinaryTree {
    

    public TreeNode root;

    public BinaryTree() {
        root = null;
    }


    public void insert(int bit, short characterVal, boolean left, TreeNode root) {
        if (left) {
            root.left = new TreeNode(bit, characterVal);
        } else {
            root.right = new TreeNode(bit, characterVal);
        }
    }


}
