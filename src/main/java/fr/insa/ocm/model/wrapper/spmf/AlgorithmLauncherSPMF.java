package fr.insa.ocm.model.wrapper.spmf;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.wrapper.api.AbstractAlgorithmLauncher;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.spmf.algorithm.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.*;


public class AlgorithmLauncherSPMF extends AbstractAlgorithmLauncher {

	// Settings to tune the algorithms
	private static volatile double minsupLCM = 0.6;
	private static volatile double minsupBIDEPlus = 0.5;
	private static volatile double minsupVMSP = 0.45;
	private static volatile int maxgapVMSP = 5;
	private static volatile double minsupPFPM = 0.5;
	private static volatile int minPeriodicityPFPM = 1;
	private static volatile int maxPeriodicityPFPM = 14;
	private static volatile boolean[] measuresPatternDescriptor = new boolean[Pattern.MeasureType.values().length];

	// The list of all available algorithms to use for OCM.
	private static List<String> listAvailableMiningAlgorithm;

	// The map which keep the link between real attribute name and its number designation.
	private Map<Integer, String> mapIdLabel;

	// The path to the current data file
	private String pathConvertedData;


	static{
		listAvailableMiningAlgorithm = new ArrayList<>();
		listAvailableMiningAlgorithm.add(LCMSPMF.NAME);
		listAvailableMiningAlgorithm.add(BIDEPlusSPMF.NAME);
		listAvailableMiningAlgorithm.add(VMSPSPMF.NAME);
		listAvailableMiningAlgorithm.add(PFPMSPMF.NAME);

		listAvailableMiningAlgorithm.forEach(s -> mapNumberCallsPerAlgorithm.put(s, 0));

		for(int i = 0; i < measuresPatternDescriptor.length; ++i){
			// False for SubGroup and Target Deviation, true else.
//			measuresPatternDescriptor[i] = !(i == Pattern.MeasureType.SUBGROUP_INTERSTINGNESS.getIndex()
//					|| i == Pattern.MeasureType.TARGET_DEVIATION.getIndex());
			measuresPatternDescriptor[i] = true;
		}
	}

	public AlgorithmLauncherSPMF(String pathRawData){
		super();

		listAttributeName = new ArrayList<>();
		mapIdLabel = new HashMap<>();

		// Directly load the data once the AlgorithmLauncher is created.
		importData(pathRawData);
	}

	@Override
	public int getNbAlgorithms() {
		return listAvailableMiningAlgorithm.size();
	}

	@Override
	public List<Pattern> startAlgorithm(int algorithmNumber) {
		List<Pattern> listResult = new ArrayList<>();

		if(algorithmNumber < 0 || algorithmNumber >= listAvailableMiningAlgorithm.size()){
			throw new UnsupportedOperationException("ERR: The algorithm id ask is not a valid one : "+ algorithmNumber + ".");
		}

		MiningAlgorithmSPMF algorithm = getAlgorithm(listAvailableMiningAlgorithm.get(algorithmNumber));

		if(algorithm == null) {
			DebugLogger.printDebug("AlgorithmLauncherSPMF: Unable to retrieve a valid algorithm instance.", DebugLogger.MessageSeverity.HIGH);
			return new ArrayList<>();
		}

		algorithm.loadData(pathConvertedData);

		// Calling the algorithm
//		System.err.println(Thread.currentThread().getName() + ": is launching " + lastMiningAlgorithm);
		listResult.addAll(algorithm.call());

		return listResult;
	}

	@Override
	public void stopAlgorithm() {

	}

	@Override
	public List<String> getListAlgorithmName() {
		return new ArrayList<>(listAvailableMiningAlgorithm);
	}

	//********** Public methods **********//

	public String getLabel(Integer id){
		return mapIdLabel.getOrDefault(id, "?");
	}

	//********** Internal methods **********//

	private MiningAlgorithmSPMF getAlgorithm(String name){
		MiningAlgorithmSPMF miningAlgorithm = null;

		if(listAvailableMiningAlgorithm.contains(name)){
			int numberCalls = mapNumberCallsPerAlgorithm.getOrDefault(name, 0);
			mapNumberCallsPerAlgorithm.put(name, numberCalls+1);
		}

		switch (name){
			case LCMSPMF.NAME:

				miningAlgorithm = new LCMSPMF(minsupLCM, Duration.ZERO.plusDays(7), this);
				break;

			case BIDEPlusSPMF.NAME:

				miningAlgorithm = new BIDEPlusSPMF(minsupBIDEPlus, Duration.ZERO.plusDays(1), Duration.ZERO.plusDays(7), this);
				break;

			case VMSPSPMF.NAME:

				miningAlgorithm = new VMSPSPMF(minsupVMSP, maxgapVMSP, Duration.ZERO.plusDays(1), Duration.ZERO.plusDays(7), this);
				break;

			case PFPMSPMF.NAME:

				miningAlgorithm = new PFPMSPMF(Duration.ZERO.plusDays(7), minsupPFPM, minPeriodicityPFPM, maxPeriodicityPFPM, minPeriodicityPFPM, maxPeriodicityPFPM, this);
				break;

		}

		return miningAlgorithm;
	}

