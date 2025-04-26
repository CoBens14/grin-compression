package edu.grinnell.csc207.compression;

import java.util.Collection;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits.  However, we also need to encode a special EOF character to
 * denote the end of a .grin file.  Thus, we need 9 bits to store each
 * byte value.  This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {


    BinaryTree huffTree;

    PriorityQueue<QNode> priorQ;

    /**
     * Constructs a new HuffmanTree from a frequency map.
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree (Map<Short, Integer> freqs) {
        TreeNode root = null;
        // add all characters from map
        for (int i = 0; i < 256; i++) {
            if (freqs.get((short) i) != null) {
                priorQ.add(new QNode((short) i, freqs.get((short) i), null));
            }
        }
        // add EOF char
        priorQ.add(new QNode((short) 256, 1, null));
        
        // create tree
        while (priorQ.size() > 1) {
            int bitVal = 1;
            if (priorQ.peek().character < 257) { 
                bitVal = 0;
            }
            TreeNode left = new TreeNode(bitVal, priorQ.peek().character);
            int freq1 = priorQ.remove().amount;
            if (priorQ.peek().character < 257) { 
                bitVal = 0;
            }
            TreeNode right = new TreeNode(bitVal, priorQ.peek().character);
            int freq2 = priorQ.remove().amount;
            root = new TreeNode(1, (short) 300);
            root.left = left;
            root.right = right;
            priorQ.add(new QNode((short) 300, freq1 + freq2, root));   
        }
        huffTree.root = root;
    }

    private void constructH(BitInputStream in, TreeNode root) {
        int nextBit;
        nextBit = in.readBit();
        if (nextBit == 1) {
            root = new TreeNode(nextBit, (short) 0);
            constructH(in, root.left);
            constructH(in, root.right);
        } else {
            root = new TreeNode(nextBit, (short) in.readBits(9));
        }

    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree (BitInputStream in) {
        huffTree = new BinaryTree();
        constructH(in, huffTree.root);
    }

    private void serializeH(BitOutputStream out, TreeNode root) {
        if (root.left != null) {
            out.writeBit(root.bit);
            serializeH(out, root.left);
            serializeH(out, root.right);
        } else {
            out.writeBits(root.character, 9);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * @param out the output file as a BitOutputStream
     */
    public void serialize (BitOutputStream out) {
        serializeH(out, huffTree.root);
    }
   
    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * @param in the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode (BitInputStream in, BitOutputStream out) {
        out.writeBit(0x736);
        serialize(out);
        //writeChars(); // need to write serialized chars to file
        in.close();
        out.close();

    }

    private short traverseForChar(BitInputStream in, TreeNode root) {
        if (root.bit != 0) {
            if (root.left != null) {
                return traverseForChar(in, root.left);
            } else {
                return traverseForChar(in, root.right);
            }

        } else {
            return root.character;
        }
    }


    private void decodeText(BitInputStream in, BitOutputStream out) {
        while (in.hasBits()) {
            out.writeBits(traverseForChar(in, huffTree.root), 8);
        }
    }

    private void checkForGrin(BitInputStream in) {
        if (in.readBits(32) != 1846) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * @param in the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode (BitInputStream in, BitOutputStream out) {
        checkForGrin(in);
        constructH(in, huffTree.root);
        decodeText(in, out);
        in.close();
        out.close();
    }
}
