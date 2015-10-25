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
	public void addPiece(byte[] b){
		synchronized(memLock){
			pieces.add(lock1);
		}
	} 
}