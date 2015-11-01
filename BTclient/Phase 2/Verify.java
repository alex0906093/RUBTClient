import java.security.MessageDigest;
import java.util.Arrays;


public class Verify{
	TorrentInfo t;
	
	public Verify(TorrentInfo t;){
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
		if(Arrays.equals(piece_hash, tInfo.piece_hashes[index])){
			return true;
		}
		else{
			System.out.println("Hash for piece " + index + "Did not check out");
			return false;
		}
	}
}