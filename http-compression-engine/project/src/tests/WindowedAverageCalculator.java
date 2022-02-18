package tests;

import java.io.*;
import java.text.DecimalFormat;

public class WindowedAverageCalculator
{
	public static void main(String args[]) throws IOException
	{
		File test = new File("C:/test httpc/prova 4/filtered/24");
//		File original = new File(test.getAbsolutePath()+"/original");
//		File encoded = new File(test.getAbsolutePath()+"/encoded");
		DecimalFormat df = new DecimalFormat("#.###");
		int window = 50;
		BufferedWriter bw = new BufferedWriter(new FileWriter(test.getAbsolutePath()+"/avgrates.csv"));
		BufferedReader br = new BufferedReader(new FileReader(test.getAbsolutePath()+"/rates.csv"));
		br.readLine();
		bw.write("Window;AvgRates\n");
		int i = 0, cont = 0;
		double sum=0,d;
		String line;

		while ((line = br.readLine())!=null)
		{
			if(i==window)
			{
				bw.write(cont+";"+df.format(sum/window).replace(",",".")+"\n");
				cont++;
				i=0;
				sum = 0;
			}
			d = Double.parseDouble(line.substring(line.indexOf(";")+1));
			sum += d;
			i++;
		}
		br.close();
		bw.close();
	}
}
