package tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import static tests.FilteredRateCalculator.getFolderSize;

public class FilteredTreeDimensionCalculator
{
	public static void main(String args[]) throws IOException
	{
		File test = new File("C:/test httpc/prova 4/filtered");
		long dim = 0;
		for (File host:test.listFiles())
		{
			if (host.isDirectory()) {
				dim += getFolderSize(new File(host.getAbsolutePath()+"/trees"));
			}
		}
		System.out.println(dim);
	}
}
