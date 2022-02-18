package tests;

import model.http.HttpRequest;
import model.http.HttpResponse;
import prototype.Main;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestConsistenzaOriginal
{
	public boolean result;

	public void run()
	{
		File original = new File(Main.test1 +"/original");
		result = false;

		try {
			HttpRequest request;
			HttpResponse response;
			FileInputStream fis;
			File f;
			List<File> files = Arrays.asList(original.listFiles());
			if (files.size()%2==1) return;
			for (int i=0; i<files.size()/2; i++)
			{
				f = new File(original.getAbsolutePath()+"/"+(i+1)+"_c.txt");
				if (!files.contains(f))
				{
				//	System.out.println(f);
					return;
				}
				fis = new FileInputStream(f);
				request = new HttpRequest(fis);
				fis.close();

				f = new File(original.getAbsolutePath()+"/"+(i+1)+"_s.txt");
				if (!files.contains(f)) {
			//		System.out.println(f);
					return;
				}
				fis = new FileInputStream(f);
				response = new HttpResponse(fis);
				fis.close();
			}
		}
		catch (Exception e){e.printStackTrace(); return;}
		result = true;
	}

	public static void main(String args[])
	{
		TestConsistenzaOriginal t = new TestConsistenzaOriginal();
		t.run();
		System.out.println(t.result);
	}
}
