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
	ArrayList<Piece> pieces = new ArrayList<Piece>();
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
	//add block to piece, keep synchronization in tact
	public void addBlock(byte[] b, int begin,int index){
			//starting position of the piece
			Piece p = pieces.get(index);
			p.writeBlock(b, begin)
			//piece is finished
			if(p.haveAllBlocks == 0){
				gotten[index] = true;
			}else{
				getting[index] = false;
			}
	}
	//check if we have the piece
	public boolean havePiece(int index){
		synchronized(indexLock){
			if(gotten[index]){
				return true
			}
			else{
				return false
			}
		}
	}

	//only call after havePiece
	public byte[] getPiece(int index, int length){
			return pieces.get(index).getBytes();	
	}

	//Method to give an incomplete piece to a thread so it can try to get a block
	public int nextPieceIndex(){
		synchronized(indexLock){
				for(int i = 0; i < numPieces; i++){
					if(!gotten[i]){
						if(!getting[i]){
							getting[i] = true;
							return i;
							}
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

}