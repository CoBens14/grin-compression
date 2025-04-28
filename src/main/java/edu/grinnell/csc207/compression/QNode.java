package edu.grinnell.csc207.compression;

/**
 * QNode Class
 */
public class QNode implements Comparable {
    public short character;
    public int amount;
    public TreeNode tNode;

    /**
     * Constructor for QNode
     * 
     * @param character the character of QNode
     * @param amount the amount of times character has appeared
     * @param tNode the TreeNode corresponding to QNode
     */
    public QNode(short character, int amount, TreeNode tNode) {
        this.character = character;
        this.amount = amount;
        this.tNode = tNode;
    }

    /**
     * Comparison method
     */
    @Override
    public int compareTo(Object o) {
        QNode other;
        if (o instanceof QNode) {
            other = (QNode) o;
            if (amount > other.amount) {
                return 1;
            } else if (amount == other.amount) {
                return 0;
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }
}
