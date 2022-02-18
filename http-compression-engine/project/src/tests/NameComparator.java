package tests;

import java.util.Comparator;

public class NameComparator implements Comparator<String>
{
	public int compare(String o1, String o2) {
		String[] s1 = o1.split("_");
		String[] s2 = o2.split("_");
		int c = Integer.compare(Integer.parseInt(s1[0]),Integer.parseInt(s2[0]));
		if (c!=0)
			return c;
		else
			return s1[1].compareTo(s2[1]);
	}
}
