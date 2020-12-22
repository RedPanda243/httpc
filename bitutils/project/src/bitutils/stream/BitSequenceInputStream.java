package bitutils.stream;

import bitutils.BitSequence;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class BitSequenceInputStream extends BitInputStream
{
	private int limit;
	private int cont;
	public BitSequenceInputStream(BitSequence sequence) throws IOException
	{
		super(new ByteArrayInputStream(sequence.toBytes()));
		limit = sequence.length();
		cont = 0;
	}

	public int read() throws IOException
	{
		cont++;
		return ((cont-1)==limit?-1:super.read());
	}

	public static void main(String args[]) throws IOException
	{
		BitSequence sequence = new BitSequence("1011001100");
		BitSequenceInputStream bsis = new BitSequenceInputStream(sequence);
		BitSequence sequence1 = new BitSequence();
		int b,i=0;
		while((b=bsis.read())!=-1) {
			System.out.print(b);
			sequence1.set(i, b);
			i++;
		}
		System.out.println();
		System.out.println(sequence.equals(sequence1));

	}
}
