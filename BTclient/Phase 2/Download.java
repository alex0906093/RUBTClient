import java.util.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.net.Socket;

public class Download Implements Runnable{
	/*GLOBALS*/
	public static final int KBLIM = 16384;
	public final int timeoutTime = 130000;
	public void run(){
		System.out.println("Running thread for Download");
	}

	public boolean downloadPiece(Peer p, Message m) throws Exception{
		p.dOutStream.write(m.mess);
		p.dOutStream.flush();
		p.socket.setSoTimeout(timeoutTime);
	}
}