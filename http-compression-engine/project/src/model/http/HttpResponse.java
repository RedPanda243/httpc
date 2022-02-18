package model.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class HttpResponse extends HttpMessage
{
	public String status;
	public String phrase;

	public HttpResponse()
	{

	}

	public HttpResponse(String string) throws IOException
	{
		super(string);
	}

	public HttpResponse(InputStream inputStream) throws IOException
	{
		super(inputStream);
	}

	@Override
	protected String get0() {
		return version;
	}

	@Override
	protected String get1() {
		return status;
	}

	@Override
	protected String get2() {
		return phrase;
	}


	@Override
	protected void set0(String line) {
		version = line.substring(0,line.indexOf(" "));
	}

	@Override
	protected void set1(String line) {
		line = line.substring(line.indexOf(" ")+1);
		status = line.substring(0,line.indexOf(" "));
	}

	@Override
	protected void set2(String line) {
		line = line.substring(line.indexOf(" ")+1);
		phrase = line.substring(line.indexOf(" ")+1);
	//	phrase = line;
	}
}
