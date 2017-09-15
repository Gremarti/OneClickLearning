package fr.insa.ocm.model.wrapper.spmf.algorithm;

import ca.pfv.spmf.algorithms.sequentialpatterns.spam.AlgoVMSP;
import ca.pfv.spmf.algorithms.sequentialpatterns.spam.PatternVMSP;
import ca.pfv.spmf.patterns.itemset_list_integers_without_support.Itemset;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;
import fr.insa.ocm.model.wrapper.spmf.PatternSPMF;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class VMSPSPMF extends SequentialMiningAlgorithmSPMF {

	// Name of this algorithm
	public static final String NAME = "VMSP";

	// Extension of the files used by the algorithm
	private static final String VMSP_DAT_EXT = ".vmspdat";
	private static volatile boolean isAlreadyConverted = false;

	// SPMF stuff
	private AlgoVMSP algorithmVMSP;
	private double minsup; // Required to launch VMSP

	// From which AlgorithmLauncher comes this algorithm, so Patterns could be easily made.
	private AlgorithmLauncherSPMF algorithmLauncher;
	private static int numberSequenceVMSP = 0;

	public VMSPSPMF(double minsup, int maxGap,
	                Duration intervalItemset,
	                Duration intervalSequence,
	                AlgorithmLauncherSPMF algorithmLauncher) {
		super(VMSP_DAT_EXT, intervalItemset, intervalSequence);

		this.minsup = minsup;
		this.algorithmLauncher = algorithmLauncher;

		algorithmVMSP = new AlgoVMSP();
		algorithmVMSP.setMaxGap(maxGap);
	}

	@Override
	public void loadData(String csvPath) {
		if(!isAlreadyConverted) {
			isAlreadyConverted = true;
			super.loadData(csvPath);

			numberSequenceVMSP = super.numberSequence;
		} else {
			// We need to get the already converted file.
			String fileName = csvPath.substring(0, csvPath.lastIndexOf("."));
			File convertedFile = new File(fileName + VMSPSPMF.VMSP_DAT_EXT);

			pathDataConverted = convertedFile.getAbsolutePath();
		}
	}

	@NotNull
	@Override
	public List<PatternSPMF> call() {
		List<PatternSPMF> resultPattern = new ArrayList<>();

		try {
			List<TreeSet<PatternVMSP>> maxPatterns = algorithmVMSP.runAlgorithm(pathDataConverted, DebugLogger.directoryLog +"junk.txt", minsup);

			resultPattern.addAll(computeResult(maxPatterns));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultPattern;
	}

	@NotNull
	private List<PatternSPMF> computeResult(@NotNull List<TreeSet<PatternVMSP>> listSetPatternVMSP){
		List<PatternSPMF> listPatternResult = new ArrayList<>();

		listSetPatternVMSP.forEach(setPatternVMSP -> {
			if(setPatternVMSP != null) {
				setPatternVMSP.forEach(patternVMSP -> listPatternResult.add(computePattern(patternVMSP)));
			}
		});

		return listPatternResult;
	}


	@NotNull
	private PatternSPMF computePattern(@NotNull PatternVMSP patternVMSP) {
		double[] interestingMeasures = new double[Pattern.MeasureType.values().length];
		StringBuilder patternDescriptorBuilder = new StringBuilder("[");
		Set<String> setAttributeName = new HashSet<>();

		for (Itemset itemset : patternVMSP.getPrefix().getItemsets()) {

			if (patternVMSP.getPrefix().getItemsets().get(0).equals(itemset)) {
				patternDescriptorBuilder = patternDescriptorBuilder.append(" ... (");
			} else {
				patternDescriptorBuilder = patternDescriptorBuilder.append("\n\t... (");
			}

			for (Integer id : itemset.getItems()) {
				String label = algorithmLauncher.getLabel(id);
				setAttributeName.add(label);

				if (itemset.get(0).equals(id)) {
					patternDescriptorBuilder.append(" ... , ");
					patternDescriptorBuilder.append(label);
					patternDescriptorBuilder.append("");
				} else {
					patternDescriptorBuilder.append(", ... , ");
					patternDescriptorBuilder.append(label);
					patternDescriptorBuilder.append("");
				}
			}

			patternDescriptorBuilder = patternDescriptorBuilder.append(", ... ) ...");
		}
		patternDescriptorBuilder = patternDescriptorBuilder.append("]");

		interestingMeasures[Pattern.MeasureType.FREQUENCY.getIndex()] = ((double) patternVMSP.getSupport()) / numberSequenceVMSP;
		interestingMeasures[Pattern.MeasureType.RELATIVE_SHORTNESS.getIndex()] = ((double) setAttributeName.size()) / algorithmLauncher.getNbAttributes();

		return new PatternSPMF(patternDescriptorBuilder.toString(),
				new ArrayList<>(setAttributeName),
				algorithmLauncher,
				interestingMeasures,
				VMSPSPMF.NAME);
	}
}
