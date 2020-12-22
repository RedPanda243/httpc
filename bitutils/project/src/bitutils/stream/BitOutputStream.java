/* 
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */
package bitutils.stream;
import bitutils.BitSequence;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;


/**
 * A bitutils.stream where bits can be written to. Because they are written to an underlying
 * byte bitutils.stream, the end of the bitutils.stream is padded with 0's up to a multiple of 8 bits.
 * The bits are written in big endian. Mutable and not thread-safe.
 * @see BitInputStream
 */
public class BitOutputStream implements AutoCloseable {
	
	/*---- Fields ----*/
	
	// The underlying byte bitutils.stream to write to (not null).
	private OutputStream output;
	
	// The accumulated bits for the current byte, always in the range [0x00, 0xFF].
	private int currentByte;
	
	// Number of accumulated bits in the current byte, always between 0 and 7 (inclusive).
	private int numBitsFilled;
	
	
	
	/*---- Constructor ----*/
	
	/**
	 * Constructs a bit output bitutils.stream based on the specified byte output bitutils.stream.
	 * @param out the byte output bitutils.stream
	 * @throws NullPointerException if the output bitutils.stream is {@code null}
	 */
	public BitOutputStream(OutputStream out) {
		output = Objects.requireNonNull(out);
		currentByte = 0;
		numBitsFilled = 0;
	}
	
	
	
	/*---- Methods ----*/
	
	/**
	 * Writes a bit to the bitutils.stream. The specified bit must be 0 or 1.
	 * @param b the bit to write, which must be 0 or 1
	 * @throws IOException if an I/O exception occurred
	 */
	public void write(int b) throws IOException {
		if (b != 0 && b != 1)
			throw new IllegalArgumentException("Argument must be 0 or 1");
		currentByte = (currentByte << 1) | b;
		numBitsFilled++;
		if (numBitsFilled == 8)
			flush();
	}
	
	public void write(BitSequence sequence) throws IOException
	{
		for(Integer bit:sequence)
			write(bit);
	}

	/**
	 * Closes this bitutils.stream and the underlying output bitutils.stream. If called when this
	 * bit bitutils.stream is not at a byte boundary, then the minimum number of "0" bits
	 * (between 0 and 7 of them) are written as padding to reach the next byte boundary.
	 * @throws IOException if an I/O exception occurred
	 */
	public void close() throws IOException {
		while (numBitsFilled != 0)
			write(0);
		output.close();
	}

	// Fill empty space in buffer with 0s.
	public void fill()
	{
		currentByte <<= (8 - numBitsFilled);
	}

	public void flush() throws IOException
	{
		if (numBitsFilled == 0) {
			return;
		}
		fill();
		output.write(currentByte);
		// Reset buffer.
		numBitsFilled = 0;
		currentByte = 0;
	}

	public OutputStream getOutput() {
		return output;
	}

	public int getCurrentByte() {
		return currentByte;
	}

	public int getNumBitsFilled() {
		return numBitsFilled;
	}
}
