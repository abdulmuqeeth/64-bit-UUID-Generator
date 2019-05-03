package main;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/*
 * 64-bit UUID generator
 * The distributed system has 1024 nodes with nodeIds: 0-1023
 * Each node receives up to 100000 requests per second
 * This design makes the node handle up to 128 requests per millisecond
 * 
 * UUID structure: Time Stamp + Node ID + Counter
 * 
 *  Counter bits: 8
 *  127 can fit in 7 bits. 
 *  To allow for vertical scaling 1 additional bit is allocated to counter.
 * 	
 * Node ID bits: 13
 * 1023 can fit in 10 bits
 * To allow for horizontal scaling by 8x in the future 3 more bits are allocated 
 * 
 * 
 * Time stamp bits: 43
 * Current time stamp occupies 41 bits. Same 41 bits can handle time stamps for additional ~16 years
 * Two additional bits allow for unique time stamps generation for next ~200 years
 * 
 * Final UUID structure:
 * 		43 bit time stamp + 13 bit node id + 8 bit counter = 64 bits
*/
public class UUIDGenerator {
	
	/*
	 * Max number of requests per second is 128 
	 * Hence max value of counter is 127
	 * In case of vertical scaling this can be changed to up to 255
	 * 
	 *  Max value of time stamp is 2^43 - 1 = 8796093022207L
	 */
	final static long MAX_COUNTER = 127L;
	final static long MAX_TIMESTAMP = 8796093022207L;
	static long LAST_ID = (timestamp() << 21) + (nodeId() << 8);
	
	public static long getId(long last_id) {
		
		long uuid;
		
		/*
		 * Extracting time stamp 
		 */
		long last_timestamp = (last_id & (MAX_TIMESTAMP << 21));
		
		if(last_timestamp != (timestamp() << 21)) {
			uuid =  (timestamp() << 21) + (nodeId() << 8);
		} else {
			
			/*
			 * Extracting the counter (least significant 8 bits)
			 * If counter is not 127 then increment the counter by 1 and return the uuid
			 * Else wait till the next millisecond and generate the uuid with counter 0
			 * In case of vertical scaling
			 */
			long counter = (last_id & MAX_COUNTER);
			if (counter != MAX_COUNTER) {
				uuid = last_id+1L;
			}else {
				while(last_timestamp == (timestamp() << 21));
				
				uuid =  (timestamp() << 21) + (nodeId() << 8);	
			}
		}
		
		LAST_ID = uuid;
		return uuid;
		
	}
	
	/*
	 * This method returns time in milliseconds since epoch
	 */
	public static long timestamp() {
		return Instant.now().toEpochMilli();
	}
	
	/*
	 * This methods returns the nodeID
	 * Here it is hard coded to 255 since this class is modeling a particular node
	 * With horizontal scaling max value can be 8191 for 13 bits 
	 */
	public static int nodeId() {
		return 255;
	}
	
	/*
	 * Driver method to run and test
	 */
	public static void main(String[] args) {
		List<Long> uuids = new ArrayList<Long>();
		uuids.add(LAST_ID);
		
		for(int i = 0; i <= 500; i++) {
			uuids.add(getId(LAST_ID));
		}
		
		for(long id: uuids) {
			System.out.println(id);
		}
		
	  }
}





