import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;

public class Peer {
	public TorrentInfo tInfo = null;
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
	private int am_choking = 1;
	private int am_interested = 0;
	private int peer_choking = 1;
	private int peer_interested = 0;
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
	public Peer(String ipAdd, int port, TorrentInfo tInfo, byte[] peerID){
		//Initialize variables
		this.ipAdd = ipAdd;
		this.port = port;
		this.tInfo = tInfo;
		System.out.println("IP Address of peer is " + ipAdd + "opening Socket");
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
			e.printStackTrace();
		}
		try{
			closeCon();
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
	
	public void closeCon() throws Exception{
		socket.close();
		dInStream.close();
		dOutStream.close();
		fOutStream.close();
	}
}


	