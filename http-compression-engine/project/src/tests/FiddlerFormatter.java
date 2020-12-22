package tests;

import model.http.HttpRequest;
import model.http.HttpResponse;
import prototype.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
/*
public class FiddlerFormatter
{
	//I file prodotti da fiddler usano '\n\r' per andare a capo. Riformatto usando solo \n (solo negli header!)
	public static void main(String args[]) throws IOException
	{
		File original = new File(Main.test1+"/original");
		String box;
		File temp = new File(original+"/.temp");
		for (File f: original.listFiles())
		{
			
			BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.UTF_8));
			BufferedWriter bw = new BufferedWriter(new FileWriter(temp, StandardCharsets.UTF_8));
			int index = 0;
			while((box=br.readLine())!=null && !box.equals(""))
			{
				index+=box.length()+2;
				bw.write(box+"\n");
			}
			index++;
			bw.write("\n");
			br.close();
			bw.close();
			BufferedInputStream bis =new BufferedInputStream(new FileInputStream(f));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp,true));
			byte[] read = new byte[(int)f.length()-index];
			bis.skip(index);
			int r = bis.read(read);
			bos.write(read,0,r);
			bis.close();
			bos.close();
			temp.renameTo(f);
		}
	}
}
*/