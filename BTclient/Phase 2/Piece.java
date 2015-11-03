//package client;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
//import GivenTools;

public class Piece{
	public byte[] rawBytes;
	public int numBlocksGotten;
	public int blockSize;
	public int pieceSize;
	public int pieceIndex;
	public int[] blocksGottenIndex;
	public int numBlocks;
	private Object lock1;
	public Verify verify;
	public boolean verified;
	public boolean[] wroteBlock;
	public Piece(int blockSize, int pieceSize, int pieceIndex){
		synchronized(this){
		this.rawBytes = new byte[pieceSize];
		this.numBlocksGotten = 0;
		this.verified = false;
		this.blockSize = blockSize;
		this.pieceSize = pieceSize;
		this.pieceIndex = pieceIndex;
		this.numBlocks = pieceSize/blockSize;
		if(pieceSize%blockSize != 0){
			this.numBlocks++;
		}
		this.blocksGottenIndex = makeBlockArray();
		this.wroteBlock = new boolean[pieceSize/blockSize];
		this.verify = new Verify(RUBTClient.tInfo);
		}
	}
	
	private int[] makeBlockArray(){
		int[] a = new int[numBlocks];
		for(int i = 0; i < numBlocks; i++){
			a[i] = 0;
		}
		return a;
	}

	public void writeBlock(byte[] b, int begin){
		synchronized(this){
			if(rawBytes[begin] != 0){
				return;
			}
			int a;
			if(begin == 0){
				a = 0;
			}else{
				a = pieceSize/begin;
				a--;
			}
			wroteBlock[a] = true;
		System.out.println("Piece size is " + pieceSize + " Writing Block " + a + " of " + numBlocks + " to piece " + pieceIndex);
		System.out.println("byte array length is " + b.length);
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
			if(!wroteBlock[i]){
				return i;
			}
		}
		if(numBlocks == 0){
			return 0;
		}
		return -1;

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
			if(!this.wroteBlock[i]){
				System.out.println("Block " + i + " of Piece " + pieceIndex + " is 0");
				RUBTClient.globalMemory.getting[pieceIndex] = false;
				return 0;
			}
		}	//if our bytes
			if(!verify.checkHash(rawBytes, pieceIndex)){
				System.out.println("The data was corrupt, trying to redownload");
				reset();
				return -1;
			}else{
				verified = true;
				RUBTClient.globalMemory.numPiecesGotten++;
				RUBTClient.globalMemory.gotten[pieceIndex] = true;
				RUBTClient.globalMemory.writeFile(rawBytes, pieceIndex);
				System.out.println("Wrote piece " + pieceIndex);
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