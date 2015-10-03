import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class RUBTClient{

	public static void main(String[] args){
		String torrentFN;
		String saveFN;
		String encodedText = "";
		byte[] b = null;
		if(args.length == 2){
			torrentFN = args[0];
			saveFN = args[1];
			//System.out.println("torrent file: " + torrentFN + " save file: " + saveFN);
		}
		else{
			System.out.println("Invalid number of command line arguments");
			return;
		}
		BufferedReader reader = null;
		try{
			b = Files.readAllBytes(Paths.get(torrentFN));
		} catch(FileNotFoundException e){
			System.out.println("Caught Exception File not found");
			//e.printStackTrace();
		} catch(IOException e){
			System.out.println("Caught Exception IOException");
			//e.printStackTrace();
		} finally {
			try{
				if(reader != null){
					reader.close();
				}
			} catch(IOException e){
				System.out.println("Caught Exception IOException");
				//e.printStackTrace();
			}

		}
		//b = encodedText.getBytes(Charset.forName("UTF-8"));
		Object o = null;
		String w = b.toString();
		System.out.println(w);
		try{
		o = GivenTools.Bencoder2.decode(b);
		}catch(GivenTools.BencodingException l){
			l.printStackTrace();
		}
		//System.out.println(o);
	}
}
