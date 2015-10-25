import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;

public class Download Implements Runnable{
	/*GLOBALS*/
	public static final byte KEEP_ALIVE_ID = -1;
    
    public static final byte CHOKE_ID = 0;
    
    public static final byte UNCHOKE_ID = 1;
    
    public static final byte INTERESTED_ID = 2;
    
    public static final byte NOT_INTERESTED_ID = 3;
    
    public static final byte HAVE_ID = 4;
    
    public static final byte BITFEILD_ID = 5;
    
    public static final byte REQUEST_ID = 6;
    
    public static final byte PIECE_ID = 7;
    
    public static final byte CANCEL_ID = 8;
    
    public static final byte HANDSHAKE_ID = 9;
	public final int timeoutTime = 130000;
	public int pSize;
	

	public void run(){
		System.out.println("Running thread for Download");
	}

	public boolean sendPeer(Peer p, Message m) throws Exception{
		p.dOutStream.write(m.mess);
		p.dOutStream.flush();
		p.socket.setSoTimeout(timeoutTime);
		byte[] b = new byte[pSize];
		int length = p.dInStream().readInt();
		int id = p.dInStream().readByte();
		switch(id){
			case CHOKE_ID://need to see what to do
				return false;
			case UNCHOKE_ID:
				return false;
			case INTERESTED_ID:
				return false;
			case HAVE_ID:
				return false;
			case BITFEILD_ID:
				return false;
			case REQUEST_ID:{
				int pieceIndex = p.dInStream().readInt();
				int begin = p.dInStream().readInt();
				int blockLength = p.dInStream().readInt();
				//make a new message to write to peer if we have the piece requested
				Seed s = new Seed();
				s.sendMessage(pieceIndex,begin,blockLength);
			}
			case PIECE_ID:{
				//get data
				int pieceIndex = p.dInStream().readInt();
				int begin = p.dInStream().readInt();
				byte data = new byte[length - 9];
				p.dInStream().readFully(data);
				RUBTClient.globalMemory.add(index, data);
			}
		}

	}
}