package tests;

import model.http.HttpRequest;
import model.http.HttpResponse;
import prototype.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FiddlerFormatter
{
	//I file prodotti da fiddler usano '\n\r' per andare a capo. Riformatto usando solo \n (solo negli header!)
	public static void main(String args[]) throws IOException
	{
		File original = new File(Main.test1+"/original");
		File backup = new File(Main.test1+"/backup");
		String box;
		int i = 0;
		int l = backup.listFiles().length;
		for (File f: backup.listFiles())
		{
			i++;
			System.out.println(i+"/"+l);
			String name = f.getName();
			while(name.startsWith("0"))
				name = name.substring(1);
			BufferedReader br = new BufferedReader(new FileReader(f, StandardCharsets.ISO_8859_1));
			BufferedWriter bw = new BufferedWriter(new FileWriter(original+"/"+name, StandardCharsets.ISO_8859_1));
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
			/*BufferedInputStream bis =new BufferedInputStream(new FileInputStream(f));
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(temp,true));
			byte[] read = new byte[(int)f.length()-index];
			bis.skip(index);
			int r = bis.read(read);
			bos.write(read,0,r);
			bis.close();
			bos.close();
			f.delete();
			System.out.println(temp.renameTo(f));*/
		}
	}
}
