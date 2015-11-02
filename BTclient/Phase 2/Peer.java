//package client;

import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.lang.*;
import java.util.LinkedList;
import java.util.PriorityQueue;

//import GivenTools;

public class Peer implements Runnable{
	public TorrentInfo tInfo = null;
	ArrayList<byte[]> pieces = null;
	public int port;
	public String ipAdd;
	public Socket socket = null;
	public OutputStream output = null;
	public InputStream input = null;
	public DataOutputStream dOutStream = null;
	public DataInputStream dInStream = null;
	public final int timeoutTime = 130000;
	FileOutputStream fOutStream = null;
	public static final int KBLIM = 16384;
	private int am_choking = 1;
	private int am_interested = 0;
	private int peer_choking = 1;
	private int peer_interested = 0;
	private Queue<Integer> availablePieces;
	private LinkedList<Integer> gettingList;
	private LinkedList aPieces;
	public static final byte KEEP_ALIVE_ID = -1;
    
    public static final byte CHOKE_ID = 0;
    
    public static final byte UNCHOKE_ID = 1;
    
    public static final byte INTERESTED_ID = 2;
    
    public static final byte NOT_INTERESTED_ID = 3;
    
    public static final byte HAVE_ID = 4;
    
    public static final byte BITFEILD_ID = 5;
    
    public static final byte REQUEST_ID = 6;
    
    public static final byte PIECE_ID = 7;
    
    public static final byte CANCEL_ID = 8;
    
    public static final byte HANDSHAKE_ID = 9;

	public int pSize;
	public Seed seed;
	/*
	 *Constructor for just IP Address and Port Num
	 */


