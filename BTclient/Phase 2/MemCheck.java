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
	<ArrayListbyte[] pieces = new byte[tInfo.length];
	private Object memLock = new Object();
	private Object indexLock = new Object();
	private int nextIndex = 0;
	public int numPieces;
	boolean[] gotten;
	boolean[] getting;
	TorrentInfo tInfo;
	//constructor
	public MemCheck(TorrentInfo tInfo){
		this.tInfo = tInfo;
		pieces();
	}
	//set global memory appropriately
	private void pieces(){
		int lastByteSize = tInfo.length % tInfo.piece_length;
		int tmp = tInfo.length - lastByteSize;
		numPieces = tmp/tInfo.piece_length;
		numPieces++; 
		gotten = new boolean[numPieces];
		getting = new boolean[numPieces];
	}

	//add piece to our byte array, VERIFY BEFORE CALLING
	public void addPiece(byte[] b, int begin,int index){
		synchronized(memLock){
			//starting position of the piece
			int pieceStart = index * tInfo.piece_length;
			int position = pieceStart + begin ;
			System.arraycopy(b,0,pieces,position,b.length)
		}
	}

	public boolean havePiece(int index){
		synchronized(memLock){
			if(pieces[index] == 0){
				return false;
			}else{
				return true;
			}
		}
	}
	public byte[] getPiece(int index, int length){
		synchronized(memLock){
			byte[length] p;
			for(int i = index; i < length; i++){
				p[i] = pieces[i];
			}	
			return p;
		}	
	}
	public int nextPieceIndex(){
		synchronized(indexLock){
				//numPieces or numPieces + 1?
				for(int i = 0; i < numPieces; i++){
					if(!gotten[i]){
						if(!getting[i]){
							getting[i] = true;
							return i;
						}

					}
				}
				//Check if we have all of the pieces already, if so return -1 to end program
				int check;
				for(int j = 0; j < numPieces; j++){
					if(gotten[i]){
						check++
					}
				}
				if(check == numPieces){
					return -1;
				}
				//tell the thread to sleep and check again a little later
				return -2;
		}

	}
	public int gaveUpOnPiece(){

	} 
}