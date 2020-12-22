package huffman.bytecodec.encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import huffman.tree.*;

public class ByteEncoder {
	
	public FileInputStream in = null;
	public BitByteOutputStream out = null;
	private String outpath;
    
    public static void main(String[] args) {
    	if(args.length < 2) {
    		System.err.println("Usage: huffman.bytecodec.encoder inputFile outputFile");
    		args = new String[2];
    		args[0] = "C://Users/Francesco/Desktop/miniature-test/test.mp3";
			args[1] = "C://Users/Francesco/Desktop/miniature-test/test.v";
			main(args);
    	}
    	else {
    		ByteEncoder enc = new ByteEncoder(args[0],args[1]);
    		Tree tree = new Tree();
    		File in = new File(args[0]);
    		long t = System.nanoTime();
    		enc.encode(tree);
    		long at = System.nanoTime();
    		File out = new File(args[1]);
    		System.out.println("Finished compression of: "+in.getName()+" in "+(float)(at-t)/1000000+" ms");
    		System.out.println("Original size: "+in.length()+" bytes");
    		System.out.println("Compressed size: "+out.length()+" bytes");
    		System.out.println("Compression ratio: "+((float)in.length()/(float)out.length()));

    		tree.printTree(true);
//    		System.out.println(tree.order);
    	}
	}
    
    public ByteEncoder(String in, String out) {
    	try {
			this.in = new FileInputStream(in);
			this.out = new BitByteOutputStream(new FileOutputStream(out));
			outpath = out;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
    	
    }

	public void encode(Tree tree) {
		
		try {
			
			int c = 0;
			int cont = 0;
			while((c = in.read()) != -1)
			{
				//Piccolo test su save/load di Tree
				if (cont==100000)
				{
					File f = new File(outpath+".tree");
					System.out.println("Salvo l'albero di codifica");
					tree.save(f);
					System.out.println("Ricarico l'albero di codifica");
					tree = Tree.load(f);
				}
				cont++;

				if (tree.contains(c)) {
					out.write(tree.getCode(c));
					tree.insertInto(c);
				}
				else {
					out.write(tree.getCode(c));
					out.writeByte(c);
					tree.insertInto(c);
				}
			}
			out.flush();
		}
		catch (IOException e) {
			System.err.println("Error reading from input");
			e.printStackTrace();
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	

}
