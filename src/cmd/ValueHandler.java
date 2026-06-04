package cmd;
//Dragon Radar: Value Handler class by ViveTheJoestar
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ValueHandler {
	public static boolean isFloat(byte[] valBytes) {
		//Check for positive floats
		if (valBytes[3] >= 0x30 && valBytes[3] <= 0x4F) return true;
		//Check for negative floats
		if (valBytes[3] >= 0xB0 && valBytes[3] <= 0xCF) return true;
		return false;
	}
	public static byte[] getValBytes(int val, boolean bigEndian) {
		byte[] valBytes = new byte[4];
		int numBits = 0;
		if (bigEndian) {
			//Same code as the Little Endian version; just the for-loop is changed (to go from right to left)
			for (int i = 3; i > 0; i--) {
				valBytes[i] = (byte) (val >> numBits);
				numBits += 8;
			}
		}
		else {
			for (int i = 0; i < 4; i++) {
				/* Literally the steps taken in the getVal() method, but the inverse:
				 * shift right by 8 bits (1 byte) 4 times to get the bytes from left to right. */
				valBytes[i] = (byte) (val >> numBits);
				numBits += 8;
			}
		}
		return valBytes;
	}
	public static float getValFloat(byte[] valBytes, boolean bigEndian) {
		ByteBuffer bb = ByteBuffer.wrap(valBytes);
		if (!bigEndian) bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}
	public static int getVal(byte[] intBytes, boolean bigEndian) {
		int numBits = 24, output = 0;
		for (int i = 0; i < 4; i++) {
			/* Steps taken:
			 * 1. Bitwise AND the current byte with 0xFF to make it unsigned
			 * 2. Because the byte is now 32 bits (0xFF is an int), shift left
			 * to set the position in the output for the byte's bits (24-31, 16-23, 8-15, 1-7) 
			 * 3. Bitwise OR the bits to add them to the output */
			output |= ((intBytes[i] & 0xFF) << numBits);
			numBits -= 8; //8 bits = 1 byte
		}
		//If only I knew about this method sooner (RIP LittleEndian "library")
		if (bigEndian) return output;
		return Integer.reverseBytes(output);
	}
	public static int getVal(RandomAccessFile raf, boolean bigEndian) throws IOException {
		byte[] intBytes = new byte[4];
		raf.read(intBytes);
		int numBits = 24, output = 0;
		for (int i = 0; i < 4; i++) {
			output |= ((intBytes[i] & 0xFF) << numBits);
			numBits -= 8;
		}
		if (bigEndian) return output;
		return Integer.reverseBytes(output);
	}
}