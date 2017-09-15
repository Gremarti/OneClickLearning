package fr.insa.ocm.model;

import fr.insa.ocm.model.wrapper.api.AbstractAlgorithmLauncher;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class DebugLogger {

	public enum MessageSeverity{
		INFO, MEDIUM, HIGH, CRITICAL;

		/**
		 * With A and B a message severity.
		 * A is higher or equals than B iff compareMatrix[A.getIndex][B.getIndex] is true;
		 */
		private final boolean[][] compareMatrix =
				{{true, false, false, false},
				{true, true, false, false},
				{true, true, true, false},
				{true, true, true, true}};

		@Override
		public String toString() {
			switch (this){
				case INFO:
					return "[Info]";
				case MEDIUM:
					return "[Medium]";
				case HIGH:
					return "[High]";
				case CRITICAL:
					return "[CRITICAL]";
				default:
					return "";
			}
		}

		public int getIndex(){
			switch (this){
				case INFO:
					return 0;
				case MEDIUM:
					return 1;
				case HIGH:
					return 2;
				case CRITICAL:
					return 3;
				default:
					return 0;
			}
		}

		public boolean isHigher(MessageSeverity messageSeverity){
			return compareMatrix[this.getIndex()][messageSeverity.getIndex()];
		}
	}

	// Informations used to print log message correctly
	private static final Semaphore semaphoreSERR = new Semaphore(1);
	private static final MessageSeverity threshold = MessageSeverity.INFO;
	private static long startLog;
	private static final DecimalFormat decimalFormat = new DecimalFormat("#0.000000");

	// Paths used to serialize the debug informations
	public static final String directoryLog = "./log/";
	public static final String directorySave = "./save/";
	private static final String banditLog = directoryLog +"bandit.csv";
	private static final String coactiveLog = directoryLog +"coactive.csv";
	private static final String pathLog = directoryLog +"debug.log";
	private static final String qualityLog = directoryLog +"quality.log";
	private static final String algorithmLog = directoryLog +"algorithm.log";
	private static final String selectionDistributionLog = directoryLog + "selectionDistribution.log";


	static {
		List<String> listDirectory = new ArrayList<>();
		listDirectory.add(directoryLog);
		listDirectory.add(directorySave);
		for(String directory : listDirectory) {
			try {
				Path directoryPath = Paths.get(directory);
				if (!directoryPath.toFile().exists()) {
					Files.createDirectory(directoryPath);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try(BufferedWriter writerLog = new BufferedWriter(new FileWriter(pathLog))){
			writerLog.write("OneClick Mining launched at: " + LocalDateTime.now());
			writerLog.newLine();
			writerLog.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try(BufferedWriter writerQuality = new BufferedWriter(new FileWriter(qualityLog))){
			writerQuality.write("id; quality");
			writerQuality.newLine();
			writerQuality.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		startLog = System.currentTimeMillis();
	}

	/**
	 * Prints in the standard error channel you debug message with the default priority INFO.
	 * @param msg The message you want to print.
	 */
	public static void printDebug(String msg){
		if (MessageSeverity.INFO.isHigher(threshold)){
			semaphoreSERR.acquireUninterruptibly();
			writeDebug("[Info]"+ msg);
			semaphoreSERR.release();
		}
	}

	public static void printDebug(String msg, MessageSeverity msgSev){
		if (msgSev.isHigher(threshold)){
			semaphoreSERR.acquireUninterruptibly();
			writeDebug(msgSev + msg);
			semaphoreSERR.release();
		}
	}

	public static void printQuality(String msg){
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(qualityLog, true))){
			writer.write(msg);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initializeBanditLog(){
		// Write the columns name at the beginning of the log file for the bandit.
		try(BufferedWriter writerBandit = new BufferedWriter(new FileWriter(banditLog))) {
			List<String> listAlgorithmNames = OCMManager.algorithmLauncherGetListAlgorithmName();

			if (OCMManager.banditGetWeights().length == listAlgorithmNames.size()) {
				StringBuilder strBandit = new StringBuilder("");

				strBandit = strBandit.append("Time;");
				for (int i = 0; i < listAlgorithmNames.size() - 1; ++i) {
					strBandit = strBandit.append(listAlgorithmNames.get(i)).append(';');
				}
				strBandit = strBandit.append(listAlgorithmNames.get(listAlgorithmNames.size() - 1)).append('\n');

				writerBandit.write(strBandit.toString());
				writerBandit.close();
			} else {
				DebugLogger.printDebug("DebugLogger: The number of arms in the bandit does not match with the number of algorithms in the Algorithm Launcher", MessageSeverity.MEDIUM);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void logBandit(){
		try(BufferedWriter writerBandit = new BufferedWriter(new FileWriter(banditLog, true))){
			double[] armsBandit = OCMManager.banditGetWeights();
			StringBuilder strBandit = new StringBuilder("");

			// Concatenate the values of the bandit within the string builder.
			strBandit = strBandit.append((System.currentTimeMillis() - startLog)/1000).append(';');
			for(int i = 0; i < armsBandit.length-1; ++i){
				strBandit.append(decimalFormat.format(armsBandit[i]));
				strBandit.append(';');
			}
			strBandit.append(decimalFormat.format(armsBandit[armsBandit.length-1]));
			strBandit.append('\n');

			writerBandit.write(strBandit.toString());
			writerBandit.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initializeCoactiveLog(){
		// Write the columns name at the beginning of the log file for the coactive learning.
		try(BufferedWriter writerCoactive = new BufferedWriter(new FileWriter(coactiveLog))){
			List<String> listAttributeNames = OCMManager.algorithmLauncherGetListAttributeName();
			List<String> listAlgorithmNames = OCMManager.algorithmLauncherGetListAlgorithmName();

			if(OCMManager.coactiveGetWeights().length == listAttributeNames.size() + listAlgorithmNames.size() + Pattern.MeasureType.values().length){
				StringBuilder strCoactive = new StringBuilder("");

				strCoactive.append("Time;");

				// Add the names of the interestingness measures
				for(Pattern.MeasureType measure : Pattern.MeasureType.values()){
					strCoactive.append(measure.toString());
					strCoactive.append(';');
				}

				// Add the names of the attributes
				for(int i = 0; i < listAttributeNames.size()-1; ++i){
					strCoactive.append(listAttributeNames.get(i));
					strCoactive.append(";");
				}
				strCoactive.append(listAttributeNames.get(listAttributeNames.size()-1));
				strCoactive.append(';');

				// Add the names of the algorithms since they have a weight associated.
				for(String algorithmName : listAlgorithmNames){
					if(listAlgorithmNames.indexOf(algorithmName) != listAlgorithmNames.size()-1){
						strCoactive.append(algorithmName);
						strCoactive.append(';');
					} else {
						strCoactive.append(algorithmName);
					}
				}

				writerCoactive.write(strCoactive.toString());
				writerCoactive.newLine();
				writerCoactive.close();
			} else {
				DebugLogger.printDebug("DebugLogger: The number of weights in the coactive learning does not match with the number of attributes in the Algorithm Launcher", MessageSeverity.MEDIUM);
			}

		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void logCoactive(){
		try(BufferedWriter writerCoactive = new BufferedWriter(new FileWriter(coactiveLog, true))){
			double[] weightsCoactive = OCMManager.coactiveGetWeights();
			StringBuilder strCoactive = new StringBuilder("");

			// Concatenate the values of the bandit within the string builder.
			strCoactive.append((System.currentTimeMillis() - startLog)/1000);
			strCoactive.append(';');
			for(int i = 0; i < weightsCoactive.length-1; ++i){
				strCoactive.append(decimalFormat.format(weightsCoactive[i]));
				strCoactive.append(';');
			}
			strCoactive.append(decimalFormat.format(weightsCoactive[weightsCoactive.length-1]));
			strCoactive.append('\n');

			writerCoactive.write(strCoactive.toString());
			writerCoactive.close();
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public static void initializeAlgorithmLog(){
		try(BufferedWriter writerAlgorithm = new BufferedWriter(new FileWriter(algorithmLog))){
			StringBuilder strAlgorithm = new StringBuilder("");

			strAlgorithm.append("Time;");
			for(String algorithmName : AbstractAlgorithmLauncher.getAllAvailableAlgorithm()){
				strAlgorithm.append(algorithmName);
				strAlgorithm.append(';');
			}

			strAlgorithm.deleteCharAt(strAlgorithm.length()-1);

			writerAlgorithm.write(strAlgorithm.toString());
			writerAlgorithm.newLine();

			writerAlgorithm.close();
		} catch (IOException e) {
			DebugLogger.printDebug("DebugLogger: Unable to startMonitoring the logging of the number of algorithm launched.", MessageSeverity.MEDIUM);
		}
	}

	public static void logAlgorithm(){
		try(BufferedWriter writerAlgorithm = new BufferedWriter(new FileWriter(algorithmLog, true))){
			StringBuilder strAlgorithm = new StringBuilder("");

			strAlgorithm.append((System.currentTimeMillis() - startLog)/1000);
			strAlgorithm.append(';');

			final Map<String, Integer> mapStatAlgorithmCall = AbstractAlgorithmLauncher.getStatsAlgorithmsLaunched();
			for(String algorithmName : AbstractAlgorithmLauncher.getAllAvailableAlgorithm()){
				strAlgorithm.append(mapStatAlgorithmCall.getOrDefault(algorithmName, 0));
				strAlgorithm.append(';');
			}

			strAlgorithm.deleteCharAt(strAlgorithm.length()-1);

			writerAlgorithm.write(strAlgorithm.toString());
			writerAlgorithm.newLine();

			writerAlgorithm.close();
		} catch (IOException e){
			DebugLogger.printDebug("DebugLogger: Unable to log the current algorithm call count.", MessageSeverity.MEDIUM);
		}
	}

	public static void initializeSelectionDistributionLog(){
		try(BufferedWriter writerSD = new BufferedWriter(new FileWriter(selectionDistributionLog))){
			List<String> listAlgorithmName = OCMManager.algorithmLauncherGetListAlgorithmName();
			StringBuilder strName = new StringBuilder("");

			strName.append("Time;");
			for(String name : listAlgorithmName){
				strName.append(name);
				strName.append(';');
			}

			strName.deleteCharAt(strName.length()-1);

			writerSD.write(strName.toString());
			writerSD.newLine();

			writerSD.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static long lastSD = 0;
	public static void logSelectionDistribution(double[] dbl){
		if((System.currentTimeMillis() - startLog) / 1000 > lastSD) {
			lastSD = (System.currentTimeMillis() - startLog) / 1000;
			try (BufferedWriter writerTemporary = new BufferedWriter(new FileWriter(selectionDistributionLog, true))) {
				StringBuilder strAlgorithm = new StringBuilder("");

				strAlgorithm.append((System.currentTimeMillis() - startLog) / 1000);
				strAlgorithm.append(';');

				for (Double d : dbl) {
					strAlgorithm.append(decimalFormat.format(d));
					strAlgorithm.append(';');
				}

				strAlgorithm.deleteCharAt(strAlgorithm.length() - 1);

				writerTemporary.write(strAlgorithm.toString());
				writerTemporary.newLine();

				writerTemporary.close();
			} catch (IOException e) {
				DebugLogger.printDebug("DebugLogger: Unable to log the logSelectionDistribution.", MessageSeverity.MEDIUM);
			}
		}
	}

	private static int lastB = 0;
	public static void logBeta(double d){
		if((System.currentTimeMillis() - startLog) / 1000 > lastB) {
			try (BufferedWriter writerTemporary = new BufferedWriter(new FileWriter(directoryLog + "beta.log", true))) {
				String strAlgorithm = ""
						+ (System.currentTimeMillis() - startLog) / 1000
						+ ';'
						+ decimalFormat.format(d);

				writerTemporary.write(strAlgorithm);
				writerTemporary.newLine();

				writerTemporary.close();
			} catch (IOException e) {
				DebugLogger.printDebug("DebugLogger: Unable to log the logSelectionDistribution.", MessageSeverity.MEDIUM);
			}
		}
	}

	//********** Internal Methods **********//

	private static void writeDebug(String msg){
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(pathLog, true))){
			writer.write("(");
			writer.write(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
			writer.write(") - ");
			writer.write(msg);
			writer.newLine();
			writer.close();
		} catch (IOException exception){
			exception.printStackTrace();
		}
	}

}
