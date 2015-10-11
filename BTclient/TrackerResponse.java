import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.HashMap;

/*
 *Class to grab repsonse from Bittorrent Tracker
 *
 */
public class TrackerResponse{
    /*
     *Fields to be received from tracker
     */
    public String TrackerID;
    public ArrayList<Peer> peers;
    public String failureReason;
    public String failureMessage;
    public int interval;
    public int minInterval;
    public int complete;
    public int incomplete;
    /*
     *Keys for fields
     */
    public static final ByteBuffer KEY_FAILURE = ByteBuffer.wrap(new byte[]{'f', 'a', 'i','l','u', 'r', 'e', ' ', 'r', 'e', 'a', 's', 'o', 'n',});
    public static final ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', 's' });
    public static final ByteBuffer KEY_INTERVAL = ByteBuffer.wrap(new byte[] {'i', 'n', 't', 'e', 'r', 'v', 'a','l' });
    public static final ByteBuffer KEY_MIN_INTERVAL = ByteBuffer.wrap(new byte[] { 'm', 'i', 'n', ' ', 'i', 'n', 't', 'e', 'r','v', 'a', 'l' });
    public static final ByteBuffer KEY_COMPLETE = ByteBuffer.wrap(new byte[] {'c', 'o', 'm', 'p', 'l', 'e', 't', 'e' });
    public static final ByteBuffer KEY_INCOMPLETE = ByteBuffer.wrap(new byte[] {'i', 'n', 'c', 'o', 'm', 'p', 'l', 'e', 't', 'e' });
    
    public TrackerResponse(HashMap<ByteBuffer, Object> response) throws Exception{
        /*
         *get all of the keys from the hash map and set them accordingly
         *
         */
        if(response.containsKey()){
            throw new Exception("Tracker Failed Key Detected");
        }
        if(response.containsKey(KEY_INTERVAL)){
            this.interval = (Integer) response.get(KEY_INTERVAL);
        }else{
            System.out.println("No Intverval, set to 0");
            this.interval = 0;
        }
        if (response.containsKey(KEY_COMPLETE))
            this.complete = (Integer) response.get(KEY_COMPLETE);
        else {
            System.out.println("Warning: no complete, setting to zero");
            this.complete = 0;
        }
        if (response.containsKey(KEY_INCOMPLETE))
            this.incomplete = (Integer) response.get(KEY_INCOMPLETE);
        else {
            System.out.println("Warning: no incomplete, setting to zero");
            this.incomplete = 0;
        }
        if (response.containsKey(KEY_MIN_INTERVAL))
            this.minInterval = (Integer) response.get(KEY_MIN_INTERVAL);
        else {
            System.out.println("Warning: no min interval, setting to zero");
            this.minInterval = 0;
        }
        ByteBuffer peersResponse = (ByteBuffer) response.get(KEY_PEERS);
        this.peers = new ArrayList<Peer>();

        for (int i = 0; i < NUM_PEERS; i++){
            try{
                //read the peer response and add to the list of peers
                String peerIP = "";
                peerIP += peersResponse.get() & 0xff;peerIP += ":";
                peerIP += peersResponse.get() & 0xff;peerIP += ":";
                peerIP += peersResponse.get() & 0xff;peerIP += ":";
                peerIP += peersResponse.get() & 0xff;
                int peerPort = peersResponse.get() * 256 + peersResponse.get();
                this.peers.add(new Peer(peerIP, peerPort));
            }
            catch (Exception e){

            }
        }
    }
    
    
}