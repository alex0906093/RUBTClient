import java.io.*;
import java.nio.*;
import java.util.Arrays;

public class Message {

    //Declare message types
    
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
    
    //create the protocol byte array of a string for the initial handshake message
    
    public static byte[] PROTOCOL_ID = new byte[] {'B', 'i', 't', 'T', 'o', 'r', 'r', 'e', 'n', 't', ' ',
        'p', 'r', 'o', 't', 'o', 'c', 'o', 'l'};
    
    //declare variables used throughout the message objects
    
    public byte[] mess = null;          //the message itself
    public final int length;            //length of the message for use in byte array
    public final byte id;               //id of the message type
    public byte[] info_hash = null;     //id the hash key, used for handshake
    public byte[] pieceOfFile = null;   //this is the peice of the file that is sent in piece messages
    public byte[] peerID = null;        //idetifies the peer used in handshake messages
    
    
    //creating handshake messages will be different from creating other types
    //need a special constructor for handshakes
    
    public Message(byte[] info_hash, byte[] peerID){
        
        this.info_hash = info_hash;
        this.peerID = peerID;
        this.length = 0;
        this.id = HANDSHAKE_ID;
        this.mess = new byte[62];   //initialize the message
        this.mess[0] = (byte) 19;   //put the length of the protocol id in the first spot of the array
        
        //fill the message with the protocol id, the hash info, then the peerid
	System.arraycopy( PROTOCOL_ID, 0, this.mess, 1, 19);
        System.arraycopy( info_hash, 0, this.mess, 28, 20);
        System.arraycopy( peerID, 0, this.mess, 48, 14);
        
        
    }
    
    
    //create a constructor for all the other types of messages
    
