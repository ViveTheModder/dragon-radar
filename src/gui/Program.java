package gui;
//Dragon Radar: Program class by ViveTheJoestar
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import cmd.CsvHandler;
import cmd.Main;
import cmd.ParamFileReader;

public class Program {
	private static final String WINDOW_TITLE = "Dragon Radar";
	
	private static void error(Toolkit tk) {
		Runnable runWinErrorSnd = (Runnable) tk.getDesktopProperty("win.sound.exclamation");
		if (runWinErrorSnd != null) runWinErrorSnd.run();
	}
	private static void init(ParamFileReader[] readers, String paramType, String[] paramNames, String[] paramDescs,  Image img, Toolkit tk) throws Exception {
		//Set components
		Color bgColor = new Color(0x88, 0xC1, 0x63);
		Color lblColor = new Color(0xEB, 0xB0, 0x3D);
		Color prmColor = new Color(0x6E, 0xDF, 0xDF);
		Dimension minFrameSize = new Dimension(512, 256);
		Dimension minFieldSize = new Dimension(100, 50);
		Dimension minLabelSize = new Dimension(350, 50);
		Font charaLblFnt = new Font("Tahoma", Font.BOLD, 20);
		Font paramLblFnt = new Font("Tahoma", Font.BOLD, 16);
		JFrame frame = new JFrame(WINDOW_TITLE);
		JLabel[] charaNameLbls = new JLabel[readers.length];
		JPanel mainPanel = new JPanel(new GridLayout(1, readers.length));
		JPanel[] charaPanels = new JPanel[readers.length];
		JScrollPane scroll = new JScrollPane(mainPanel);
		for (int charaCnt = 0; charaCnt < readers.length; charaCnt++) {
			charaNameLbls[charaCnt] = new JLabel(readers[charaCnt].getCharaName());
			charaNameLbls[charaCnt].setAlignmentX(JLabel.CENTER_ALIGNMENT);
			charaNameLbls[charaCnt].setFont(charaLblFnt);
			charaNameLbls[charaCnt].setForeground(lblColor);
			charaNameLbls[charaCnt].setHorizontalAlignment(JLabel.LEFT);
			charaPanels[charaCnt] = new JPanel();
			charaPanels[charaCnt].setBackground(bgColor);
			charaPanels[charaCnt].setLayout(new BoxLayout(charaPanels[charaCnt], BoxLayout.Y_AXIS));
			Box lblBox = Box.createHorizontalBox();
			lblBox.add(Box.createHorizontalGlue());
			lblBox.add(charaNameLbls[charaCnt]);
			lblBox.add(Box.createHorizontalGlue());
			charaPanels[charaCnt].add(Box.createVerticalStrut(20));
			charaPanels[charaCnt].add(lblBox);
			charaPanels[charaCnt].add(new JLabel(" "));
			JLabel[] paramLbls = new JLabel[paramNames.length];
			JTextField[] paramFields = new JTextField[paramNames.length];
			String[] paramVals = readers[charaCnt].getParamVals(paramType);
			for (int paramCnt = 0; paramCnt < paramNames.length; paramCnt++) {
				paramLbls[paramCnt] = new JLabel(paramNames[paramCnt]);
				paramLbls[paramCnt].setAlignmentX(JLabel.CENTER_ALIGNMENT);
				paramLbls[paramCnt].setFont(paramLblFnt);
				paramLbls[paramCnt].setForeground(new Color(0x51, 0x84, 0x7D));
				paramLbls[paramCnt].setHorizontalAlignment(JLabel.CENTER);
				paramLbls[paramCnt].setMinimumSize(minLabelSize);
				paramLbls[paramCnt].setMaximumSize(minLabelSize);
				paramLbls[paramCnt].setPreferredSize(minLabelSize);
				paramLbls[paramCnt].setToolTipText(paramDescs[paramCnt]);
				paramFields[paramCnt] = new JTextField();
				paramFields[paramCnt].setBackground(Color.WHITE);
				paramFields[paramCnt].setEditable(false);
				paramFields[paramCnt].setFont(paramLblFnt);
				paramFields[paramCnt].setForeground(prmColor);
				paramFields[paramCnt].setHorizontalAlignment(JTextField.CENTER);
				paramFields[paramCnt].setMinimumSize(minFieldSize);
				paramFields[paramCnt].setMaximumSize(minFieldSize);
				paramFields[paramCnt].setPreferredSize(minFieldSize);
				paramFields[paramCnt].setText(paramVals[paramCnt]);
				Box paramBox = Box.createHorizontalBox();
				paramBox.add(Box.createHorizontalGlue());
				paramBox.add(paramLbls[paramCnt]);
				paramBox.add(new JLabel(" "));
				paramBox.add(paramFields[paramCnt]);
				paramBox.add(Box.createHorizontalGlue());
				charaPanels[charaCnt].add(paramBox);
			}
			charaPanels[charaCnt].add(Box.createVerticalStrut(20));
		}
		//Set component properties
		mainPanel.setBackground(bgColor);
		//Add components
		for (int charaCnt = 0; charaCnt < readers.length; charaCnt++) {
			Box horizontal = Box.createHorizontalBox();
			horizontal.add(Box.createHorizontalStrut(20));
			horizontal.add(charaPanels[charaCnt]);
			horizontal.add(Box.createHorizontalStrut(20));
			mainPanel.add(horizontal);
		}
		frame.add(scroll);
		//Set frame properties
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(img);
		frame.setMinimumSize(minFrameSize);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	public static void run() {
		int[] cfgVals = null;
		String paramType = "";
		Toolkit tk = Toolkit.getDefaultToolkit();
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			Image img = tk.getImage(ClassLoader.getSystemResource("img/icon.png"));
			String msg = "Select a parameter type:";
			File[] availableCfgs = new File("./res/cfg/").listFiles((dir, name) -> name.toLowerCase().endsWith(".cfg"));
			if (availableCfgs.length == 0) {
				error(tk);
				String err = "No configuration files were found!";
				JOptionPane.showMessageDialog(null, err, msg, JOptionPane.WARNING_MESSAGE);
				return;
			}
			String[] paramTypes = new String[availableCfgs.length];
			for (int cfgCnt = 0; cfgCnt < availableCfgs.length; cfgCnt++)
				paramTypes[cfgCnt] = availableCfgs[cfgCnt].getName().replace(".cfg", "");
			paramType = (String) JOptionPane.showInputDialog(null, msg, WINDOW_TITLE, JOptionPane.QUESTION_MESSAGE, new ImageIcon(img), paramTypes, null);
			if (paramType == null) return;
			cfgVals = Main.getParamCfgVals(paramType);
			if (cfgVals == null) {
				error(tk);
				String err = "Configuration file belonging to the parameter type is either empty or not present!";
				JOptionPane.showMessageDialog(null, err, msg, JOptionPane.WARNING_MESSAGE);
				return;
			}
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Select folder containing parameters...");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = fc.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
				File paramFolder = fc.getSelectedFile();
				if (!paramFolder.isDirectory()) {
					error(tk);
					JOptionPane.showMessageDialog(fc, "Provided directory is not a folder!", WINDOW_TITLE, JOptionPane.WARNING_MESSAGE);
					return;
				}
				File[] dats = paramFolder.listFiles((dir, name) -> name.endsWith("_common_param.dat"));
				if (dats.length == 0) {
					error(tk);
					JOptionPane.showMessageDialog(fc, "Folder contains no parameter files!", WINDOW_TITLE, JOptionPane.WARNING_MESSAGE);
					return;
				}
				ParamFileReader[] readers = new ParamFileReader[dats.length];
				for (int datCnt = 0; datCnt < dats.length; datCnt++)
					readers[datCnt] = new ParamFileReader(dats[datCnt], cfgVals[3] == 1);
				File[] csvFiles = CsvHandler.getAvailableCsvFiles();
				int searchResult = CsvHandler.getCsvSearchResult(csvFiles, paramType);
				if (searchResult < 0) {
					error(tk);
					JOptionPane.showMessageDialog(fc, "Required CSV not found!", WINDOW_TITLE, JOptionPane.WARNING_MESSAGE);
					return;
				}
				String[] lines = CsvHandler.getLines(csvFiles[searchResult]);
				String[] paramDescs = new String[lines.length], paramNames = new String[lines.length];
				for (int lineCnt = 0; lineCnt < lines.length; lineCnt++) {
					String[] lineArray = lines[lineCnt].split(",");
					paramNames[lineCnt] = lineArray[1];
					paramDescs[lineCnt] = lineArray[2];
				}
				init(readers, paramType, paramNames, paramDescs, img, tk);
			}
		}
		catch (Exception e) {
			error(tk);
			String err = e.getClass().getSimpleName() + ": " + e.getMessage() + "\n";
			StackTraceElement[] elements = e.getStackTrace();
			for (StackTraceElement ste: elements) {
				String line = ste.toString().replace("[", "").replace("]", "");
				if (line.startsWith("gui") || line.startsWith("cmd")) err += line + "\n";
			}
			JOptionPane.showMessageDialog(null, err, "Exception", JOptionPane.ERROR_MESSAGE);
		}
	}
}