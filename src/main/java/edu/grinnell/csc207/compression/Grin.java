package edu.grinnell.csc207.compression;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * The driver for the Grin compression program.
 */
public class Grin {
    /**
     * Decodes the .grin file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * 
     * @param infile  the file to decode
     * @param outfile the file to ouptut to
     * @throws IOException
     */
    public static void decode(String infile, String outfile) throws IOException {
        BitInputStream input = new BitInputStream(infile);
        HuffmanTree huffTree = new HuffmanTree(input);
        BitOutputStream output = new BitOutputStream(outfile);
        huffTree.decode(input, output);
    }

    /**
     * Creates a mapping from 8-bit sequences to number-of-occurrences of
     * those sequences in the given file. To do this, read the file using a
     * BitInputStream, consuming 8 bits at a time.
     * 
     * @param file the file to read
     * @return a freqency map for the given file
     * @throws IOException
     */
    public static Map<Short, Integer> createFrequencyMap(String file) throws IOException {
        Map<Short, Integer> freqMap = new HashMap<>();
        BitInputStream in = new BitInputStream(file);
        while (in.hasBits()) {
            short nextChar = (short) in.readBits(8);
            int numberOfChar = 0;
            if (freqMap != null && freqMap.get(nextChar) != null) {
                numberOfChar = freqMap.get(nextChar);
            }
            freqMap.put(nextChar, numberOfChar + 1);
        }
        return freqMap;
    }

    /**
     * Encodes the given file denoted by infile and writes the output to the
     * .grin file denoted by outfile.
     * 
     * @param infile  the file to encode.
     * @param outfile the file to write the output to.
     * @throws IOException
     */
    public static void encode(String infile, String outfile) throws IOException {
        Map<Short, Integer> freqMap = createFrequencyMap(infile);
        HuffmanTree huffTree = new HuffmanTree(freqMap);
        BitInputStream input = new BitInputStream(infile);
        BitOutputStream output = new BitOutputStream(outfile);
        huffTree.encode(input, output);
    }

    /**
     * The entry point to the program.
     * 
     * @param args the command-line arguments.
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        boolean encode = false;
        if (args[0].toLowerCase().compareTo("encode") == 0) {
            encode = true;
        } else if (args[0].toLowerCase().compareTo("decode") == 0) {
            encode = false;
        } else {
            System.out.println("No decode or encode command provided");
            System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
            System.exit(-1);
        }

        File inputFile = new File(args[1]);
        if (!(inputFile.exists())) {
            System.out.println("Input file does not exist");
            System.out.println("Usage: java Grin <encode|decode> <infile> <outfile>");
            System.exit(-1);
        }

        if (encode) {
            encode(args[1], args[2]);
        } else {
            decode(args[1], args[2]);
        }
    }
}
