import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;


public class RUBTClient{

	public static void main(String[] args){
		String torrentFN;
		String saveFN;
		if(args.length == 2){
			torrentFN = args[0];
			saveFN = args[1];
			//System.out.println("torrent file: " + torrentFN + " save file: " + saveFN);
		}
		else{
			System.out.println("Invalid number of command line arguments");
			return;
		}
		File torrentFile = new File(torrentFN);
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(torrentFile));
			String encodedText = null;
			String getLines = null;
			while((getLines = reader.readLine()) != null){
				encodedText = encodedText + getLines;
			}
			System.out.println("encoded text is :" + encodedText);
			return;
		} catch(FileNotFoundException e){
			System.out.print("File not found: ");
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		} finally {
			try{
				if(reader != null){
					reader.close();
				}
			} catch(IOException e){
				System.out.println("Error: ");
				e.printStackTrace();
			}

		}
	}


}