    public Message( int lengthPrefix, byte messageID){
        
        this.length = lengthPrefix;
        this.id = messageID;
        this.mess = new byte[this.length + 4];
        
        //create the messages with the length in the first four bytes (as an integer) and the
        //ID in the last byte
        
        if( this.id == CHOKE_ID){
            System.arraycopy(byteArrayFromInt(1), 0, this.mess, 0, 4);
            this.mess[4] = (byte) CHOKE_ID;
            
        }else if( this.id == UNCHOKE_ID){
            System.arraycopy(byteArrayFromInt(1), 0, this.mess, 0, 4);
            this.mess[4] = (byte) UNCHOKE_ID;
            
        }else if( this.id == INTERESTED_ID){
            System.arraycopy(byteArrayFromInt(1), 0, this.mess, 0, 4);
            this.mess[4] = (byte) INTERESTED_ID;
            
        }else if( this.id == NOT_INTERESTED_ID){
            System.arraycopy(byteArrayFromInt(1), 0, this.mess, 0, 4);
            this.mess[4] = (byte) NOT_INTERESTED_ID;
            
        }else if( this.id == HAVE_ID){
            System.arraycopy(byteArrayFromInt(5), 0, this.mess, 0, 4);
            this.mess[4] = (byte) HAVE_ID;
            
        }else if( this.id == REQUEST_ID){
            System.arraycopy(byteArrayFromInt(13), 0, this.mess, 0, 4);
            this.mess[4] = (byte) REQUEST_ID;
            
        }else if( this.id == PIECE_ID){
            System.arraycopy(byteArrayFromInt(this.length), 0, this.mess, 0, 4);
            this.mess[4] = (byte) PIECE_ID;
            
        }else if ( this.id == KEEP_ALIVE_ID){
            
            //do nothing
        }
        //we dont have to worry about cancel or bitfield yet
        
    }
    
    
    public void setLoad( int pieceBegin, int pieceIndex, byte[] pieceBlock,
                        int requestIndex, int requestBegin, int requestLength,
                        int havePayload){
        
        if( this.id == HAVE_ID){
            System.arraycopy(byteArrayFromInt(havePayload), 0, this.mess, 5, 4);
            
        }else if( this.id == REQUEST_ID){
            System.arraycopy(byteArrayFromInt(requestIndex), 0, this.mess, 5, 4);
            System.arraycopy(byteArrayFromInt(requestBegin), 0, this.mess, 9, 4);
            System.arraycopy(byteArrayFromInt(requestLength), 0, this.mess, 13, 4);
            
        }else if( this.id == PIECE_ID){
            this.pieceOfFile = pieceBlock;
            System.arraycopy(byteArrayFromInt(pieceIndex), 0, this.mess, 5, 4);
            System.arraycopy(byteArrayFromInt(pieceBegin), 0, this.mess, 9, 4);
            
            //when setting the rest of the message to be the piece, we have to
            //take into account the length of the previously initialized byte array
            System.arraycopy(pieceBlock, 0, this.mess, 13, this.length - 9);
            
        }else{
            //all the other message we are dealing with at this time dont have a payload
            System.out.println( "This message type has no payload");
            return;
        }
        
        return;
    }
    
    
    public byte[] getLoad() throws Exception{
        
        byte[] load = null;
        
        //check to see if we have a valid message type
        
        if(this.id == HAVE_ID || this.id == REQUEST_ID || this.id == PIECE_ID){
            
            //return the correct load for each of the different id types
            if( this.id == HAVE_ID){
                //have types only contain a load of one number
                load = new byte[4];
                System.arraycopy( this.mess, 5, load, 0, 4);
                return load;
                
            }else if( this.id == REQUEST_ID){
                //return the three ints that are contain in the message
                load = new byte[13];
                System.arraycopy( this.mess, 5, load, 0, 12);
                return load;
                
            }else{
                //we have a piece message
                load = new byte[this.length-1];
                System.arraycopy( this.mess, 5, load, 0, this.length - 1);
                return load;
                
            }
        }else{
            //we have an invalid message type with no load
            throw new Exception( "This message has no payload");
        }
    }
    
    
    public static byte[] byteArrayFromInt( int num ){
        
        byte[] array = new byte[4];     //integers are of size 4 bytes
        
        //we use bitwise shifts to get the corresponding bytes
        array[0] = (byte) (num >> 24);
        array[1] = (byte) (num >> 16);
        array[2] = (byte) (num >> 8);
        array[3] = (byte) (num);
        
        return array;
    }
    
    
    //we should create a method that does the opposite for use in printing / debuging
    public static final int intFromByteArray( byte[] array){
        return java.nio.ByteBuffer.wrap(array).getInt();
    }
    
    
    public String toString(){
        String retString = "";
        
        if(this.id == KEEP_ALIVE_ID){
            retString = "Keep alive message";
        }else if( this.id == CHOKE_ID){
            retString = "Choke message";
        }else if( this.id == UNCHOKE_ID){
            retString = "Unchoke message";
        }else if( this.id == INTERESTED_ID){
            retString = "Interested message";
        }else if( this.id == NOT_INTERESTED_ID){
            retString = "Not interested message";
        }else if( this.id == HAVE_ID){
            retString = "Have message";
        }else if( this.id == REQUEST_ID){
            retString = "Request Message";
        }else if( this.id == HANDSHAKE_ID){
            retString = "Handshake Message";
            for(int i = 0; i <20 ; i++){
                System.out.print( this.mess[i]);
            }
            System.out.println("Info Hash:");
            for(int i = 0; i > this.info_hash.length; i++){
                System.out.print( this.info_hash[i] + " ");
            }
            System.out.println("Peer ID:");
            for(int i = 0; i > peerID.length; i++){
                System.out.print( peerID[i]);
            }
            System.out.println();
            return retString;
            
        }else if( this.id == PIECE_ID){
            retString = "Piece Message";
            System.out.println("Piece Message");
            //use the array to int method
            System.out.println("Piece index: " + intFromByteArray(Arrays.copyOfRange(this.mess, 5, 8)));
            System.out.println("Begin Index: " + intFromByteArray(Arrays.copyOfRange(this.mess, 9, 12)));
            System.out.println("Piece block: ");
            for(int i = 0; i < this.pieceOfFile.length; i++){
                System.out.print( this.pieceOfFile[i]);
            }
            return retString;
            
        }
        return retString;
    }
}
