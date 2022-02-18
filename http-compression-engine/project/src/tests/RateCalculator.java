package tests;

import prototype.Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class RateCalculator
{
	public static void main(String args[]) throws IOException
	{
		File test = new File("C:/test httpc/prova 4");
		File original = new File(test.getAbsolutePath()+"/original");
		File encoded = new File(test.getAbsolutePath()+"/encoded");
		BufferedWriter bw = new BufferedWriter(new FileWriter(test.getAbsolutePath()+"/rates.csv"));
		bw.write("Message;Rate\n");
		int i = 0, cont = 0;
		int l = encoded.listFiles().length/2;
		double rate;
		DecimalFormat df = new DecimalFormat("#.###");
		File e,o;
		while (i<l)
		{
			e = new File(encoded.getAbsolutePath()+"/"+(i+1)+"_c.txt");
			o = new File(original.getAbsolutePath()+"/"+(i+1)+"_c.txt");
			bw.write(cont+";"+(df.format(1-(double)e.length()/(8*o.length())))+"\n");
			cont++;
			e = new File(encoded.getAbsolutePath()+"/"+(i+1)+"_s.txt");
			o = new File(original.getAbsolutePath()+"/"+(i+1)+"_s.txt");
			bw.write(cont+";"+(df.format(1-(double)e.length()/(8*o.length())))+"\n");
			cont++;
			i++;
		}
		bw.close();
	}
}
