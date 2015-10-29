import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;


public class Piece{
	byte[] rawBytes;
	int numBlocksGotten;
	int blockSize;
	int pieceSize;
	int[] blocksGottenIndex;
	public Piece(byte[] rawBytes, int numBlocksGotten, int blockSize, int pieceSize){
		this.rawBytes = rawBytes;
		this.numBlocksGotten = numBlocksGotten;
		this.blockSize = blockSize;
		this.pieceSize = pieceSize;
		blocksGottenIndex = new int[pieceSize/blockSize];
		
	}
}