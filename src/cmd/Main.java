package cmd;
//Dragon Radar: Main class by ViveTheJoestar
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;
import gui.Program;

public class Main {
	public static int[] getParamCfgVals(String paramType) throws IOException {
		int lineCnt = 0;
		int[] cfgVals = new int[4];
		File cfg = new File("./res/cfg/" + paramType + ".cfg");
		if (!cfg.exists()) return null;
		Scanner sc = new Scanner(cfg);
		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			line = line.substring(0, line.indexOf(" #"));
			if (line.matches("\\d+") && lineCnt < cfgVals.length)
				cfgVals[lineCnt] = Integer.parseInt(line);
			else {
				sc.close();
				return null;
			}
			lineCnt++;
		}
		sc.close();
		return cfgVals;
	}
	public static String getCharaName(String pakName) {
		//Get rid of last underscore for damaged costume to prevent confusion
		pakName = pakName.replace("p_dmg", "pdmg");
		int lastUnderscoreInName = pakName.lastIndexOf("_");
		return pakName.substring(0, lastUnderscoreInName);
	}
	public static void writeParamInfoToCsv(File[] dats, String paramType, boolean bigEndian) throws IOException {
		String csvText = ",";
		File[] csvFiles = CsvHandler.getAvailableCsvFiles();
		int searchResult = CsvHandler.getCsvSearchResult(csvFiles, paramType);
		if (searchResult < 0) return;
		String[] lines = CsvHandler.getLines(csvFiles[searchResult]);
		String[] paramNames = new String[lines.length];
		int[] positions = new int[lines.length];
		for (int posCnt = 0; posCnt < positions.length; posCnt++) {
			String[] lineArray = lines[posCnt].split(",");
			positions[posCnt] = Integer.parseUnsignedInt(lineArray[0]);
			paramNames[posCnt] = lineArray[1];
		}		
		for (File dat: dats) csvText += getCharaName(dat.getName().replace(paramType, "")) + ",";
		csvText += "\n";
		for (int posCnt = 0; posCnt < positions.length; posCnt++) {
			csvText += paramNames[posCnt] + ",";
			for (File dat: dats) {
				ParamFileReader pfr = new ParamFileReader(dat, bigEndian);
				csvText += pfr.getParamVal(positions[posCnt]) + ",";
			}
			csvText += "\n";
		}
		File csv = dats[0].getParentFile().toPath().resolve("common-params.csv").toFile();
		FileWriter csvWriter = new FileWriter(csv);
		csvWriter.write(csvText);
		csvWriter.close();
	}
	public static void main(String[] args) throws Exception {
		boolean bigEndian = false;
		String[] possibleArgs = { "-w", "-r", "-csv" };
		String[] argDescs = {
			"Extracts parameter files from BT3 character costume files (PAK) or BT4 character container files (PAK).",
			"Displays information from parameter files in a folder.",
			"Writes information from parameter files in a folder to a CSV (to form a table)."
		};
		if (args.length > 2) {
			int[] cfgVals = null;
			File folder = new File(args[1]);
			if (!args[2].equals("")) cfgVals = Main.getParamCfgVals(args[2]);
			if (cfgVals == null) {
				System.out.println("Configuration file belonging to the parameter type is either empty or not present!");
				return;
			}
			if (!folder.isDirectory()) {
				System.out.println("Provided directory is not a folder!");
				return;
			}
			else {
				if (args[1].toUpperCase().equals("N")) folder = new File("./res/prm/");
				if (!folder.exists()) {
					System.out.println("Default parameter directory (/res/prm/) does not exist!");
					return;
				}
			}
			if (args[0].equals("-w")) {
				File[] paks = folder.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						boolean matchCostumeName = name.matches("[a-zA-Z0-9_]+_\\dp.pak") || name.matches("[a-zA-Z0-9_]+_\\dp_dmg.pak");
						boolean matchContainerName = name.endsWith("_container.pak");
						return matchCostumeName ^ matchContainerName;
					}
				});
				for (File pak: paks) {
					PakFileValidator fv = new PakFileValidator(pak);
					bigEndian = fv.isBigEndian();
					if (fv.isCharaContainer()) ParamFileRetriever.writeCommonParamsFromContainer(pak, args[2], cfgVals[2], cfgVals[1], bigEndian);
					else if (fv.isCharaCostume()) ParamFileRetriever.writeCommonParamsFromCostume(pak, args[2], cfgVals[0], cfgVals[1], bigEndian);
				}
			}
			else if (args[0].equals("-r")) {
				File[] dats = folder.listFiles((dir, name) -> name.endsWith("_" + args[2] + ".dat"));
				for (File dat: dats) {
					ParamFileReader pfr = new ParamFileReader(dat, bigEndian);
					System.out.println("[" + dat.getName() + "]\n" + pfr.readParamInfo(args[2]));
				}
			}
			else if (args[0].equals("-csv")) {
				File[] dats = folder.listFiles((dir, name) -> name.endsWith("_" + args[2] + ".dat"));
				System.out.print("Writing parameter info to CSV...");
				writeParamInfoToCsv(dats, args[2], cfgVals[3] == 1);
				System.out.println(" DONE!");
			}
		}
		else if (args.length > 0) {
			if (args[0].equals("-h")) {
				String helpText = "USAGE: java -jar dragon-radar.jar \"arg\" \"path/to/folder/\" [param-type]\nPossible Values for \"arg\":\n";
				for (int argCnt = 0; argCnt < possibleArgs.length; argCnt++)
					helpText += "* " + possibleArgs[argCnt] + ": " + argDescs[argCnt] + "\n";
				System.out.print("[Dragon Radar by ViveTheJoestar]" + "\n" + helpText);
			}
		}
		else Program.run();
	}
}