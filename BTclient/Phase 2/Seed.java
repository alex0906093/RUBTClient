import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;

public class Seed Implements Runnable{
	/*GLOBALS*/

	public Seed(){

	}
	public void sendMessage(int pieceIndex, int begin, int blockLength){
		if(RUBTClient.havePiece(pieceIndex)){
			byte[] data = RUBTClient.getPiece(pieceIndex);
			PieceMessage sendMessage = new PieceMessage(pieceIndex, begin, data);
			
		}


		

	}
	public void run(){
		System.out.println("Running thread for Seeding");
	}

}

