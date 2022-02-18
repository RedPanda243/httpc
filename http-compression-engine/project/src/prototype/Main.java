package prototype;

import bitutils.BitSequence;
import bitutils.stream.BitInputStream;
import bitutils.stream.BitSequenceInputStream;
import model.http.HttpRequest;
import model.http.HttpResponse;
import tests.TestConsistenzaDecoded;
import tests.TestConsistenzaOriginal;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class Main
{
//	public static final File workspace = new File(System.getProperty("user.dir")).getParentFile().getParentFile();
//	public static final File test1 = new File(workspace.getAbsolutePath()+"/test httpc/prova 2");

	public static final File test1 = new File("C:/test httpc/prova 4");
/*
	public static void main1(String args[]) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(test1.getAbsolutePath()+"/times.csv"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(test1.getAbsolutePath()+"/times1.csv"));
		String line;
		int cont = 0;
		while((line=br.readLine())!=null) {
			bw.write(cont + ";" + line + "\n");
			cont++;
		}
		br.close();
		bw.close();
	}
*/
	public static void main(String args[]) throws Exception
	{
		File original = new File(Main.test1.getAbsolutePath() +"/original");
		File decoded = new File(Main.test1.getAbsolutePath() +"/decoded");
		File encoded = new File(Main.test1.getAbsolutePath() +"/encoded");
		//Controllo consistenza original
/*		TestConsistenzaOriginal testoriginal = new TestConsistenzaOriginal();
		testoriginal.run();

		if (!testoriginal.result) throw new Exception("test original failed");
*/
		File rootclient = new File(Main.test1.getAbsolutePath()+"/trees/client");
		File rootserver = new File(Main.test1.getAbsolutePath()+"/trees/server");
		Session client = new Session(rootclient);
		Session server = new Session(rootserver);

		HttpRequest request;
		HttpResponse response;
		FileInputStream fis;
		FileOutputStream fos;
		File f;
		BitSequence coded;
		BitInputStream stream;
		String suffix;

		File times = new File(original.getParentFile().getAbsolutePath()+"/times.csv");
		BufferedWriter timeWriter = new BufferedWriter(new FileWriter(times));
		long init_time, finish_time;
		List<File> files = Arrays.asList(original.listFiles());
		int l = files.size()/2;
		int cont = 0;
		for (int i=0; i<l; i++)
		{
			suffix = "/"+(i+1)+"_c.txt";
			System.out.println(suffix);
			f = new File(original.getAbsolutePath()+suffix);
			fis = new FileInputStream(f);
			request = new HttpRequest(fis);
			fis.close();
			init_time = System.currentTimeMillis();
			coded = client.codeRequest(request);
			finish_time = System.currentTimeMillis();
			timeWriter.write(cont+";"+(finish_time-init_time)+"\n");
			cont++;
			fos = new FileOutputStream(encoded.getAbsolutePath()+suffix);
			fos.write(coded.toString().getBytes(StandardCharsets.ISO_8859_1));	//Salvo i bit codificati usando un byte 0 o 1 per ogni bit. Costoso ma leggibile
			fos.close();
	//		System.out.println("\tCoded");
	//		stream = new BitSequenceInputStream(coded);
	//		server.decodeRequest(stream).save(new File(decoded.getAbsolutePath()+suffix));
	//		System.out.println("\tDecoded");

			suffix = "/"+(i+1)+"_s.txt";
			System.out.println(suffix);
			f = new File(original.getAbsolutePath()+suffix);
			fis = new FileInputStream(f);
			response = new HttpResponse(fis);
			fis.close();
			init_time = System.currentTimeMillis();
			coded = server.codeResponse(response);
			finish_time = System.currentTimeMillis();
			timeWriter.write(cont+";"+(finish_time-init_time)+"\n");
			cont++;
			fos = new FileOutputStream(encoded.getAbsolutePath()+suffix);
			fos.write(coded.toString().getBytes(StandardCharsets.ISO_8859_1));	//Salvo i bit codificati usando un byte 0 o 1 per ogni bit. Costoso ma leggibile
			fos.close();
	//		System.out.println("\tCoded");
	//		stream = new BitSequenceInputStream(coded);
	//		client.decodeResponse(stream).save(new File(decoded.getAbsolutePath()+suffix));

	//		System.out.println("\tDecoded");

		}
		timeWriter.close();
		/*TestConsistenzaDecoded testdecoded = new TestConsistenzaDecoded();
		testdecoded.run();
		System.out.println("\n"+testdecoded.result);*/

	}

	public static void code()
	{

	}
}
