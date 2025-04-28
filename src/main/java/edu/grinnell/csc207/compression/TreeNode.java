package edu.grinnell.csc207.compression;

/**
 * TreeNode class
 */
public class TreeNode implements Comparable {
    public int bit;
    public short character;

    public TreeNode left;
    public TreeNode right;

    /**
     * @param bit the bit of the TreeNode
     * @param character the character of the TreeNode
     */
    public TreeNode(int bit, short character) {
        this.bit = bit;
        this.character = character;
        this.left = null;
        this.right = null;
    }

    /**
     * Comparison method
     */
    @Override
    public int compareTo(Object o) {
        TreeNode other;
        if (o instanceof TreeNode) {
            other = (TreeNode) o;
            if (other.bit == bit && other.character == character) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return -1;
        }
    }

}