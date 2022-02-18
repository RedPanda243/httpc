package prototype;

import bitutils.BitSequence;
import bitutils.stream.BitInputStream;
import model.http.HttpRequest;
import model.http.HttpResponse;
import tests.NameComparator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MainFiltered
{
	static boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}


	public static void main(String args[]) throws IOException
	{
		File test = new File("C:/test httpc/prova 4/filtered");
		int x = 1;
		File[] all = test.listFiles();
		for (File host:all)
		{
			System.out.println(x+"/"+all.length);
			x++;
			File original = new File(host.getAbsolutePath() +"/original");
			File encoded = new File(host.getAbsolutePath() +"/encoded");
			if (!encoded.exists())
				encoded.mkdirs();

			File trees = new File(host.getAbsolutePath()+"/trees");
			deleteDirectory(trees);
			trees.mkdirs();

			File rootclient = new File(trees+"/client");
			File rootserver = new File(trees+"/server");
			Session client = new Session(rootclient);
			Session server = new Session(rootserver);

			HttpRequest request;
			HttpResponse response;
			FileInputStream fis;
			FileOutputStream fos;
			File f;
			BitSequence coded;
//			BitInputStream stream;
			String suffix;

			File times = new File(original.getParentFile().getAbsolutePath()+"/times.csv");
			BufferedWriter timeWriter = new BufferedWriter(new FileWriter(times));
			timeWriter.write("Message;Time\n");
			long init_time, finish_time;
			List<String> files = Arrays.asList(original.list());
			files.sort(new NameComparator());
//			int l = files.size()/2;
			int cont = 0;
			for (String file:files)
			{
				if (file.contains("c")) {
					suffix = "/" + file;
					f = new File(original.getAbsolutePath() + suffix);
					fis = new FileInputStream(f);
					request = new HttpRequest(fis);
					fis.close();
					init_time = System.currentTimeMillis();
					coded = client.codeRequest(request);
					finish_time = System.currentTimeMillis();
					timeWriter.write(cont + ";" + (finish_time - init_time) + "\n");
					cont++;
					fos = new FileOutputStream(encoded.getAbsolutePath() + suffix);
					fos.write(coded.toString().getBytes(StandardCharsets.ISO_8859_1));    //Salvo i bit codificati usando un byte 0 o 1 per ogni bit. Costoso ma leggibile
					fos.close();
					//		System.out.println("\tCoded");
					//		stream = new BitSequenceInputStream(coded);
					//		server.decodeRequest(stream).save(new File(decoded.getAbsolutePath()+suffix));
					//		System.out.println("\tDecoded");

					suffix = suffix.replace("c","s");

					f = new File(original.getAbsolutePath() + suffix);
					fis = new FileInputStream(f);
					response = new HttpResponse(fis);
					fis.close();
					init_time = System.currentTimeMillis();
					coded = server.codeResponse(response);
					finish_time = System.currentTimeMillis();
					timeWriter.write(cont + ";" + (finish_time - init_time) + "\n");
					cont++;
					fos = new FileOutputStream(encoded.getAbsolutePath() + suffix);
					fos.write(coded.toString().getBytes(StandardCharsets.ISO_8859_1));    //Salvo i bit codificati usando un byte 0 o 1 per ogni bit. Costoso ma leggibile
					fos.close();
				}
				//		System.out.println("\tCoded");
				//		stream = new BitSequenceInputStream(coded);
				//		client.decodeResponse(stream).save(new File(decoded.getAbsolutePath()+suffix));

				//		System.out.println("\tDecoded");

			}
			timeWriter.close();
		}
	}
}
