package tests;

import model.http.HttpRequest;
import model.http.HttpResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class HostFilter
{
	public static void main(String args[]) throws IOException
	{
		File test = new File("C:/test httpc/prova 4");
		File original = new File(test.getAbsolutePath()+"/original");
		File filtered = new File(test.getAbsolutePath()+"/filtered");
		DecimalFormat df = new DecimalFormat("#.###");

		int i = 0;
		int l = original.listFiles().length/2;
		String name,host;
		ArrayList<String> hosts = new ArrayList<>();
		File filter;
		while (i<l)
		{
			name = "/"+(i+1)+"_c.txt";
			HttpRequest req = new HttpRequest(new FileInputStream(original.getAbsolutePath()+name));
			host = req.headers.get("Host").replace(":","_").replace(".","-");
			if (!hosts.contains(host))
				hosts.add(host);
			filter = new File(filtered.getAbsolutePath()+"/"+hosts.indexOf(host)+"/original");
			if (!filter.exists())
				filter.mkdirs();
			req.save(new File(filter.getAbsolutePath()+name));
			name = name.replace("c","s");
			HttpResponse res = new HttpResponse(new FileInputStream(original.getAbsolutePath()+name));
			res.save(new File(filter.getAbsolutePath()+name));
			i++;
		}
	}
}
