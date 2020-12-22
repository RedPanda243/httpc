package bitutils;

import bitutils.stream.BitInputStream;
import bitutils.stream.BitOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;

public class BitSequence implements Iterable<Integer>
{
	public static final BitSequence zero = new BitSequence(0,1);
	public static final BitSequence one  = new BitSequence(1,1);

	private int length;
	private BitSet bitset;

	public BitSequence()
	{
		bitset = new BitSet();
	}

	public BitSequence(int length)
	{
		bitset = new BitSet(length);
		this.length = length;
	}

	public BitSequence(long number, int length)
	{
		this(length);
		if (number<0)
			throw new IllegalArgumentException("number < 0");
		for(int i=length-1; i>-1; i--)
		{
			set(i,(int)(number % 2));
			number = number / 2;
		}
	}

	public BitSequence(String string)
	{
		this(string.length());
		int c;
		for (int i=0; i<string.length(); i++)
		{
			c = Integer.parseInt(""+string.charAt(i));
			set(i,c);
		}
	}

	public BitSequence(byte[] bytes, int length) throws IOException
	{
		this();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		BitInputStream bis = new BitInputStream(bais);
		for (int i=0; i<length; i++)
			this.set(i,bis.read());
		this.length = length;
	}

	public BitSequence(byte[] bytes) throws IOException
	{
		this(bytes,bytes.length*8);
	}

	public BitSequence clone()
	{
		BitSequence bs = new BitSequence();
		bs.length = length;
		bs.bitset = (BitSet)bitset.clone();
		return bs;
	}

	public BitSequence concat(BitSequence suffix)
	{
		if (suffix == null)
			return this;
		BitSequence bs = new BitSequence(length()+suffix.length());
		for (int i=0; i<length(); i++)
			bs.set(i,this.get(i));

		for (int i=0; i<suffix.length(); i++)
			bs.set(length()+i,suffix.get(i));
		return bs;
	}

	public int count()
	{
		int c=0;
		for (int i=0; i<this.length();i++)
			c+=get(i);
		return c;
	}

	public boolean equals(Object object)
	{
		if (object instanceof BitSequence)
		{
			BitSequence s = (BitSequence)object;
			return length == s.length && bitset.equals(s.bitset);
		}
		return false;
	}

	public int get(int bitIndex)
	{
		return bitset.get(bitIndex)?1:0;
	}

	public BitSequence get(int fromIndex, int toIndex)
	{
		BitSequence bs = new BitSequence();
		bs.length = toIndex-fromIndex;
		bs.bitset = bitset.get(fromIndex,toIndex);
		return bs;
	}

	public int hashCode()
	{
		return bitset.hashCode();
	}

	public boolean isPrefixOf(BitSequence sequence)
	{
		if (sequence.length()<this.length())
			return false;

		for (int i=0; i<length(); i++)
		{
			if (get(i)!=sequence.get(i))
				return false;
		}
		return true;
	}

	public int length()
	{
		return length;
	}

	public void set(int bitIndex)
	{
		set(bitIndex,1);
	}

	public void set(int bitIndex, int value)
	{
		if (bitIndex>=length)
			length = bitIndex+1;
		bitset.set(bitIndex, value==1);
	}
/*
	public BitSequence subSequence(int length)
	{
		return subSequence(0,length);
	}

	public BitSequence subSequence(int start, int length)
	{
		BitSequence bs = new BitSequence();
		for (int i=start; i<start+length; i++)
			bs.set(i-start,get(i));
		return bs;
	}
*/
	public long value()
	{
		long v = 0;
		for (int i=0; i<length; i++)
		{
			if (get(i)==1)
				v+=(long) Math.pow(2,length-i-1);
		}
		return v;
	}

	public byte[] toBytes() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BitOutputStream bos = new BitOutputStream(baos);
		for (int b:this)
			bos.write(b);
		bos.close();
		return baos.toByteArray();
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for (int i=0; i<length(); i++)
			sb.append(get(i));
		return sb.toString();
	}

	public static void main(String args[])
	{
		BitSequence bs = new BitSequence("010");
		System.out.println(bs);
		System.out.println(bs.length());
		System.out.println(bs.bitset.size());
		bs.set(5);
		System.out.println("\n"+bs);
		System.out.println(bs.length());
	}

	@Override
	public Iterator<Integer> iterator()
	{
		BitSequence sequence = this;
		return new Iterator<Integer>()
		{
			int index = 0;

			@Override
			public boolean hasNext() {
				return index<sequence.length();
			}

			@Override
			public Integer next()
			{
				index++;
				return sequence.get(index-1);
			}
		};
	}
}
