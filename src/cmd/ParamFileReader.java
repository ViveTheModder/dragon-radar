package cmd;
//Dragon Radar: Parameter File Reader class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ParamFileReader {
	private boolean bigEndian;
	private RandomAccessFile param;
	private String charaName;
	
	public ParamFileReader(File f, boolean be) throws IOException {
		bigEndian = be;
		charaName = Main.getCharaName(f.getName().replace("common_param", ""));
		param = new RandomAccessFile(f, "r");
	}
	
	public String getCharaName() {
		return charaName;
	}
	public String getParamVal(int pos) throws IOException {
		byte[] valBytes = new byte[4];
		param.seek(pos);
		param.read(valBytes);
		String paramStr = "";
		if (ValueHandler.isFloat(valBytes)) paramStr += ValueHandler.getValFloat(valBytes, bigEndian);
		else paramStr += ValueHandler.getVal(valBytes, bigEndian);
		return paramStr;
	}
	public String readParamInfo(String paramType) throws IOException {
		String paramInfo = "";
		File[] csvFiles = CsvHandler.getAvailableCsvFiles();
		int searchResult = CsvHandler.getCsvSearchResult(csvFiles, paramType);
		if (searchResult < 0) return null;
		String[] lines = CsvHandler.getLines(csvFiles[searchResult]);
		String[] paramNames = new String[lines.length];
		int[] positions = new int[lines.length];
		for (int posCnt = 0; posCnt < positions.length; posCnt++) {
			String[] lineArray = lines[posCnt].split(",");
			positions[posCnt] = Integer.parseUnsignedInt(lineArray[0]);
			paramNames[posCnt] = lineArray[1];
		}
		for (int posCnt = 0; posCnt < positions.length; posCnt++) {
			String paramStr = paramNames[posCnt] + ": " + getParamVal(positions[posCnt]) + "\n";
			paramInfo += paramStr;
		}
		return paramInfo;
	}
	public String[] getParamVals(String paramType) throws IOException {
		File[] csvFiles = CsvHandler.getAvailableCsvFiles();
		int searchResult = CsvHandler.getCsvSearchResult(csvFiles, paramType);
		if (searchResult < 0) return null;
		String[] lines = CsvHandler.getLines(csvFiles[searchResult]);
		String[] paramVals = new String[lines.length];
		int[] positions = new int[lines.length];
		for (int posCnt = 0; posCnt < positions.length; posCnt++) {
			String[] lineArray = lines[posCnt].split(",");
			if (lineArray[0].matches("\\d+")) {
				positions[posCnt] = Integer.parseUnsignedInt(lineArray[0]);
				paramVals[posCnt] = getParamVal(positions[posCnt]);
			}
			else return null;
		}
		return paramVals;
	}
}
