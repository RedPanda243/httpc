package tests;

import model.http.HttpRequest;
import model.http.HttpResponse;
import prototype.Main;

import java.io.*;

public class TestModel
{
	public static void main(String args[]) throws Exception
	{
		File original = new File(Main.test1.getAbsolutePath() +"/original");
		File decoded = new File(Main.test1.getAbsolutePath() +"/decoded");
		for (File f: original.listFiles())
		{
			FileInputStream fis = new FileInputStream(f);
			if (f.getName().endsWith("c.txt"))
			{
				HttpRequest request = new HttpRequest(fis);
		//		System.out.println(request);
				request.save(new File(decoded.getAbsolutePath()+"/"+f.getName()));
			}
			else if (f.getName().endsWith("s.txt")) {
				HttpResponse response = new HttpResponse(fis);
		//		System.out.println(response);
				response.save(new File(decoded.getAbsolutePath() + "/" + f.getName()));
			}
			fis.close();
		}

		TestConsistenzaDecoded.main(null);
	}
}
