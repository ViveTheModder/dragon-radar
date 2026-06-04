package cmd;
//Dragon Radar: PAK File Validator class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PakFileValidator {
	private static final int NUM_CONTENTS_COSTUME = 252;
	private static final int NUM_CONTENTS_CONTAINER = 3;
	
	private RandomAccessFile raf;
	private boolean bigEndian, hasValidSize;
	private int numContents;
	
	public PakFileValidator(File f) throws IOException {
		raf = new RandomAccessFile(f, "r");
		numContents = ValueHandler.getVal(raf, true);
		if (numContents < 0) bigEndian = true;
		raf.seek(0);
		numContents = ValueHandler.getVal(raf, bigEndian);
		raf.seek((numContents + 1) * 4);
		int fileSize = ValueHandler.getVal(raf, bigEndian);
		hasValidSize = fileSize == raf.length();
	}
	
	public boolean isBigEndian() {
		return bigEndian;
	}
	public boolean isCharaCostume() {
		return hasValidSize && (numContents == NUM_CONTENTS_COSTUME);
	}
	public boolean isCharaContainer() {
		return hasValidSize && (numContents == NUM_CONTENTS_CONTAINER);
	}
}
