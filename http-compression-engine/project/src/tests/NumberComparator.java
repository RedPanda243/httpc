package tests;

import java.util.Comparator;

public class NumberComparator implements Comparator<String>
{
	public int compare(String o1, String o2) {
		int i1,i2;
		try
		{
			i1 = Integer.parseInt(o1);
		}catch (NumberFormatException e)
		{
			return 1;
		}
		try
		{
			i2 = Integer.parseInt(o2);
		}catch (NumberFormatException e)
		{
			return -1;
		}
		return Integer.compare(i1, i2);
	}
}
