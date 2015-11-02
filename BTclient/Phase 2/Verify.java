//package client;

import java.security.MessageDigest;
import java.util.Arrays;
import java.security.NoSuchAlgorithmException;
import java.nio.ByteBuffer;
//import GivenTools;

public class Verify{
	TorrentInfo t;
	
	public Verify(TorrentInfo t){
		this.t = t;
	}
	
	//check hash against tInfo
	public boolean checkHash(byte[] check, int index){
		MessageDigest d = null;
		try{
			d = MessageDigest.getInstance("SHA-1");
		}catch(NoSuchAlgorithmException e){
			System.out.println("No such algorithm " + e.getMessage());
			return false;
		}
		byte[] piece_hash = d.digest(check);
		d.update(piece_hash);
		ByteBuffer buf = t.piece_hashes[index];
		byte[] c = new byte[20];
		buf.get(c, index*20, index*20+19);
		if(Arrays.equals(piece_hash, c)){
			return true;
		}
		else{
			System.out.println("Hash for piece " + index + "Did not check out");
			return false;
		}
	}
}