	public Peer(String ipAdd, int port){
		this.ipAdd = ipAdd;
		this.port = port;
	}
	/*
	 *Constructor for peer when establishing a connection and reading/writting
	 */
	public Peer(String ipAdd, int port, TorrentInfo tInfo, byte[] peerID){
		//Initialize variables
		this.ipAdd = ipAdd;
		this.port = port;
		this.tInfo = tInfo;
		this.availablePieces = new LinkedList<Integer>();
		System.out.println("IP Address of peer is " + ipAdd + "opening Socket");
		//try to establish a connection and open a socket
		try{
			socket = new Socket(ipAdd, port);
			input = socket.getInputStream();
			output = socket.getOutputStream();
			dInStream = new DataInputStream(input);
			dOutStream = new DataOutputStream(output);
		}catch(Exception e){
			System.out.println("Connection setup failed");
		}
		//set up a handshake
		if(!(sendHandshake(tInfo.info_hash.array(), peerID))){
			System.out.println("Handshake Failed");
			return;
		}
		this.seed = new Seed();
	}
	//handshake method
	public boolean sendHandshake(byte[] info_hash, byte[] peerid){
		Message handshake = new Message(info_hash, peerid);
		boolean ret;
		try{
			dOutStream.write(handshake.mess);
			dOutStream.flush();
			socket.setSoTimeout(timeoutTime);
			byte[] receiveShake = new byte[68];
			dInStream.readFully(receiveShake);
			byte[] peerInfoHash = Arrays.copyOfRange(receiveShake, 28, 48);
			ret = Arrays.equals(peerInfoHash, info_hash) ? true : false;
			return ret;
		}catch(Exception e){
			System.out.println("Exception thrown for handshake");
		}
		
		return true;
	}
	/*
	 *Method to simaltaniously download file from multiple peers
	 *
	 */
	public void run(){
		System.out.println("Running Thread");
		int nextMessage = 0;
		gettingList = new LinkedList<Integer>();
		nextMessage = RUBTClient.globalMemory.nextPieceIndex();
		boolean firstMess = true;
		//seed checker IP Address
		if(ipAdd.equals("128.6.171.132")){
			sendHave();
		}
		//because nextPieceIndex returns -1 if we have all of the pieces
		while(nextMessage != -1){
			if(RUBTClient.globalMemory.isFinished){
				break;
			}
			long threadId = Thread.currentThread().getId();
			System.out.println("Running loop on thread " + threadId);
			int interest = 1;
			if(firstMess){
				try{
					dOutStream.writeInt(interest);
					dOutStream.writeByte(INTERESTED_ID);
					dOutStream.flush();
					System.out.println("Wrote Interest");
					firstMess = false;
				}catch(IOException e){
					System.out.println("Writting Error");
				}
			}else if(peer_choking ==1){

			}else{
					int a = 0;
					while(availablePieces.peek() != null && RUBTClient.globalMemory.gotten[a=availablePieces.remove()]){
						if(RUBTClient.globalMemory.getting[a]){
							continue;
						}
						else{
							break;
						}
					}
					if(availablePieces.peek() == null){
						int o = 0;
						for(int m =0; m < 436; m++){
							if(!RUBTClient.globalMemory.gotten[m]){
								availablePieces.add(m);
								o++;
							}
						}
						if(o == 0)
							a = -1;
					}

					if(a != -1){
						System.out.println("Sending request for piece " + a);
						gettingList.add(a);
						try{
							RUBTClient.globalMemory.getting[a] = true;
							sendRequests(a);
						}catch(IOException e){

						}
					}

			}
			int messLen = 0;
			int id = 0;
			try{	
				messLen = dInStream.readInt();
				System.out.println("messLen is " + messLen);
				if(messLen == KEEP_ALIVE_ID){
					System.out.println("Keep Alive");
					continue;
				}else{
					id = dInStream.readByte();
					System.out.println("Got Bytes");
				}
			}catch(IOException e){
				System.out.println("Writting Error");
			}
			System.out.println("Got id " + (byte)id);
		switch(id){
			case KEEP_ALIVE_ID:{
				System.out.println("Keep Alive Message");
				if(RUBTClient.globalMemory.isFinished){
					break;
				}else{
					continue;
				}
			}
			case CHOKE_ID:{//we're being choked
				System.out.println("Choked");
				for(int m =0; m < 436; m++){

					if(!RUBTClient.globalMemory.gotten[m]){
						System.out.print("Didnt finish piece " + m);
						if(RUBTClient.globalMemory.getting[m]){
							System.out.print("and we think we're getting it \n");
						}
					}
				}
				synchronized(gettingList){
				for(int p : gettingList){
					if(RUBTClient.globalMemory.getting[p]){
						RUBTClient.globalMemory.getting[p] = false;
					}
					gettingList.remove(p);
				}
				}
				System.out.println(RUBTClient.globalMemory.numPiecesGotten + " " + RUBTClient.globalMemory.numPieces);
				if(peer_choking == 1){
					if(RUBTClient.globalMemory.isFinished){
						break;
					}else{
						try{
							Thread.sleep(1000);
						}catch(InterruptedException e){
							System.out.println("InterruptedException");
						}
						continue;
					}
				}else{
					peer_choking = 1;
					continue;
				}
			}
			case UNCHOKE_ID:{
				System.out.println("Unchoked");
				peer_choking = 0;
					int a = 0;
					while(availablePieces.peek() != null && RUBTClient.globalMemory.gotten[a=availablePieces.remove()]){
						if(RUBTClient.globalMemory.getting[a]){
							continue;
						}
						else{
							break;
						}
					}
					if(availablePieces.peek() == null){
						a = -1;
					}
					
					if(a != -1){
						System.out.println("Sending request for piece " + a);
						gettingList.add(a);
						try{
							RUBTClient.globalMemory.getting[a] = true;
							sendRequests(a);
						}catch(IOException e){

						}
					}
				continue;
			
			}
			case INTERESTED_ID:{
				peer_interested = 1;
				am_choking = 0;
				int len = 1;
				try{
				dOutStream.write(len);
				dOutStream.write(UNCHOKE_ID);
				System.out.println("Interested");
					if(RUBTClient.globalMemory.numPiecesGotten == 0){
						continue;
					}else{
					sendSomeHaves();
					}
				}catch(IOException e){
				System.out.println("Writting Error");
				}
				continue;
			}
			case HAVE_ID:{//They have a piece
					//see if we want it
					System.out.println("Have Message");
					try{
					int pIndex = dInStream.readInt();
					if(RUBTClient.globalMemory.havePiece(pIndex)){
						continue;
					}else{
						//we want it, send request messages 
						sendRequests(pIndex);
					}
					}catch(IOException e){
						System.out.println("Writting Error");
					}

			}case BITFEILD_ID:{
					System.out.println("Bitfield Message");
					try{
						int r = messLen - 1;
						byte[] bf = new byte[r];
						dInStream.readFully(bf);
						queueBitfield(bf);
					}catch(IOException e){
						System.out.println("problem reading Stream");
					}
					continue;
			}case HANDSHAKE_ID:{
				System.out.println("Handshake, should not be happening");
			}
				
			case REQUEST_ID:{
				System.out.println("Request Message");
				int pieceIndex = 0;
				int begin = 0;
				int blockLength = 0;
				try{
					pieceIndex = dInStream.readInt();
					begin = dInStream.readInt();
					blockLength = dInStream.readInt();
				}catch(IOException e){
					System.out.println("Writting Error");
				}
				//check if we have the piece, if so send it.
				if(RUBTClient.globalMemory.havePiece(pieceIndex)){
					try{
						seed.sendPiece(this, pieceIndex);
					}catch(IOException e){
						System.out.println("Error");
					}
				}else{//not sure for now if I don't have the piece
					continue;
				}
			}
			case PIECE_ID:{
				System.out.println("Piece Message");
				try{
					int lengthOfBlock = messLen - 9;
					int pieceIndex = dInStream.readInt();
					int begin = dInStream.readInt();
					if(pieceIndex == 91){
						try{
							Thread.sleep(1000);
						}catch(InterruptedException e){}
					}
					byte[] block = new byte[lengthOfBlock];
					for(int l = 0; l < lengthOfBlock; l++){
						block[l] = dInStream.readByte();
					}
					System.out.println("Got piece message for piece " + pieceIndex + " With offset " + begin);
					Piece gp = RUBTClient.globalMemory.pieces.get(pieceIndex);
					gp.writeBlock(block, begin);
					int c1 = gp.haveAllBlocks();
					System.out.println("c1 is " + c1);
					if(c1 == 0){
						continue;
					}else if(c1 == 1){
						RUBTClient.globalMemory.gotten[pieceIndex] = true;
						gettingList.remove(new Integer(pieceIndex));
						System.out.println("wrote piece " + pieceIndex + " to global memory");
						nextMessage = RUBTClient.globalMemory.nextPieceIndex();
						continue;
					}else{
						System.out.println("Problem with piece " + pieceIndex + "will try to fetch again");
						continue;
					}
				}catch(IOException e){
					System.out.println("Writting Error");
				}
			}case CANCEL_ID:{
				System.out.println("Cancel");
				try{
					int cIndex = dInStream.readInt();
					int cBegin = dInStream.readInt();
					int cLength = dInStream.readInt();
					System.out.println(" " + cIndex + " " +cBegin + " "+ cLength);
					processCancel(cIndex, cBegin, cLength);
				}catch(IOException e){}
				continue;
			}
			}
			//for the sake of efficiency
			firstMess = false;
			nextMessage = RUBTClient.globalMemory.nextPieceIndex();
			if(nextMessage > -1){
				try{
				sendRequests(nextMessage);
				}catch(IOException e){
				System.out.println("Writting Error here");
				}
			}
		}
	}
	public void sendRequests(int pIndex) throws IOException{
		Piece p = RUBTClient.globalMemory.getPiece(pIndex);
		int i = 0;
		if(p.numBlocks == 0){
			Message.RequestMessage rm = new Message.RequestMessage(pIndex, 0, p.blockSize);
			dOutStream.writeInt(13);
			dOutStream.writeByte(REQUEST_ID);
			dOutStream.writeInt(pIndex);
			dOutStream.writeInt(0);
			dOutStream.writeInt(p.blockSize);
			dOutStream.flush();
		}else{
			int mMade = 0;
			//request the block from this peer
			while(mMade < p.numBlocks){
				int begin = mMade * p.blockSize;
				Message.RequestMessage rm = new Message.RequestMessage(pIndex, begin, p.blockSize);
				dOutStream.writeInt(13);
				dOutStream.writeByte(REQUEST_ID);
				dOutStream.writeInt(pIndex);
				dOutStream.writeInt(begin);
				dOutStream.writeInt(p.blockSize);
				dOutStream.flush();
				mMade++;
			}
		}		
	}
	public void processCancel(int cIndex, int cBegin, int cLength){
		RUBTClient.globalMemory.getting[cIndex] = false;
		availablePieces.add(cIndex);
	}
	public void queueBitfield(byte[] bitfield){
		try{
		BitfieldIterable iter = new BitfieldIterable(bitfield);
		int i = 0;
		for(boolean val : iter){
			if(val){
				System.out.print(" " + i +" " );
				this.availablePieces.add(i);
			}
			i++;
		}}catch(UnsupportedOperationException e){

		}
	}
	public void sendSomeHaves() throws IOException{
		if(RUBTClient.globalMemory.numPiecesGotten == 0){
			//send a keep alive message
			int k = 0;
			dOutStream.writeInt(k);
			dOutStream.flush();
		}else{
			int haveLim = 0;
			for(int i = 0; i < pieces.size(); i++){
				if(haveLim == 10){
					break;
				}
				if(RUBTClient.globalMemory.pieces.get(i).verified){
					int len = 5;
					dOutStream.writeInt(len);
					dOutStream.writeByte(HAVE_ID);
					dOutStream.writeInt(i);
					dOutStream.flush();
					haveLim++;
				}
			}
		}
	}
	//for client 132, it will send a have message once we have blocks
	public void sendHave(){
		while(RUBTClient.globalMemory.numPiecesGotten == 0){
			try {
				long threadId = Thread.currentThread().getId();
				System.out.println("Sleeping on thread " + threadId);
				Thread.sleep(1000);
            }catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
		}
		int len;
		try{
		while(!RUBTClient.globalMemory.isFinished){
		for(int i = 0; i < pieces.size(); i++){
			if(RUBTClient.globalMemory.pieces.get(i).verified){
				len = 5;
				dOutStream.writeInt(len);
				dOutStream.writeByte(HAVE_ID);
				dOutStream.writeInt(i);
				dOutStream.flush();
			}
		}
		len = dInStream.readInt();
		int id = dInStream.readByte();
		if(id == REQUEST_ID){
			int pieceIndex = dInStream.readInt();
			int begin = dInStream.readInt();
			int length = dInStream.readInt();
			Piece p = RUBTClient.globalMemory.pieces.get(pieceIndex);
			if(p.verified){
				seed.sendPiece(this, pieceIndex);
				//dont want to waste too much time seeding
			try {
				Thread.sleep(1000);
            }catch(InterruptedException ex){
                Thread.currentThread().interrupt();
            }
				}
			} 
		}
	}catch(IOException e){
		System.out.println("Writting Error");
	}
	}
	public void closeCon() throws Exception{
		socket.close();
		dInStream.close();
		dOutStream.close();
		fOutStream.close();
	}
}


	