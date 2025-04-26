package edu.grinnell.csc207.compression;

public class QNode {
    public short character;
    public int amount;
    public TreeNode tNode;

    public QNode(short character, int amount, TreeNode tNode) {
        this.character = character;
        this.amount = amount;
        this.tNode = tNode;
    }

    public int compareTo(QNode other) {
        if (amount > other.amount) {
            return 1;
        } else if (amount == other.amount) {
            return 0;
        } else {
            return -1;
        }
    }
}
