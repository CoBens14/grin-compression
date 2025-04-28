package edu.grinnell.csc207.compression;

/**
 * TreeNode class
 */
public class TreeNode implements Comparable {
    private int bit;
    private short character;

    private TreeNode left;
    private TreeNode right;

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

    /**
     * @return the bit
     */
    public int getBit() {
        return bit;
    }

    /**
     * @param bit new bit value
     */
    public void setBit(int bit) {
        this.bit = bit;
    }

    /**
     * @return the character
     */
    public short getCharacter() {
        return character;
    }

    /**
     * @param character new character
     */
    public void setCharacter(short character) {
        this.character = character;
    }

    /**
     * @return left node
     */
    public TreeNode getLeft() {
        return left;
    }

    /**
     * @param left new left node
     */
    public void setLeft(TreeNode left) {
        this.left = left;
    }

    /**
     * @return right node
     */
    public TreeNode getRight() {
        return right;
    }

    /**
     * @param right new right node
     */
    public void setRight(TreeNode right) {
        this.right = right;
    }

}