import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;

public class Peer implements Runnable{
	public GiventTools.TorrentInfo tInfo = null;
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

	public Peer(String ipAdd, int port){
		this.ipAdd = ipAdd;
		this.port = port;
	}

	public Peer(String ipAdd, int port, GiventTools.TorrentInfo tInfo, byte[] peerID){
		this.ipAdd = ipAdd;
		this.port = port;
		this.tInfo = tInfo;

		try{
			socket = new Socket(ipAdd, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
			dInStream = new DataInputStream(input);
			dOutStream = new DataOutputStream(output);
		}catch(Exception e){
			System.out.println("Connection setup failed");
		}
		if(!(sendHandshake(tInfo.info_hash.array(), peerid))){
			System.out.println("Handshake Failed");
			return;
		}
		try{
			download();
		}catch(Exception e){
			System.out.println("Exception: Could not download file");
		}
		try{
			completeCon();
		}catch (Exception e){
			System.out.println("Exception: could not complete download");
		}
	}
	//handshake method
	public boolean sendHandshake(byte[] info_hash, byte[] peerid){
		Message handshake = new Message(info_hash, peerid);
		boolean ret;
		try{
			dOutStream.write(handshake.message);
			dOutStream.flush();
			socket.setSoTimeout(timeoutTime);
			byte[] receiveShake = new byte[68];
			dInStream.readFully(receiveShake);
			byte[] peerInfoHash = Arrays.copyOfRange(receiveShake, 28, 48);
			value = Arrays.equals(peerInfoHash, info_hash) ? true : false;
			return ret;
		}catch(Exception e){
			System.out.println("Exception thrown for handshake");
		}
	}

	public boolean download(){
		Message mainMessage = new Message(1, (byte) 2);
		Message requested = null;
		byte[] buf = null;
		int lastPiece;
		int numPieces = 0;
		int begin = 0;
		int count = 16384
		int difference;

			for(int i = 0; i < 6; i++){
				dInStream.readByte();
			}
			dOutStream.write(mainMessage.message);
			dOutStream.flush();
			socket.setSoTimeout(timeoutTime);

			for(int i = 0; i < 5; i++){
				if(i == 4 && dInStream.readByte() == 1){
					break;
				}
				dInStream.readByte();
			}
			difference = tInfo.piece_hashes.length - 1;
			
	}


}