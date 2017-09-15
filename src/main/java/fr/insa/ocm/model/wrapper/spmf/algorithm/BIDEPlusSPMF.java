package fr.insa.ocm.model.wrapper.spmf.algorithm;

import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.AlgoBIDEPlus;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.SequentialPattern;
import ca.pfv.spmf.algorithms.sequentialpatterns.prefixspan.SequentialPatterns;
import ca.pfv.spmf.patterns.itemset_list_integers_without_support.Itemset;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;
import fr.insa.ocm.model.wrapper.spmf.PatternSPMF;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BIDEPlusSPMF extends SequentialMiningAlgorithmSPMF {

	// Name of this algorithm
	public static final String NAME = "BIDEPlus";

	// Extension of the files used by the algorithm
	private static final String BIDEP_DAT_EXT = ".bidepdat";
	private static volatile boolean isAlreadyConverted = false;

	// SPMF stuff
	private AlgoBIDEPlus algorithmBIDEPlus;
	private double minsup; // Required to launch BIDEPlus

	// From which AlgorithmLauncher comes this algorithm, so Patterns could be easily made.
	private AlgorithmLauncherSPMF algorithmLauncher;
	private static int numberSequenceBIDEPlus = 0;


	public BIDEPlusSPMF(double minsup, Duration intervalItemset, Duration intervalSequence, AlgorithmLauncherSPMF algorithmLauncher){
		super(BIDEP_DAT_EXT, intervalItemset, intervalSequence);

		this.minsup = minsup;
		this.algorithmLauncher = algorithmLauncher;

		algorithmBIDEPlus = new AlgoBIDEPlus();
	}

	@Override
	public void loadData(String csvPath) {
		if(!isAlreadyConverted) {
			isAlreadyConverted = true;
			super.loadData(csvPath);
			numberSequenceBIDEPlus = super.numberSequence;
		} else {
			// We need to get the already converted file.
			String fileName = csvPath.substring(0, csvPath.lastIndexOf("."));
			File convertedFile = new File(fileName + BIDEPlusSPMF.BIDEP_DAT_EXT);

			pathDataConverted = convertedFile.getAbsolutePath();
		}
	}

	@Override
	public List<PatternSPMF> call() {
		List<PatternSPMF> resultPattern = new ArrayList<>();
		try {
			SequentialPatterns sequentialPatterns = algorithmBIDEPlus.runAlgorithm(pathDataConverted, minsup, null);

			//sequentialPatterns.printFrequentPatterns(algorithmBIDEPlus.patternCount, false);
			//algorithmBIDEPlus.printStatistics();

			resultPattern.addAll(computeResult(sequentialPatterns));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultPattern;
	}

	@NotNull
	private List<PatternSPMF> computeResult(@NotNull SequentialPatterns sequentialPatterns){
		List<PatternSPMF> listPatternResult = new ArrayList<>();

		sequentialPatterns.getLevels().forEach(sequentialPatternsSet -> sequentialPatternsSet.forEach(sequentialPattern -> {
			listPatternResult.add(computePattern(sequentialPattern));
		}));

		return listPatternResult;
	}

	@NotNull
	private PatternSPMF computePattern(@NotNull SequentialPattern sequentialPattern){
		double[] interestingMeasures = new double[Pattern.MeasureType.values().length];
		StringBuilder patternDescriptorBuilder = new StringBuilder("[");
		Set<String> setAttributeName = new HashSet<>();


		for (Itemset itemset : sequentialPattern.getItemsets()) {

			if(sequentialPattern.get(0).equals(itemset)) {
				patternDescriptorBuilder = patternDescriptorBuilder.append("(");
			} else {
				patternDescriptorBuilder = patternDescriptorBuilder.append("\n\t(");
			}

			for(Integer id : itemset.getItems()){
				String label = algorithmLauncher.getLabel(id);
				setAttributeName.add(label);

				if(itemset.get(0).equals(id)){
					patternDescriptorBuilder = patternDescriptorBuilder.append(label);
				} else {
					patternDescriptorBuilder = patternDescriptorBuilder.append(", ").append(label);
				}
			}

			patternDescriptorBuilder = patternDescriptorBuilder.append(")");
		}
		patternDescriptorBuilder = patternDescriptorBuilder.append("]");

		interestingMeasures[Pattern.MeasureType.FREQUENCY.getIndex()] = ((double) sequentialPattern.getAbsoluteSupport())/numberSequenceBIDEPlus;
		interestingMeasures[Pattern.MeasureType.RELATIVE_SHORTNESS.getIndex()] = ((double) setAttributeName.size())/algorithmLauncher.getNbAttributes();

		return new PatternSPMF(patternDescriptorBuilder.toString(),
				new ArrayList<>(setAttributeName),
				algorithmLauncher,
				interestingMeasures,
				BIDEPlusSPMF.NAME);
	}
}
