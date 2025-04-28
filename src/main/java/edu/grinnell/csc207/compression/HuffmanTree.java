package edu.grinnell.csc207.compression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;

/**
 * A HuffmanTree derives a space-efficient coding of a collection of byte
 * values.
 *
 * The huffman tree encodes values in the range 0--255 which would normally
 * take 8 bits. However, we also need to encode a special EOF character to
 * denote the end of a .grin file. Thus, we need 9 bits to store each
 * byte value. This is fine for file writing (modulo the need to write in
 * byte chunks to the file), but Java does not have a 9-bit data type.
 * Instead, we use the next larger primitive integral type, short, to store
 * our byte values.
 */
public class HuffmanTree {

    BinaryTree huffTree;

    PriorityQueue<QNode> priorQ;

    /**
     * Constructs a new HuffmanTree from a frequency map.
     * 
     * @param freqs a map from 9-bit values to frequencies.
     */
    public HuffmanTree(Map<Short, Integer> freqs) {
        TreeNode cur = null;
        priorQ = new PriorityQueue<>();
        huffTree = new BinaryTree();
        // add all characters from map
        for (int i = 0; i < 256; i++) {
            if (freqs.get((short) i) != null) {
                priorQ.add(new QNode((short) i, freqs.get((short) i), null));
            }
        }
        // add EOF char
        priorQ.add(new QNode((short) 256, 1, null));

        int offset = 0;
        TreeNode left, right;

        // create tree
        while (priorQ.size() > 1) {
            int bitVal = 1;
            if (priorQ.peek().getCharacter() < 257) {
                bitVal = 0;
            }
            if (priorQ.peek().gettNode() != null) {
                left = priorQ.peek().gettNode();
            } else {
                left = new TreeNode(bitVal, priorQ.peek().getCharacter());
            }
            int freq1 = priorQ.remove().getAmount();
            if (priorQ.peek().getCharacter() < 257) {
                bitVal = 0;
            }
            if (priorQ.peek().gettNode() != null) {
                right = priorQ.peek().gettNode();
            } else {
                right = new TreeNode(bitVal, priorQ.peek().getCharacter());
            }
            int freq2 = priorQ.remove().getAmount();
            cur = new TreeNode(1, (short) (300 + offset));
            cur.setLeft(left);
            cur.setRight(right);
            priorQ.add(new QNode((short) (300 + offset), freq1 + freq2, cur));
            offset += 1;
        }
        huffTree.setRoot(cur);
    }

    /**
     * Helper that helps construct tree
     * 
     * @param in  the InputStream used to read in bit information
     * @param cur the current TreeNode
     * @return the TreeNode needed based on inputStream
     */
    private TreeNode constructH(BitInputStream in, TreeNode cur) {
        int nextBit;
        nextBit = in.readBit();
        TreeNode input;
        if (nextBit == 1) {
            input = new TreeNode(nextBit, (short) 300);
            input.setLeft(constructH(in, input.getLeft()));
            input.setRight(constructH(in, input.getRight()));
            ;
        } else {
            input = new TreeNode(nextBit, (short) in.readBits(9));
        }
        return input;

    }

    /**
     * Constructs a new HuffmanTree from the given file.
     * 
     * @param in the input file (as a BitInputStream)
     */
    public HuffmanTree(BitInputStream in) {
        checkForGrin(in);
        huffTree = new BinaryTree();
        huffTree.setRoot(constructH(in, huffTree.getRoot()));
    }

    /**
     * Helper that writes tree as bits
     * 
     * @param out the output stream that bits are being written to
     * @param cur the current TreeNode
     */
    private void serializeH(BitOutputStream out, TreeNode cur) {
        if (cur != null && cur.getBit() == 1) {
            out.writeBit(cur.getBit());
            serializeH(out, cur.getLeft());
            serializeH(out, cur.getRight());
        } else if (cur != null) {
            out.writeBit(cur.getBit());
            out.writeBits(cur.getCharacter(), 9);
        }
    }

    /**
     * Writes this HuffmanTree to the given file as a stream of bits in a
     * serialized format.
     * 
     * @param out the output file as a BitOutputStream
     */
    public void serialize(BitOutputStream out) {
        serializeH(out, huffTree.getRoot());
    }

