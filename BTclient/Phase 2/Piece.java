import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;


public class Piece{
	public byte[] rawBytes;
	public int numBlocksGotten;
	public int blockSize;
	public int pieceSize;
	public int pieceIndex;
	public int[] blocksGottenIndex;
	public int numBlocks;
	public Verify verify;
	public boolean verified;
	
	public Piece(int blockSize, int pieceSize, int pieceIndex){
		synchronized(this){
		this.rawBytes = new byte[pieceSize];
		this.numBlocksGotten = 0;
		this.verified = false;
		this.blockSize = blockSize;
		this.pieceSize = pieceSize;
		this.numBlocks = pieceSize/blockSize;
		blocksGottenIndex = makeBlockArray();
		this.verify = new Verify();
		}
	}
	
	private int[] makeBlockArray(){
		int[] a = new int[numBlocks];
		for(int i = 0; i < numBlocks; i++){
			a[i] = 0;
		}
	}

	public void writeBlock(byte[] b, int begin){
		synchronized(this){
			if(rawBytes[begin] == 0){
				return;
			}
		System.arraycopy(b,0,this.rawBytes,begin,b.length);
		}
	}
	public byte[] getBytes(){
		synchronized(this){
			return rawBytes;
		}
	}
	//figure out which block is needed next
	public int nextBlockNeeded(){
		for(int i = 0; i < numBlocks; i++){
			if(rawBytes[i*blockSize] == 0){
				return i;
			}
		}
		if(numBlocks == 0){
			return 0;
		}

	}
	/*
	 *Method to check if we have successfully downloaded all of the blocks
	 *return 0 if we have yet to receive all of the blocks
	 *return -1 if the bytes are corrupt
	 *return 1 if the hash checks out
	*/
	public int haveAllBlocks(){
		synchronized(this){
		for(int i = 0; i < numBlocks; i++){
			if(a[i] == 0){
				return 0;
			}
		}	//if our bytes
			if(!verify.checkBytes(rawBytes, pieceIndex)){
				System.out.println("The data was corrupt, trying to redownload");
				reset();
				return -1;
			}else{
				verified = true;
				RUBTClient.globalMemory.numPiecesGotten++;
				return 1;
			}
		}
	}
	//corrupt data, get it again
	private void reset(){
		this.rawBytes = new byte[pieceSize];
		this.blocksGottenIndex = makeBlockArray();
	}
}