package huffman.bytecodec.encoder;

import bitutils.stream.BitOutputStream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Write individual bits to an output stream, or write whole bytes.
 *
 * Bits are stored in a buffer until the buffer contains 8 bits, then
 * it is written to the output stream.
 *
 * Class was modified to suit the style of BitInputStream.
 *
 * @author 1300002022
 *
 */
public class BitByteOutputStream extends BitOutputStream {

	// Stream to write bits to.

	public BitByteOutputStream(OutputStream out) {
		super(out);
	}

	/**
	 * Write the value x to output stream.
	 *
	 * If buffer is empty, then just write x straight
	 * to the output. Otherwise, write the contents of
	 * buffer to output as well as the first n bits of x
	 * then fill buffer with remaining bits of x.
	 *
	 * @param x - byte to write to output.
	 */
	public void writeByte(int x) throws IOException{
		if( x < 0 || x > 255) {
			throw new IllegalArgumentException("Value not in range. ("+x+")");
		}

		if (getNumBitsFilled() == 0) {
			// If buffer empty just write x to out.
			try {
				getOutput().write(x);
			}
			catch (IOException e) {
				System.err.println("Write Error");
				e.printStackTrace();
			}
			return;
		}

		for (int i = 0; i < 8; i++) {
			int bit = ((x >>> (8 - i - 1)) & 1);
			write(bit);
		}
	}
}
