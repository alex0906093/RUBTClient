import java.util.ArrayList;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.*;
import java.io.UnsupportedEncodingException;

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
    public final int NUM_PEERS = 33;
    public static final ByteBuffer KEY_FAILURE = ByteBuffer.wrap(new byte[]{'f', 'a', 'i','l','u', 'r', 'e', ' ', 'r', 'e', 'a', 's', 'o', 'n',});
    public static final ByteBuffer KEY_PEERS = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', 's' });
    private static final ByteBuffer KEY_IP = ByteBuffer.wrap(new byte[] { 'i','p' });
    private static final ByteBuffer KEY_PEER_ID = ByteBuffer.wrap(new byte[] {'p', 'e', 'e', 'r', ' ', 'i', 'd' });
    public static final ByteBuffer KEY_PORT = ByteBuffer.wrap(new byte[] {'p', 'o', 'r', 't'});
    public static final ByteBuffer KEY_INTERVAL = ByteBuffer.wrap(new byte[] {'i', 'n', 't', 'e', 'r', 'v', 'a','l' });
    public static final ByteBuffer KEY_MIN_INTERVAL = ByteBuffer.wrap(new byte[] { 'm', 'i', 'n', ' ', 'i', 'n', 't', 'e', 'r','v', 'a', 'l' });
    public static final ByteBuffer KEY_COMPLETE = ByteBuffer.wrap(new byte[] {'c', 'o', 'm', 'p', 'l', 'e', 't', 'e' });
    public static final ByteBuffer KEY_INCOMPLETE = ByteBuffer.wrap(new byte[] {'i', 'n', 'c', 'o', 'm', 'p', 'l', 'e', 't', 'e' });
    
    public TrackerResponse(HashMap<ByteBuffer, Object> response) throws Exception{
        /*
         *get all of the keys from the hash map and set them accordingly
         *
         */
        if(response.containsKey(KEY_FAILURE)){
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
        //need to figure out how to decode this response map correctly
        //ArrayList<HashMap<ByteBuffer, Object>> encodedPeerList = null;
        
        
        this.peers = new ArrayList<Peer>();
        
        for (Object element : (ArrayList<?>) response.get(KEY_PEERS) ){
                //@SuppressWarning("unchecked");
                Map<ByteBuffer, Object> peerMap= (Map<ByteBuffer, Object>)element;
                
            
            if(!peerMap.containsKey(KEY_PORT) || !peerMap.containsKey(KEY_IP) || !peerMap.containsKey(KEY_PEER_ID)){
                System.out.println("Missing information about peer, skipping");
                continue;
            }
            int peerPort =((Integer) peerMap.get(KEY_PORT)).intValue();
            String peerIP = objectToStr(peerMap.get(KEY_IP));
            byte[] pid = ((ByteBuffer) peerMap.get(KEY_PEER_ID)).array(); 
            if(objectToStr(peerMap.get(KEY_PEER_ID)).contains("RU")){
                this.peers.add(new Peer(peerIP, peerPort));
            }

        }
    

    }
        public static String objectToStr(Object o){
        
        if(o instanceof Integer){
            return String.valueOf(o);
        } else if(o instanceof ByteBuffer){
            try {
                return new String(((ByteBuffer) o).array(),"ASCII");
            } catch (UnsupportedEncodingException e) {
                return o.toString();
            }
        }else if(o instanceof Map<?,?>){
            
            String retStr = "";
            for (Object name: ((Map<?, ?>) o).keySet()){
                String value = objectToStr(((Map<?, ?>) o).get(name));  
                retStr += objectToStr(name) + ": " + value + "\n";  
            } 
            
            return retStr;
        }else if(o instanceof List){
            
            String retStr = "";
            for(Object elem: (List<?>)o){
                retStr += objectToStr(elem) + "\n";
            }
            return retStr;
        }
        return o.toString();
    }
    
    
}