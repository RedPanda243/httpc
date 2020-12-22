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
	public static final File workspace = new File(System.getProperty("user.dir")).getParentFile().getParentFile();
	public static final File test1 = new File(workspace.getAbsolutePath()+"/test httpc/prova 1");

	public static void main(String args[]) throws Exception
	{
		File original = new File(Main.test1.getAbsolutePath() +"/original");
		File decoded = new File(Main.test1.getAbsolutePath() +"/decoded");
		File encoded = new File(Main.test1.getAbsolutePath() +"/encoded");

		//Controllo consistenza original
		TestConsistenzaOriginal testoriginal = new TestConsistenzaOriginal();
		testoriginal.run();

		if (!testoriginal.result) throw new Exception("test original failed");

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

		List<File> files = Arrays.asList(original.listFiles());
		for (int i=0; i<files.size()/2;/*i<1;*/ i++)
		{
			suffix = "/"+(i+1)+"_c.txt";
			f = new File(original.getAbsolutePath()+suffix);
			fis = new FileInputStream(f);
			request = new HttpRequest(fis);
			fis.close();
			coded = client.codeRequest(request);
			fos = new FileOutputStream(encoded.getAbsolutePath()+suffix);
			fos.write(coded.toString().getBytes(StandardCharsets.ISO_8859_1));	//Salvo i bit codificati usando un byte 0 o 1 per ogni bit. Costoso ma leggibile
			fos.close();
			stream = new BitSequenceInputStream(coded);
			server.decodeRequest(stream).save(new File(decoded.getAbsolutePath()+suffix));

			suffix = "/"+(i+1)+"_s.txt";
			f = new File(original.getAbsolutePath()+suffix);
			fis = new FileInputStream(f);
			response = new HttpResponse(fis);
			fis.close();
			coded = server.codeResponse(response);
			fos = new FileOutputStream(encoded.getAbsolutePath()+suffix);
			fos.write(coded.toString().getBytes(StandardCharsets.ISO_8859_1));	//Salvo i bit codificati usando un byte 0 o 1 per ogni bit. Costoso ma leggibile
			fos.close();
			stream = new BitSequenceInputStream(coded);
			client.decodeResponse(stream).save(new File(decoded.getAbsolutePath()+suffix));

		}

		/*TestConsistenzaDecoded testdecoded = new TestConsistenzaDecoded();
		testdecoded.run();
		System.out.println("\n"+testdecoded.result);*/

	}

	public static void code()
	{

	}
}
