package fr.insa.ocm.model.wrapper.spmf.algorithm;

import ca.pfv.spmf.algorithms.frequentpatterns.pfpm.AlgoPFPM;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;
import fr.insa.ocm.model.wrapper.spmf.PatternSPMF;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PFPMSPMF extends ItemsetMiningAlgorithm {

	// Name of this algorithm
	public static final String NAME = "PFPM";

	// Extension of the files used by the algorithm
	private static final String PFPM_DAT_EXT = ".pfpmdat";
	private static volatile boolean isAlreadyConverted = false;

	// SPMF stuff
	private AlgoPFPM algorithmPFPM;
	private double minsup;
	private int minPeriodicity;
	private int maxPeriodicity;
	private int minAveragePeriodicity;
	private int maxAveragePeriodicity;

	// From which AlgorithmLauncher comes this algorithm, so Patterns could be easily made.
	private AlgorithmLauncherSPMF algorithmLauncher;
	private static int numberItemSetPFPM = 0;

	public PFPMSPMF(Duration intervalItemset,
	         double minsup,
	         int minPeriodicity, int maxPeriodicity,
	         int minAveragePeriodicity, int maxAveragePeriodicity,
	         AlgorithmLauncherSPMF algorithmLauncher) {
		super(PFPMSPMF.PFPM_DAT_EXT, intervalItemset);

		this.minsup = minsup;
		this.minPeriodicity = minPeriodicity;
		this.maxPeriodicity = maxPeriodicity;
		this.minAveragePeriodicity = minAveragePeriodicity;
		this.maxAveragePeriodicity = maxAveragePeriodicity;

		this.algorithmLauncher = algorithmLauncher;

		algorithmPFPM = new AlgoPFPM(true);
		algorithmPFPM.setEnableESCP(true);
	}


	@Override
	public void loadData(String csvPath) {
		if(!isAlreadyConverted) {
			isAlreadyConverted = true;
			super.loadData(csvPath);

			numberItemSetPFPM = this.numberItemset;
		} else {
			// We need to get the already converted file.
			String fileName = csvPath.substring(0, csvPath.lastIndexOf("."));
			File convertedFile = new File(fileName + PFPMSPMF.PFPM_DAT_EXT);

			pathDataConverted = convertedFile.getAbsolutePath();
		}
	}

	@NotNull
	@Override
	public List<PatternSPMF> call() {
		List<PatternSPMF> resultPattern = new ArrayList<>();

		try {
			algorithmPFPM.runAlgorithm(pathDataConverted, DebugLogger.directoryLog +"junk.txt",
					minPeriodicity, maxPeriodicity, minAveragePeriodicity,
					maxAveragePeriodicity);

			//System.out.println(algorithmPFPM.getResultAsString());
			resultPattern.addAll(computeResult(algorithmPFPM.getResultAsString()));
			//System.out.println("numberItemSetPFPM = " + numberItemSetPFPM);
		} catch (IOException e){
			e.printStackTrace();
		}

		return resultPattern;
	}

	@NotNull
	private List<PatternSPMF> computeResult(@NotNull String results){
		List<PatternSPMF> resultPattern = new ArrayList<>();

		List<String> listResult = Arrays.asList(results.split("\n"));

		listResult.forEach(strPattern -> {
			if(strPattern != null){
				PatternSPMF pattern = computePattern(strPattern);
				if(pattern != null) {
					resultPattern.add(pattern);
				}
			}
		});

		return resultPattern;
	}

	@Nullable
	private PatternSPMF computePattern(@NotNull String strPattern){
		String[] patternParts = strPattern.split(";");
		double[] interestingMeasures = new double[Pattern.MeasureType.values().length];
		StringBuilder patternDescriptorBuilder = new StringBuilder("[");
		List<String> listAttributeName = new ArrayList<>();

		// The pattern sent by the PFPM algorithm contains 5 items
		if(patternParts.length != 5){
			System.err.println("ERR: Impossible to retrieve information from this pattern: " + strPattern);
			return null;
		}

		for (int i = 0; i < interestingMeasures.length; i++) {
			interestingMeasures[i] = 0d;
		}

		int indexAttribute = 0;
		for(String strId : patternParts[0].split(" ")){
			Integer id = Integer.valueOf(strId);
			String label = algorithmLauncher.getLabel(id);
			listAttributeName.add(label);

			if(indexAttribute != 0){
				patternDescriptorBuilder = patternDescriptorBuilder.append(", ").append(label);
			} else {
				patternDescriptorBuilder = patternDescriptorBuilder.append(label);
			}

			indexAttribute++;
		}
		patternDescriptorBuilder = patternDescriptorBuilder.append("]");

		Integer absoluteSupport = Integer.valueOf(patternParts[1]);
		Integer minPeriod = Integer.valueOf(patternParts[2]);
		Integer maxPeriod = Integer.valueOf(patternParts[3]);

		interestingMeasures[Pattern.MeasureType.FREQUENCY.getIndex()] = ((double) absoluteSupport)/ numberItemSetPFPM;
		interestingMeasures[Pattern.MeasureType.RELATIVE_SHORTNESS.getIndex()] = ((double) indexAttribute)/algorithmLauncher.getNbAttributes();
		interestingMeasures[Pattern.MeasureType.RELATIVE_PERIODICITY.getIndex()] = minPeriod.doubleValue()/ maxPeriod.doubleValue();

		if(interestingMeasures[Pattern.MeasureType.FREQUENCY.getIndex()] < minsup){
			// The created pattern should have a frequency at least higher then the minsup.
			return null;
		}

		return new PatternSPMF(patternDescriptorBuilder.toString(),
				listAttributeName,
				algorithmLauncher,
				interestingMeasures,
				PFPMSPMF.NAME);
	}
}
