import java.util.Iterator;

public class BitfieldItterate implements Iterable<Boolean>{
	private final byte[] bitfield;

	public BitfieldItterate(byte[] bitfield){
		this.bitfield = bitfield;
	}
	public Iterator<Boolean> iterator(){
		return new Iterator<Boolean>(){
			private int bitIndex = 0;
			private int arrayIndex = 0;
		
		public boolean hasNext(){
			return(arrayIndex < bitfield.length) && (bitIndex<8);
		}
		public boolean next(){
			boolean val = (bitfield[arrayIndex] >>(7-bitIndex) & 1) == 1;
			bitIndex++;
			if(bitIndex == 8){
				bitIndex = 0;
				arrayIndex++;
			}
			return val;
		}
		public void remove(){
			throw new UnsupportedOperationExcetption();
		}	
	};
	}
}