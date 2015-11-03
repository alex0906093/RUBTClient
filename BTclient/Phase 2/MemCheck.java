//package client;
import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.io.RandomAccessFile;
//import GivenTools;
/*
 *This class will be used as the 
 *main memory so we can run threads 
 *and have the memory locked safely
 */

public class MemCheck{
	public ArrayList<Piece> pieces = new ArrayList<Piece>();
	private Object memLock = new Object();
	private Object indexLock = new Object();
	private int nextIndex = 0;
	public int numPieces;
	public boolean[] gotten;
	public boolean[] getting;
	public TorrentInfo tInfo;
	public int numPiecesGotten;
	public boolean isFinished;
	private RandomAccessFile file;
	public int lastByteSize;
	int openPeers = 0;
	//constructor
	public MemCheck(TorrentInfo tInfo, RandomAccessFile file){
		this.file = file;
		this.tInfo = tInfo;
		this.numPiecesGotten = 0;
		isFinished = false;
		pieces_make();
	}
	//set global memory appropriately
	private void pieces_make(){
		System.out.println("file size is " + tInfo.file_length);
		int lastByteSize = tInfo.file_length %tInfo.piece_length;
		this.lastByteSize = lastByteSize;
		int tmp = tInfo.file_length - lastByteSize;
		System.out.println("last piece size is " + lastByteSize);
		System.out.println("tmp is " + tmp );
		this.numPieces = tmp/tInfo.piece_length;
		//Maybe not?
		this.numPieces++; 
		this.gotten = new boolean[numPieces];
		this.getting = new boolean[numPieces];
		System.out.println("numPieces is " + numPieces);
		for(int i = 0; i < numPieces;i++){
			if(i == numPieces-1){
				int a = (lastByteSize < 16384) ? lastByteSize : 16384;
				Piece p = new Piece(a,lastByteSize,i);
				System.out.println("last piece blocksize is " + lastByteSize);
				this.pieces.add(p);
			}else{
				Piece p = new Piece(16384,tInfo.piece_length,i);
				this.pieces.add(p);
			}
		}
	}
	//add block to piece, keep synchronization in tact
	public void addBlock(byte[] b, int begin,int index){
			//starting position of the piece
			Piece p = pieces.get(index);
			p.writeBlock(b, begin);
			//piece is finished
			if(p.haveAllBlocks() == 0){
				gotten[index] = true;
			}else{
				getting[index] = false;
			}
	}
	public void writeFile(byte[] b, int pIndex){
		synchronized(this.file){
			try{
				int r = pIndex*tInfo.piece_length;
				try{
					System.out.println("file length here is " + this.file.length());
				}catch(IOException e){}
				System.out.println("b.length is " + b.length);
				System.out.println("r is "+ r);
				this.file.seek(r);
				this.file.write(b);
			}catch(IOException e){}
		}
	}
	//check if we have the piece
	public boolean havePiece(int index){
		synchronized(indexLock){
			if(gotten[index]){
				return true;
			}
			else{
				return false;
			}
		}
	}

	/*
	public byte[] getPieceBytes(int index){
			return pieces.get(index).getBytes();	
	}
	*/
	public Piece getPiece(int index){
		return pieces.get(index);
	}

	//Method to give an incomplete piece to a thread so it can try to get a block
	public int nextPieceIndex(){
		synchronized(indexLock){
				for(int i = 0; i < numPieces; i++){
					if(!gotten[i]){
							return i;
						}
					}
				}
				//Check if we have all of the pieces already, if so return -1 to end program
				int check = 0;
				for(int j = 0; j < numPieces; j++){
					if(gotten[j]){
						check++;
					}
				}
				if(check == numPieces){
					isFinished = true;
					return -1;
				}
				//tell the thread to sleep and check again a little later
				return -2;
		}

	}

