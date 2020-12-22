package model.http;

import model.ListCouples;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

abstract class HttpMessage
{
	public String version = "";
	public ListCouples headers = new ListCouples();
	public char[] body = new char[0];
	int l = 0;

	public HttpMessage()
	{

	}

	public HttpMessage(String string) throws IOException
	{
		this(new ByteArrayInputStream(string.getBytes(StandardCharsets.ISO_8859_1)));
	}

	public HttpMessage(InputStream inputStream) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,StandardCharsets.ISO_8859_1));
		readFirstLine(br.readLine());
		String line;
		int i;
		while(!(line = br.readLine()).equals(""))
		{
			i = line.indexOf(":");
			headers.put(line.substring(0,i),line.substring(i+1));
		}
		body = new char[0];
		//Ricerca case-sensitive dell'header Content-Length
		for (String h:headers.keySet())
		{
			if (h.equals("Content-Length"))
			{
				body = new char[Integer.parseInt(headers.get(h).replace(" ",""))];
				l = br.read(body);
			}
		}

	}

	protected void readFirstLine(String line) throws IOException
	{
/*		String[] splitted = line.split(" ");
		if (splitted.length!=3)
			throw new IOException("Malformed request."+ Arrays.toString(splitted));*/
		set0(line);
		set1(line);
		set2(line);
	}

	public String head()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(get0()).append(" ").append(get1()).append(" ").append(get2()).append("\n");
		for (String h: headers.names())
			sb.append(h).append(":").append(headers.get(h)).append("\n");
		sb.append("\n");
		return sb.toString();
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(head());
		if (body.length>0)
			sb.append(new String(body));

		return sb.toString();
	}

	public void save(File file) throws IOException
	{
		FileWriter fos = new FileWriter(file,StandardCharsets.ISO_8859_1);
		fos.write(head());
		if (body.length>0)
			fos.write(body,0,l);
		fos.close();
	}

	protected abstract String get0();
	protected abstract String get1();
	protected abstract String get2();

	protected abstract void set0(String line);
	protected abstract void set1(String line);
	protected abstract void set2(String line);
}