	/**
	 * Loads the data provided to the AlgorithmLauncher when it is created.
	 * @param csv The path to the csv file where is the data.
	 */
	private void importData(String csv) {
		File fileData = new File(csv);

		if(fileData.exists() && fileData.isFile()){
			String pathRawData = fileData.getAbsolutePath();

			CSVLoaderSPMF csvLoader = new CSVLoaderSPMF(pathRawData);

			listAttributeName = csvLoader.getListItem();
			mapIdLabel = csvLoader.getMapIdLabel();
			pathConvertedData = csvLoader.getPathConvertedData();
		}else{
			System.err.println("ERR: The given path is incorrect, no file found there : " + csv);
		}
	}

	private void exportMapIdLabel(){
		try(BufferedWriter writer = new BufferedWriter(new FileWriter("mapIdLabel.txt"))){
			mapIdLabel.forEach((integer, str) -> {
				try {
					writer.write(integer + ";" + str);
					writer.newLine();
				} catch (IOException e){
					e.printStackTrace();
				}
			});

			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//********** Getters/Setters method **********//

	// Getters //

	public static double getMinsupLCM(){ return AlgorithmLauncherSPMF.minsupLCM; }

	public static double getMinsupBIDEPlus(){ return AlgorithmLauncherSPMF.minsupBIDEPlus; }

	public static double getMinsupVMSP(){ return AlgorithmLauncherSPMF.minsupVMSP; }

	public static double getMaxgapVMSP(){ return AlgorithmLauncherSPMF.maxgapVMSP; }

	public static double getMinsupPFPM(){ return AlgorithmLauncherSPMF.minsupPFPM; }

	public static int getMinPeriodicityPFPM(){ return AlgorithmLauncherSPMF.minPeriodicityPFPM; }

	public static int getMaxPeriodicityPFPM(){ return AlgorithmLauncherSPMF.maxPeriodicityPFPM; }

	public static boolean getMeasurePatternDescriptor(int index){
		return index >= 0 && index < measuresPatternDescriptor.length && measuresPatternDescriptor[index];
	}

	public static List<String> getListAvailableMiningAlgorithm(){
		return new ArrayList<>(listAvailableMiningAlgorithm);
	}

	// Setters //

	public static void setMinsupLCM(double minsupLCM) {
		AlgorithmLauncherSPMF.minsupLCM = minsupLCM;
	}

	public static void setMinsupBIDEPlus(double minsupBIDEPlus) {
		AlgorithmLauncherSPMF.minsupBIDEPlus = minsupBIDEPlus;
	}

	public static void setMinsupVMSP(double minsupVMSP) {
		AlgorithmLauncherSPMF.minsupVMSP = minsupVMSP;
	}

	public static void setMaxgapVMSP(int maxgapVMSP) {
		AlgorithmLauncherSPMF.maxgapVMSP = maxgapVMSP;
	}

	public static void setMinsupPFPM(double minsupPFPM){
		AlgorithmLauncherSPMF.minsupPFPM = minsupPFPM;
	}

	public static void setMinPeriodicityPFPM(int minPeriodicityPFPM) {
		AlgorithmLauncherSPMF.minPeriodicityPFPM = minPeriodicityPFPM;
	}

	public static void setMaxPeriodicityPFPM(int maxPeriodicityPFPM) {
		AlgorithmLauncherSPMF.maxPeriodicityPFPM = maxPeriodicityPFPM;
	}

	public static void setMeasuresPatternDescriptor(boolean b, int index){
		if(index >= 0 && index < measuresPatternDescriptor.length){
			measuresPatternDescriptor[index] = b;
		}
	}

	//********** Main method **********//

	/**
	 * For quick testing purpose only
	 * @param args Main parameters
	 */
	public static void main(String[] args) {
		AlgorithmLauncherSPMF algorithmLauncherSPMF = new AlgorithmLauncherSPMF("ressource/sacha_chua_more_preprocessed.csv");

		List<Pattern> listResult = algorithmLauncherSPMF.startAlgorithm(3);
		algorithmLauncherSPMF.exportMapIdLabel();

		listResult.forEach(System.err::println);
		System.out.println("listResult.size() = " + listResult.size());



	}
}
