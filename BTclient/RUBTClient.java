import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URL;
import java.net.HttpURLConnection;

public class RUBTClient{
    public String file_destination;

	public static void main(String[] args){
		String torrentFN;
		String saveFN;
		String encodedText = "";
		byte[] b = null;
		if(args.length == 2){
			torrentFN = args[0];
			saveFN = args[1];
			file_destination = saveFN;
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
        GivenTools.TorrentInfo info = null;
        try{
        info = new GivenTools.TorrentInfo(b);
        System.out.println(info.file_name);
        }catch(GivenTools.BencodingException e){
            System.out.println("Bencoding Exception");
        }
        try{
            System.out.println(info.announce_url.toString());
            //HttpClientExample http = new HttpClientExample();
            sendGet(info.announce_url);
        }catch(Exception e){
            System.out.println("fuck off");
        }
	}
    
    public static void sendGet(URL url) throws Exception{
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        System.out.println("\nSent GET Request, Response Code: " + responseCode);
       
    }
    
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        char[] hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
            'B', 'C', 'D', 'E', 'F' };
        
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            byte hi = (byte) ((b >> 4) & 0x0f);
            byte lo = (byte) (b & 0x0f);
            sb.append('%').append(hex[hi]).append(hex[lo]);
        }
        
        return sb.toString();
    }
    
}
