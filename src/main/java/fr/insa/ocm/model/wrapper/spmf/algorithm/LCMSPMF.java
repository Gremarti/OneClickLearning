package fr.insa.ocm.model.wrapper.spmf.algorithm;

import ca.pfv.spmf.algorithms.frequentpatterns.lcm.AlgoLCM;
import ca.pfv.spmf.algorithms.frequentpatterns.lcm.Dataset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemsets;
import org.jetbrains.annotations.NotNull;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;
import fr.insa.ocm.model.wrapper.spmf.PatternSPMF;

import java.io.*;
import java.time.Duration;
import java.util.*;

public class LCMSPMF extends ItemsetMiningAlgorithm {

	// Name of this algorithm
	public static final String NAME = "LCM";

	// Extension of the files used by the algorithm
	private static final String LCM_DAT_EXT = ".lcmdat";
	private static volatile boolean isAlreadyConverted = false;

	// SPMF stuff
	private AlgoLCM algorithmLCM;
	private double minsup; // Required to launch AlgoLCM

	// From which AlgorithmLauncher comes this algorithm, so Patterns could be easily made.
	private AlgorithmLauncherSPMF algorithmLauncher;
	private static int numberItemSetLCM = 0;

	public LCMSPMF(double minsup, Duration discretizeCriteron, AlgorithmLauncherSPMF algorithmLauncher){
		super(LCM_DAT_EXT, discretizeCriteron);
		this.minsup = minsup;
		this.algorithmLauncher = algorithmLauncher;

		algorithmLCM = new AlgoLCM();
	}

	@Override
	public void loadData(String csvPath) {
		if(!isAlreadyConverted) {
			isAlreadyConverted = true;
			super.loadData(csvPath);

			numberItemSetLCM = this.numberItemset;
		} else {
			// We need to get the already converted file.
			String fileName = csvPath.substring(0, csvPath.lastIndexOf("."));
			File convertedFile = new File(fileName + LCMSPMF.LCM_DAT_EXT);

			pathDataConverted = convertedFile.getAbsolutePath();
		}
	}

	@NotNull
	@Override
	public List<PatternSPMF> call() {
		List<PatternSPMF> resultPattern = new ArrayList<>();
		try {
			Dataset dataset = new Dataset(pathDataConverted);
			Itemsets itemsetsResult = algorithmLCM.runAlgorithm(minsup, dataset, null);

			resultPattern.addAll(computeResult(itemsetsResult));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultPattern;
	}

	/**
	 * Create all the Patterns to describe the results given by the algorithm. The resulting pattern are ready to use for OCM.
	 * @param itemsets The itemsets which needs to be converted to Patterns.
	 * @return Create a list of {@link PatternSPMF} to describe the result of the LCM algorithm.
	 */
	@NotNull
	private List<PatternSPMF> computeResult(@NotNull Itemsets itemsets){
		List<PatternSPMF> resultPattern = new ArrayList<>();

		itemsets.getLevels().forEach(levelItemSet -> levelItemSet.forEach(itemset -> {
			resultPattern.add(computePattern(itemset));
		}));

		return resultPattern;
	}

	/**
	 * Create a new pattern corresponding to the current itemset, for a OCM use.
	 * @param itemset The itemset needed to be converted into a Pattern.
	 * @return A new {@link PatternSPMF} which describe the sent itemset.
	 */
	@NotNull
	private PatternSPMF computePattern(@NotNull Itemset itemset){
		int[] items = itemset.getItems();
		double[] interestingMeasures = new double[Pattern.MeasureType.values().length];
		StringBuilder patternDescriptorBuilder = new StringBuilder("[");
		List<String> listAttributeName = new ArrayList<>();

		for (int i = 0; i < interestingMeasures.length; i++) {
			interestingMeasures[i] = 0d;
		}

		for(int id : items){
			String label = algorithmLauncher.getLabel(id);
			listAttributeName.add(label);

			if(id != items[0]){
				patternDescriptorBuilder = patternDescriptorBuilder.append(", ").append(label);
			} else {
				patternDescriptorBuilder = patternDescriptorBuilder.append(label);
			}
		}
		patternDescriptorBuilder = patternDescriptorBuilder.append("]");

		interestingMeasures[Pattern.MeasureType.FREQUENCY.getIndex()] = ((double) itemset.getAbsoluteSupport())/ numberItemSetLCM;
		interestingMeasures[Pattern.MeasureType.RELATIVE_SHORTNESS.getIndex()] = ((double) items.length)/algorithmLauncher.getNbAttributes();

		return new PatternSPMF(patternDescriptorBuilder.toString(),
				listAttributeName,
				algorithmLauncher,
				interestingMeasures,
				LCMSPMF.NAME);
	}
}
