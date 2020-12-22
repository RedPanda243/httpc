package utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;

public class ReaderUtils
{
	public static String readUntil(Reader reader, char... cs) throws IOException
	{
		StringBuilder s = new StringBuilder();
		char box;
		boolean flag = true;
		while(flag)
		{
			box = (char) reader.read();
			if (box == (char)-1)
				break;
			s.append(box);
			for (char c:cs)
			{
				if (box == c)
					flag = false;
			}
		}
		return s.toString();
	}
}
