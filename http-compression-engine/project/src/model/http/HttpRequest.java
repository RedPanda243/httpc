package model.http;

import model.ListCouples;
import utils.ReaderUtils;

import java.io.*;

public final class HttpRequest extends HttpMessage
{
	public String method;
	public String url;
	public ListCouples parameters;

	public HttpRequest()
	{

	}

	public HttpRequest(String string) throws IOException
	{
		super(string);
	}

	public HttpRequest(InputStream inputStream) throws IOException
	{
		super(inputStream);
	}

	@Override
	protected String get0() {
		return method;
	}

	@Override
	protected String get1() {
		String ret = url;
		for (String n:parameters.names())
			ret+=n+"="+parameters.get(n)+"&";
		return ret.substring(0,ret.length()-1);
	}

	@Override
	protected String get2() {
		return version;
	}


	//line = METHOD SP URI SP VERSION

	@Override
	protected void set0(String line) {
		method = line.substring(0,line.indexOf(" "));
	}

	@Override
	protected void set1(String line) {
		line = line.substring(line.indexOf(" ")+1);
		url = line.substring(0,line.indexOf(" "));
		int index = url.indexOf("?");
		if (index>-1)
		{
			parameters = new ListCouples();
			StringReader reader = new StringReader(url.substring(index+1));
			url = url.substring(0,index+1); //il '?' è l'ultimo carattere dell'url. Così saprò se ci sono parametri in decompressione
			String name,value;
			try
			{
				while(true)
				{
					name = ReaderUtils.readUntil(reader,'=');
					if (name.equals(""))
						break;
					value = ReaderUtils.readUntil(reader,'&');
					name = name.substring(0,name.length()-1);
					if (value.endsWith("&"))
						value = value.substring(0,value.length()-1);
					//Se ci sono nomi ripetuti concateno i valori con ;
					if (parameters.containsKey(name))
						value = parameters.get(name)+";"+value;
					parameters.put(name,value);
				}
			}
			catch (IOException e){}
		}
		else
			parameters = new ListCouples();
	}

	@Override
	protected void set2(String line) {
		line = line.substring(line.indexOf(" ")+1);
		version = line.substring(line.indexOf(" ")+1);
	}

}
