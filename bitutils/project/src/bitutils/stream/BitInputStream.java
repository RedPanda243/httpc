/* 
 * Reference arithmetic coding
 * Copyright (c) Project Nayuki
 * 
 * https://www.nayuki.io/page/reference-arithmetic-coding
 * https://github.com/nayuki/Reference-arithmetic-coding
 */
package bitutils.stream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


/**
 * A bitutils.stream of bits that can be read. Because they come from an underlying byte bitutils.stream,
 * the total number of bits is always a multiple of 8. The bits are read in big endian.
 * Mutable and not thread-safe.
 * @see BitOutputStream
 */
public class BitInputStream implements AutoCloseable {
	
	/*---- Fields ----*/
	
	// The underlying byte bitutils.stream to read from (not null).
	private InputStream input;
	
	// Either in the range [0x00, 0xFF] if bits are available, or -1 if end of bitutils.stream is reached.
	private int currentByte;
	
	// Number of remaining bits in the current byte, always between 0 and 7 (inclusive).
	private int numBitsRemaining;
	
	
	
	/*---- Constructor ----*/
	
	/**
	 * Constructs a bit input bitutils.stream based on the specified byte input bitutils.stream.
	 * @param in the byte input bitutils.stream
	 * @throws NullPointerException if the input bitutils.stream is {@code null}
	 */
	public BitInputStream(InputStream in) {
		input = Objects.requireNonNull(in);
		currentByte = 0;
		numBitsRemaining = 0;
	}
	
	
	
	/*---- Methods ----*/
	
	/**
	 * Reads a bit from this bitutils.stream. Returns 0 or 1 if a bit is available, or -1 if
	 * the end of bitutils.stream is reached. The end of bitutils.stream always occurs on a byte boundary.
	 * @return the next bit of 0 or 1, or -1 for the end of bitutils.stream
	 * @throws IOException if an I/O exception occurred
	 */
	public int read() throws IOException {
		if (currentByte == -1)
			return -1;
		if (numBitsRemaining == 0) {
			currentByte = input.read();
			if (currentByte == -1)
				return -1;
			numBitsRemaining = 8;
		}
		if (numBitsRemaining <= 0)
			throw new AssertionError();
		numBitsRemaining--;
		return (currentByte >>> numBitsRemaining) & 1;
	}
	
	
	/**
	 * Reads a bit from this bitutils.stream. Returns 0 or 1 if a bit is available, or throws an {@code EOFException}
	 * if the end of bitutils.stream is reached. The end of bitutils.stream always occurs on a byte boundary.
	 * @return the next bit of 0 or 1
	 * @throws IOException if an I/O exception occurred
	 * @throws EOFException if the end of bitutils.stream is reached
	 */
	public int readNoEof() throws IOException {
		int result = read();
		if (result != -1)
			return result;
		else
			throw new EOFException();
	}
	
	
	/**
	 * Closes this bitutils.stream and the underlying input bitutils.stream.
	 * @throws IOException if an I/O exception occurred
	 */
	public void close() throws IOException {
		input.close();
		currentByte = -1;
		numBitsRemaining = 0;
	}
/*
	public InputStream getInput() {
		return input;
	}

	public int getCurrentByte() {
		return currentByte;
	}

	public int getNumBitsRemaining() {
		return numBitsRemaining;
	}

 */
}
