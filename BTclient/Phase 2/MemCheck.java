import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;

/*
 *This class will be used as the 
 *main memory so we can run threads 
 *and have the memory locked safely
 */

public class MemCheck{
	ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	private Object memLock = new Object();
	public MemCheck(){
		
	}
	public void addPiece(byte[] b, int index){
		synchronized(memLock){
			pieces.add(index, b);
		}
	}

	public boolean havePiece(int index){
		synchronized(memLock){
			if(pieces.isEmpty() || pieces.size() < index || pieces.get(index) = null){
				return false;
			}else{
				return true;
			}
		}
	}
	public byte[] getPiece(int index){
		synchronized(memLock){
			return pieces.get(i);	
		}
		
	} 
}