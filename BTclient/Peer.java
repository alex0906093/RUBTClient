import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;

public class Peer implements Runnable{
	public GivenTools.TorrentInfo tInfo = null;
	ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	public int port;
	public String ipAdd;
	public Socket socket = null;
	public OutputStream output = null;
	public InputStream input = null;
	public DataOutputStream dOutStream = null;
	public DataInputStream dInStream = null;
	public final int timeoutTime = 130000;
	FileOutputStream fOutStream = null;
	public static final int KBLIM = 16384;
	/*
	 *Constructor for just IP Address and Port Num
	 */

	public Peer(String ipAdd, int port){
		this.ipAdd = ipAdd;
		this.port = port;
	}
	/*
	 *Constructor for peer when establishing a connection and reading/writting
	 */
	public Peer(String ipAdd, int port, GivenTools.TorrentInfo tInfo, byte[] peerID){
		//Initialize variables
		this.ipAdd = ipAdd;
		this.port = port;
		this.tInfo = tInfo;

		//try to establish a connection and open a socket
		try{
			socket = new Socket(ipAdd, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
			dInStream = new DataInputStream(input);
			dOutStream = new DataOutputStream(output);
		}catch(Exception e){
			System.out.println("Connection setup failed");
		}
		//set up a handshake
		if(!(sendHandshake(tInfo.info_hash.array(), peerID))){
			System.out.println("Handshake Failed");
			return;
		}
		//call the download function to write all of the pieces of the torrent to a local file.
		try{
			boolean success = download();
			if(!success){
				System.out.println("download Failed!");
			}
			else{
				System.out.println("download Suceeeded!");
			}
		}catch(Exception e){
			System.out.println("Exception: Could not download file");
		}
		try{
			socket.close();
			dInStream.close();
			dOutStream.close();
			fOutStream.close();
		}catch (Exception e){
			System.out.println("Exception: could not complete download");
		}
	}
	//handshake method
	public boolean sendHandshake(byte[] info_hash, byte[] peerid){
		Message handshake = new Message(info_hash, peerid);
		boolean ret;
		try{
			dOutStream.write(handshake.mess);
			dOutStream.flush();
			socket.setSoTimeout(timeoutTime);
			byte[] receiveShake = new byte[68];
			dInStream.readFully(receiveShake);
			byte[] peerInfoHash = Arrays.copyOfRange(receiveShake, 28, 48);
			ret = Arrays.equals(peerInfoHash, info_hash) ? true : false;
			return ret;
		}catch(Exception e){
			System.out.println("Exception thrown for handshake");
		}
		return true;
	}

	/*
	 *
	 *METHOD TO DOWNLAOD THE FILE
	 *
	 */

	public boolean download() throws Exception{
		Message mainMessage = new Message(1, (byte) 2);
		Message request = null;
		byte[] buff = null;
		byte[] pieceSub = null;
		int lastPiece;
		int numPieces = 0;
		int begin = 0;
		int count = KBLIM;
		int difference;

			for(int i = 0; i < 6; i++){
				dInStream.readByte();
			}
			dOutStream.write(mainMessage.mess);
			dOutStream.flush();
			socket.setSoTimeout(timeoutTime);

			for(int i = 0; i < 5; i++){
				if(i == 4 && dInStream.readByte() == 1){
					break;
				}
				dInStream.readByte();
			}
			difference = tInfo.piece_hashes.length - 1;
			lastPiece = tInfo.file_length - (difference * tInfo.piece_length);
			fOutStream = new FileOutputStream(new File(RUBTClient.file_destination));
			boolean gotPiece = false;

			//set up while loop to see if we have all the pieces
			while(numPieces != tInfo.piece_hashes.length){
				//set up loop for each piece to download
				while(!gotPiece){
					if(numPieces + 1 == tInfo.piece_hashes.length){
						request = new Message(13, (byte) 0);
						count = (lastPiece < KBLIM) ? lastPiece : KBLIM;
						lastPiece -= KBLIM;
						request.setLoad(-1, -1, null, numPieces, begin, count, -1);
						dOutStream.write(request.mess);
						dOutStream.flush();
						socket.setSoTimeout(timeoutTime);
						buff = new byte[4];
							for(int i = 0; i < 4; i++)
								buff[i] = dInStream.readByte();
						
						pieceSub = new byte[count];
					
							for(int i = 0; i < 9; i++)
								dInStream.readByte();
							
							for(int i =0; i < count; i++)
								pieceSub[i] = dInStream.readByte();

						this.pieces.add(pieceSub);
						fOutStream.write(pieceSub);
							if(lastPiece < 0){
								numPieces++;
								gotPiece=true;
								continue;
							}
						begin += count;
					} else{
						request = new Message(13, (byte) 6);
						request.setLoad(-1, -1, null, numPieces, begin, KBLIM, -1);
						dOutStream.write(request.mess);
						dOutStream.flush();
						socket.setSoTimeout(timeoutTime);
						buff = new byte[4];
							for(int i = 0; i < 4; i++)
								buff[i] = dInStream.readByte();

							for(int i = 0; i < 9; i++)
								dInStream.readByte();

							for(int i = 0; i < KBLIM; i++)
								pieceSub[i] = dInStream.readByte();
							
						this.pieces.add(pieceSub);
						fOutStream.write(pieceSub);

							if(begin + KBLIM == tInfo.piece_length){
								numPieces++;
								begin = 0;
								gotPiece=true;
								continue;
							}	else{
								begin += KBLIM;
							}
					}

				}
				gotPiece = false;
			}
			return true;
			
	}
	public void run(){
		System.out.println("Running");
	}


}
