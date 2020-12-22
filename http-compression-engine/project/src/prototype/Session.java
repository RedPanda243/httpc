package prototype;

import bitutils.BitSequence;
import bitutils.stream.BitInputStream;
import huffman.tree.Tree;
import model.ListCouples;
import model.http.HttpRequest;
import model.http.HttpResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Session
{
	private Tree queryTree;
	private Tree responseTree;
	private String rootdir;

	public Session(File file) throws IOException
	{
		rootdir = file.getAbsolutePath();
		try
		{
			if (!file.exists() && !file.mkdirs())
				throw new IOException("Impossibile creare dizionario");

			if (Objects.requireNonNull(file.listFiles()).length == 0)
				initSession();
		}
		catch (NullPointerException npe)
		{
			throw new IOException(npe);
		}

		try {queryTree = loadTree("/q");}
		catch(FileNotFoundException e) { reset("/q","HTTP/1.1"); }

		try {responseTree = loadTree("/r");}
		catch(FileNotFoundException e) { reset("/r","HTTP/1.1"); }

	}

	private void initSession() throws IOException
	{
		if (!(new File(rootdir+"/q").mkdir()) || !(new File(rootdir+"/r").mkdir()))
			throw new IOException("Impossibile creare sottoalberi PDU");
	}

	private List<String> loadValues(String path) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(rootdir+path+"/.values"));
		ArrayList<String> values = new ArrayList<>();
		String v;
		while((v=br.readLine())!=null)
			values.add(v);
		br.close();
		return values;
	}

	private Tree loadTree(String path) throws IOException
	{
		return Tree.load(new File(rootdir + path + "/.tree"));
	}

	private void saveTree(String path, Tree tree) throws IOException
	{
		tree.save(new File(rootdir+path+"/.tree"));
	}

	private void saveValue(String path, String value) throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(rootdir+path+"/.values",true));
		bw.write(value+"\n");
		bw.close();
	}

	public BitSequence codeRequest(HttpRequest request) throws IOException
	{
		System.out.println("/----------BEGIN CODING REQUEST----------/");
		System.out.println(request);
		Object[] box = {new BitSequence(),"/q"};
		box = codeType1(request.version, (String)box[1],(BitSequence)box[0]);
//		System.out.println("\nVersion: "+request.version+"\nCode: "+box[0]+" ("+((BitSequence)box[0]).length()+")");
		box = codeType1(request.method, (String)box[1],(BitSequence)box[0]);
//		System.out.println("\nMethod: "+request.method+"\nCode: "+box[0]+" ("+((BitSequence)box[0]).length()+")");
		box = codeType3(request.url, (String)box[1],(BitSequence)box[0]);
//		System.out.println("\nURL: "+request.url +"\nCode: "+box[0]+" ("+((BitSequence)box[0]).length()+")");
		box = codeType2(request.parameters, (String)box[1],(BitSequence)box[0]);
//		System.out.println("\nParameters size: "+request.parameters.size()+"\nCode: "+box[0]+" ("+((BitSequence)box[0]).length()+")");
		box = codeType2(request.headers, (String)box[1],(BitSequence)box[0]);
//		System.out.println("\nHeaders size: "+request.headers.size()+"\nCode: "+box[0]+" ("+((BitSequence)box[0]).length()+")");
		System.out.println("/----------END CODING REQUEST----------/");
		return (BitSequence)box[0];
	}

	public HttpRequest decodeRequest(BitInputStream encodedstream) throws IOException
	{
		System.out.println("/----------BEGIN DECODING REQUEST----------/");
		HttpRequest request = new HttpRequest();
		String path = "/q";
		String[] dec = decodeType1(encodedstream,path);
		request.version = dec[0];
		path = dec[1];
//		System.out.println("Version decoded: "+request.version);

		dec = decodeType1(encodedstream,path);
		request.method = dec[0];
		path = dec[1];
//		System.out.println("Method decoded: "+request.method);

		dec = decodeType3(encodedstream,path);
		request.url = dec[0];
		path = dec[1];
//		System.out.println("URL decoded");

		Object[] dec2 = decodeType2(encodedstream,path);
		request.parameters = (ListCouples)dec2[0];
		path = (String)dec2[1];
//		System.out.println("Parameters decoded");

		dec2 = decodeType2(encodedstream,path);
		request.headers = (ListCouples)dec2[0];
//		System.out.println("Headers decoded");
		System.out.println("\n"+request);

		System.out.println("/----------END CODING REQUEST----------/");
		return request;
	}

	public BitSequence codeResponse(HttpResponse response) throws IOException
	{
		System.out.println("/----------BEGIN CODING RESPONSE----------/");
		System.out.println(response);
		Object[] box = {new BitSequence(),"/r"};
		box = codeType1(response.version, (String)box[1],(BitSequence)box[0]);
		System.out.println("\nVersion: "+response.version+"\nCode: "+box[0]);
		box = codeType1(response.status+" "+response.phrase, (String)box[1],(BitSequence)box[0]);
		System.out.println("\nStatus: "+response.status+"\nCode: "+box[0]);
		box = codeType2(response.headers, (String)box[1],(BitSequence)box[0]);
		System.out.println("\nHeaders size: "+response.headers.size()+"\nCode: "+box[0]);
		System.out.println("/----------END CODING RESPONSE----------/");
		return (BitSequence)box[0];
	}

	public HttpResponse decodeResponse(BitInputStream encodedstream) throws IOException
	{
		HttpResponse response = new HttpResponse();
		String path = "/r";
		String[] dec = decodeType1(encodedstream,path);
		response.version = dec[0];
		path = dec[1];

		dec = decodeType1(encodedstream,path);
		response.status = dec[0].substring(0,dec[0].indexOf(" "));
		response.phrase = dec[0].substring(dec[0].indexOf(" ")+1);
		path = dec[1];

		Object[] dec2 = decodeType2(encodedstream,path);
		response.headers = (ListCouples)dec2[0];
//		path = (String)dec2[1];
		return response;
	}

	private void reset(String path, String defaultvalue) throws IOException
	{
		File dict = new File(rootdir+path);
		if (!dict.exists())
			dict.mkdir();
		else {
			for (File f : dict.listFiles())
				f.delete();
		}
		Tree tree = new Tree();
		tree.insertInto(0);
		saveValue(path,defaultvalue);
		saveTree(path,tree);
	}

	private void reset(String path) throws IOException
	{
		File dict = new File(rootdir+path);
		if (!dict.exists())
			dict.mkdir();
		else {
			for (File f : dict.listFiles())
				f.delete();
		}
		saveTree(path,new Tree());
		File values = new File(dict.getAbsolutePath()+"/.values");
		if (!values.createNewFile()) throw new IOException("Impossibile creare file vuoto .values");
	}

	/**
	 *
	 * Codifica la stringa passata e aggiorna l'albero di codifica
	 *
	 * @param string il valore da codificare
	 * @param path percorso del dizionario in uso. Consente il caricamento dell'albero di codifica
	 *             e della lista di valori conosciuti.
	 * @param coded BitSequence su cui viene concatenata la codifica di string
	 * @return percorso del prossimo dizionario da usare
	 * @throws IOException
	 */
	private Object[] codeType1(String string, String path, BitSequence coded) throws IOException
	{
		Tree tree;
		List<String> values;
		try {
			tree = loadTree(path);
			values = loadValues(path);
		}
		catch (FileNotFoundException fnfe)
		{
			reset(path);
			return codeType1(string,path,coded);
		}
		int intvalue = values.indexOf(string);
		System.out.print("Coding \""+string+"\": "+intvalue+" ---> ");
		if (intvalue == -1) //Nuovo valore
		{
			coded = coded.concat(tree.getCode(intvalue)).concat(new BitSequence((string+"\n").getBytes(StandardCharsets.ISO_8859_1)));
			intvalue = values.size();
			tree.insertInto(intvalue);
			saveValue(path,string);
			new File(path+"/"+intvalue).mkdir();
		}
		else //Valore già conosciuto
		{
			coded = coded.concat(tree.getCode(intvalue));
			tree.insertInto(intvalue);
		}
		saveTree(path,tree);
		System.out.println(coded+ "("+coded.length()+")");
		return new Object[]{coded,path+"/"+intvalue};
	}

	private String[] decodeType1(BitInputStream stream, String path) throws IOException
	{
		Tree tree;
		List<String> values;
		try {
			tree = loadTree(path);
			values = loadValues(path);
		}
		catch (FileNotFoundException fnfe)
		{
			reset(path);
			return decodeType1(stream,path);
		}
		BitSequence coded = new BitSequence();
		int index = 0;

		int v=-1;
		String value = "";
		System.out.println("Decoding using "+path+": tree is "+(tree.isEmpty()?"":"not ")+"empty");
		if (!tree.isEmpty())
		{
			while(!tree.contains(coded)) {
				coded.set(index, stream.read());
				index++;
			}
			v = tree.getValue(coded);
			System.out.println("\tRead bit sequence "+coded+"\n\tValue: "+v);
		}

		if (v==-1) //NYT
		{
			v = values.size();

			tree.insertInto(v);
			String boxbyte = "";
			BitSequence boxbits;
			while(!boxbyte.equals("\n"))
			{
				boxbits = new BitSequence();
				int bit;
				for (int i=0; i<8; i++) {
					bit = stream.read();
					if (bit ==-1)
						System.exit(1);
					boxbits.set(i, bit);
				}
				boxbyte = new String(boxbits.toBytes(),StandardCharsets.ISO_8859_1);
//				System.out.println("\tRead character "+boxbits+" = "+boxbyte);
				value = value+boxbyte;
			}
			value = value.substring(0,value.length()-1);
			System.out.println("\tNew value ("+v+"): "+value);
			saveValue(path,value);
			new File(path+"/"+v).mkdir();
		}
		else if (v<-1)
		{
			throw new IOException("illegal value");
		}
		else
			value = values.get(v);
		tree.insertInto(v);
		saveTree(path,tree);

		return new String[] {value,path+"/"+v};
	}

	private Object[] codeType2(ListCouples map, String path, BitSequence coded) throws IOException
	{
		Object[] box = {coded,path};
		for (String n: map.names())
		{
			box = codeType1(n,(String)box[1],(BitSequence)box[0]);
			box = codeType1(map.get(n),(String)box[1],(BitSequence)box[0]);
		}
		return codeType1("",(String)box[1],(BitSequence)box[0]); //La stringa vuota mi serve in decompressione per sapere quando ho terminato
	}

	private Object[] decodeType2(BitInputStream stream, String path) throws IOException
	{
		String[] dec = decodeType1(stream,path);
		ListCouples map = new ListCouples();
		String name = dec[0],value;
		path = dec[1];

		while(!name.equals(""))
		{
			dec = decodeType1(stream,path);
			value = dec[0];
			path = dec[1];
			map.put(name,value);

			dec = decodeType1(stream,path);
			name = dec[0];
			path = dec[1];
		}
		return new Object[] {map,path};
	}

	private Object[] codeType3(String string, String path, BitSequence coded) throws IOException
	{
		Object[] box = {coded,path};
		if (string.equals("*"))
			return codeType1(string,(String)box[1],(BitSequence)box[0]);

		if (string.startsWith("/"))
			box = codeRelativeURI(string,(String)box[1],(BitSequence)box[0]);
		else box = codeAbsoluteURI(string,(String)box[1],(BitSequence)box[0]);

		return codeType1("",(String)box[1],(BitSequence)box[0]); //La stringa vuota mi serve in decompressione per sapere quando ho terminato
	}

	private String[] decodeType3(BitInputStream stream, String path) throws IOException
	{
		String[] dec = decodeType1(stream,path);
		String value = "";
		String box = dec[0];
		path = dec[1];

		while(!box.equals(""))
		{
			value = value+box;
			dec = decodeType1(stream,path);
			box = dec[0];
			path = dec[1];
		}
		return new String[] {value,path};
	}

	private Object[] codeRelativeURI(String string, String path, BitSequence coded) throws IOException
	{
		// Il primo carattere sarà sempre / e questo mi crea un livello non necessario nei dizionari se lascio '/' come suffisso
		// perché codificherò sempre e solo '/'.
		// Quindi metto '/' come prefisso
		string = string.substring(1);
		int index = string.indexOf("/");
		String dom;
		Object[] box = {coded,path};
		while(index >-1)
		{
			dom = "/"+string.substring(0,index);
			string = string.substring(index+1);
			index = string.indexOf("/");
			box = codeType1(dom,(String)box[1],(BitSequence)box[0]);
		}
		return codeType1("/"+string,(String)box[1],(BitSequence)box[0]);
	}

	private Object[] codePeerURI(String string, String path, BitSequence coded) throws IOException
	{
		//Qui invece posso lasciare '.' come suffisso.
		int index = string.indexOf(".");
		String dom;
		Object[] box = {coded,path};
		while(index >-1)
		{
			dom = string.substring(0,index+1);
			string = string.substring(index+1);
			index = string.indexOf(".");
			box = codeType1(dom,(String)box[1],(BitSequence)box[0]);
		}
		return codeType1(string,(String)box[1],(BitSequence)box[0]);
	}

	private Object[] codeAbsoluteURI(String string, String path, BitSequence coded) throws IOException
	{
		/*
			schema url http secondo rfc		http://<host>:<port>/<path>?<searchpart>
			ma non contempla ne i server dns, ne protocolli basati su http (vedi https).
			Quindi riformulo in 	protocol://<peer>/<path>?<searchpart>
			Nota che quel <path> non ha niente a che fare con il path parametro, che verrà rinominato in "relative"
			La parte /<path>?<searchpart> è opzionale
		 */
		int index = string.indexOf(":");
		String protocol = string.substring(0, index+3); //			protocol://
		String peer = string.substring(index+3);
		String relative = null;
		index = peer.indexOf("/");
		Object[] box = {coded,path};

		if (index > -1)
		{
			relative = peer.substring(index); //					relative inizia per /
			peer = peer.substring(0,index);
		}

		box = codeType1(protocol,(String)box[1],(BitSequence)box[0]);
		box = codePeerURI(peer,(String)box[1],(BitSequence)box[0]);
		if (relative != null) {
			box = codeRelativeURI(relative, (String)box[1],(BitSequence)box[0]);
		}
		return box;
	}


}