    /**
     * Finds given char in tree
     * 
     * @param neededChar the char being searched for
     * @return returns tree route as series of bits
     */
    private List<Integer> findChar(short neededChar) {
        Stack<TreeNode> stack = new Stack<>();
        List<Short> checkedList = new ArrayList<>();
        List<Integer> ret = new ArrayList<>();

        stack.add(huffTree.getRoot());

        while (stack.peek().getCharacter() != neededChar) {
            if (stack.peek().getLeft() != null
                    && !(checkedList.contains(stack.peek().getLeft().getCharacter()))) {
                checkedList.add(stack.peek().getLeft().getCharacter());
                stack.add(stack.peek().getLeft());
                ret.add(0);
            } else if (stack.peek().getRight() != null
                    && !(checkedList.contains(stack.peek().getRight().getCharacter()))) {
                checkedList.add(stack.peek().getRight().getCharacter());
                stack.add(stack.peek().getRight());
                ret.add(1);
            } else {
                stack.pop();
                if (!ret.isEmpty()) {
                    ret.remove(ret.size() - 1);
                }
            }
        }

        return ret;
    }

    /**
     * Writes chars to outputStream
     * 
     * @param in  the input stream used to determine chars
     * @param out the output stream chars are being written to
     */
    private void writeChars(BitInputStream in, BitOutputStream out) {
        short nextChar;
        List<Integer> bitSeries;
        while (in.hasBits()) {
            nextChar = (short) in.readBits(8);
            bitSeries = findChar(nextChar);
            for (int i = 0; i < bitSeries.size(); i++) {
                out.writeBit(bitSeries.get(i));
            }
        }
        // Add EOF
        nextChar = (short) 256;
        bitSeries = findChar(nextChar);
        for (int i = 0; i < bitSeries.size(); i++) {
            out.writeBit(bitSeries.get(i));
        }

    }

    /**
     * Encodes the file given as a stream of bits into a compressed format
     * using this Huffman tree. The encoded values are written, bit-by-bit
     * to the given BitOuputStream.
     * 
     * @param in  the file to compress.
     * @param out the file to write the compressed output to.
     */
    public void encode(BitInputStream in, BitOutputStream out) {
        out.writeBits(1846, 32);
        serialize(out);
        writeChars(in, out);
        in.close();
        out.close();

    }

    /**
     * Traverse tree for given char
     * 
     * @param in  the InputStream used for reading in chars
     * @param cur the current TreeNode being searched
     * @return a short of
     */
    private short traverseForChar(BitInputStream in, TreeNode cur) {
        int nextBit;
        if (cur.getLeft() == null) {
            return cur.getCharacter();
        } else {
            nextBit = in.readBit();
            if (nextBit == 0) {
                return traverseForChar(in, cur.getLeft());
            } else {
                return traverseForChar(in, cur.getRight());
            }
        }
    }

    /**
     * Writes chars to outputStream
     * 
     * @param in  the InputStream that stores char information
     * @param out the OutPutStream used for writing chars to file
     */
    private void decodeText(BitInputStream in, BitOutputStream out) {
        short nextChar = traverseForChar(in, huffTree.getRoot());
        while (nextChar != 256) {
            out.writeBits(nextChar, 8);
            nextChar = traverseForChar(in, huffTree.getRoot());
        }
    }

    /**
     * Ensure file is a Grin file
     * 
     * @param in the inputStream of file
     */
    private void checkForGrin(BitInputStream in) {
        int i = in.readBits(32);
        if (i != 1846) {
            System.out.println("Input file must be a Grin file");
            throw new IllegalArgumentException();
        }
    }

    /**
     * Decodes a stream of huffman codes from a file given as a stream of
     * bits into their uncompressed form, saving the results to the given
     * output stream. Note that the EOF character is not written to out
     * because it is not a valid 8-bit chunk (it is 9 bits).
     * 
     * @param in  the file to decompress.
     * @param out the file to write the decompressed output to.
     */
    public void decode(BitInputStream in, BitOutputStream out) {
        decodeText(in, out);
        in.close();
        out.close();
    }
}
