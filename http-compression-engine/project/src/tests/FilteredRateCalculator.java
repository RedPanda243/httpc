package tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilteredRateCalculator
{
	public static double getFolderSize(File folder) {
		double length = 0;
		File[] files = folder.listFiles();

		int count = files.length;

		for (int i = 0; i < count; i++) {
			if (files[i].isFile()) {
				length += files[i].length();
			}
			else {
				length += getFolderSize(files[i]);
			}
		}
		return length;
	}


	public static void main(String args[]) throws IOException
	{
		File test = new File("C:/test httpc/prova 3/filtered");
		BufferedWriter bwavg = new BufferedWriter(new FileWriter(test.getAbsolutePath()+"/avgrates.csv"));
		int conthost = 0;
		DecimalFormat df = new DecimalFormat("#.###");
		bwavg.write("Host;AvgRate\n");

		List<String> list = Arrays.asList(test.list());
		list.sort(new NumberComparator());

		for (String h:list)
		{
			File host = new File(test.getAbsolutePath()+"/"+h);
			if (host.isDirectory()) {
				File output = new File(host.getAbsolutePath() + "/rates.csv");
				BufferedWriter bw = new BufferedWriter(new FileWriter(output));
				bw.write("Message;Rate\n");
				File original = new File(host.getAbsolutePath() + "/original");
				File encoded = new File(host.getAbsolutePath() + "/encoded");
				bwavg.write(conthost + ";" + df.format(1 -  getFolderSize(encoded) / (8 * getFolderSize(original))).replace(",",".") + "\n");
		/*		if (h.equals("24"))
					System.out.println(1 -  getFolderSize(encoded) / (8 * getFolderSize(original)));*/
				conthost++;
				List<String> names = Arrays.asList(original.list());
				names.sort(new NameComparator());
				int cont = 0;

				File e, o;
				for (String name : names) {
					e = new File(encoded.getAbsolutePath() + "/" + name);
					o = new File(original.getAbsolutePath() + "/" + name);
					bw.write(cont + ";" + df.format(1 - (double) e.length() / (8 * o.length())).replace(",",".") + "\n");
					cont++;
				}
				bw.close();
			}
		}
		bwavg.close();
	}
}
