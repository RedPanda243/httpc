package tests;

import model.http.HttpRequest;
import prototype.Main;

import java.io.File;
import java.io.FileInputStream;

public class TestConsistenzaDecoded
{
	public boolean result;
//	public int size;

	public void run()
	{
		File original = new File(Main.test1 +"/original");
		File decoded = new File(Main.test1 +"/decoded");
		result = false;
//		size = 0;
		try {
			if (original.listFiles().length != decoded.listFiles().length) return;

			for (File f: original.listFiles()) {
				HttpRequest r = new HttpRequest(new FileInputStream(f));
				HttpRequest r1 = new HttpRequest(new FileInputStream(decoded.getAbsolutePath() + "/" + f.getName()));
				String s = r.toString();
				if (!s.equals(r1.toString())) return;
//				size+=s.length();
			}
			result = true;
		}
		catch (Exception e){e.printStackTrace(); return;}

	}

	public static void main(String args[])
	{
		TestConsistenzaDecoded t = new TestConsistenzaDecoded();
		t.run();
		System.out.println(t.result);
	}
}
