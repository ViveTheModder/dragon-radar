package cmd;
//Dragon Radar: Character Parameter File Retriever class by ViveTheJoestar
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class ParamFileRetriever {	
	public static void writeCommonParamsFromCostume(File charaPak, String paramType, int paramPos, int paramSize, boolean bigEndian) throws IOException {
		RandomAccessFile pak = new RandomAccessFile(charaPak, "r");
		pak.seek(paramPos);
		int commonParamOffset = ValueHandler.getVal(pak, bigEndian);
		byte[] paramBytes = new byte[paramSize];
		pak.seek(commonParamOffset);
		pak.read(paramBytes);
		pak.close();
		File paramFolder = charaPak.getParentFile().toPath().resolve(paramType).toFile();
		paramFolder.mkdir();
		String pakName = charaPak.getName();
		String charaName = Main.getCharaName(pakName);
		File paramFile = paramFolder.toPath().resolve(charaName + "_" + paramType + ".dat").toFile();
		RandomAccessFile param = new RandomAccessFile(paramFile, "rw");
		param.write(paramBytes);
		param.close();
	}
	public static void writeCommonParamsFromContainer(File pakFile, String paramType, int paramPos, int paramSize, boolean bigEndian) throws IOException {
		RandomAccessFile pak = new RandomAccessFile(pakFile, "r");
		pak.seek(8);
		int paramPckOffset = ValueHandler.getVal(pak, bigEndian) + paramPos;
		byte[] paramBytes = new byte[paramSize];
		pak.seek(paramPckOffset);
		pak.read(paramBytes);
		pak.close();
		File paramFolder = pakFile.getParentFile().toPath().resolve(paramType).toFile();
		paramFolder.mkdir();
		String pakName = pakFile.getName();
		String charaName = Main.getCharaName(pakName);
		File paramFile = paramFolder.toPath().resolve(charaName + "_" + paramType + ".dat").toFile();
		RandomAccessFile param = new RandomAccessFile(paramFile, "rw");
		param.write(paramBytes);
		param.close();
	}
}