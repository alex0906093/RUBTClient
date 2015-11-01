import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;

public class Seed Implements Runnable{
	/*GLOBALS*/
	MemCheck gMem = null;
	public static final byte PIECE_ID = 7;
	public Seed(){
		this.gMem = RUBTClient.globalMemory;
	}
	public void sendPiece(Peer p, int pieceIndex){
		Piece piece = gMem.getPiece(pieceIndex);
		int bLength = piece.blockSize;
		byte[] rawbytes = piece.getBytes();
		int pmsSent = 0;
		int len = 9 + piece.blockSize;

		if(piece.numBlocks == 0){
			p.dOutStream.writeInt(len);
			p.dOutStream.writeByte(PIECE_ID);
			p.dOutStream.writeInt(0);
			p.dOutStream.write(rawBytes);
			p.dOutStream.flush();
		}
		else{
			//send a series of piece messages to send the complete piece to the peer
			while(pmsSent < pieces.numBlocks){
				int begin = pmsSent * pieces.blockSize;
				byte[] data = new byte[pieces.blockSize];
				System.arraycopy(pieceIndex,begin,data,0,piece.blockSize);
				PieceMessage pm = new PieceMessage(pieceIndex,begin,data);
				p.dOutStream.writeInt(pm.length);
				p.dOutStream.writeByte(PIECE_ID);
				p.dOutStream.writeInt(pm.getBegin());
				p.dOutStream.write(pm.getData());
				p.dOutStream.flush();
			}
		}

	}
	public void run(){
		System.out.println("Running thread for Seeding");
	}

}

