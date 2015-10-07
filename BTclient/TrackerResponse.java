import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/*
 *Class to grab repsonse from Bittorrent Tracker
 *
 */
public class TrackerResponse{
    public String TrackerID;
    public String failureReason;
    public String failureMessage;
    public int interval;
    public int minInterval;

}