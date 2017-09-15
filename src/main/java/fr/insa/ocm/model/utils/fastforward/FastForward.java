package fr.insa.ocm.model.utils.fastforward;


import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FastForward implements Runnable {

	// Used by the caller of FastForward to know when it is finished.
	private boolean finished;

	// Used by the caller to force the stop of the fastforward.
	private boolean stopRequested;

	// Defines the number of learning round the fastforward needs to do and the number of seconds to wait between each of them.
	private double numberRound;
	private double numberSecPerRound;

	// Used to describe the current progress of the fastforward.
	private String currentOperation;
	private String remainingTime;
	private Double progressLearning;
	private Double progressMining;

	// Provided by the caller, indicate which patterns to keep/trash.
	private List<Condition> listConditions;
	private Condition.ActionPatternChoice conditionPriority;

	// Intermediate results, the caller needs to get the lasts results for the next learning round.
	private List<Pattern> listKeptPatterns;
	private List<Pattern> listNeutralPatterns;
	private List<Pattern> listTrashedPatterns;

	/**
	 *
	 * @param numberRounds The number of learning round to launch.
	 * @param numberSecPerRound The number of seconds to wait between each learning round.
	 * @param listCondition The list of conditions to keep/trash the patterns.
	 * @param conditionPriority Choose what kind of condition is the most important.
	 * @param listKeptPatterns The list of kept patterns selected during a previous search.
	 * @param listNeutralPatterns The list of neutral patterns selected during a previous search.
	 * @param listTrashedPatterns The list of trashed patterns selected during a previous search.
	 */
	public FastForward(double numberRounds, double numberSecPerRound,
	                   @NotNull List<Condition> listCondition,
	                   @NotNull Condition.ActionPatternChoice conditionPriority,
	                   @NotNull List<Pattern> listKeptPatterns,
	                   @NotNull List<Pattern> listNeutralPatterns,
	                   @NotNull List<Pattern> listTrashedPatterns){
		this.numberRound = numberRounds;
		this.numberSecPerRound = numberSecPerRound;
		this.listConditions = listCondition;
		this.conditionPriority = conditionPriority;

		this.listKeptPatterns = listKeptPatterns;
		this.listNeutralPatterns = listNeutralPatterns;
		this.listTrashedPatterns = listTrashedPatterns;

		finished = false;

		stopRequested = false;

		progressLearning = 0d;
		progressMining = 0d;
		currentOperation = "";
		remainingTime = "";
	}

	@Override
	public void run() {

		final int nbIter = 100;
		try {
			for (int i = 0; i < numberRound && !stopRequested; ++i) {
				currentOperation = "Mining";
				for (int j = 0; j < nbIter && !stopRequested; ++j) {
					Thread.sleep((long)numberSecPerRound*1000/nbIter);
					progressMining = (j+1d)/nbIter;

					int remainingTimeSec = (int)((numberRound-i) * numberSecPerRound - (numberSecPerRound/nbIter) * j);
					remainingTime  = remainingTimeSec/60 + " min " + remainingTimeSec%60 + " sec";
				}

				// We process the data gathered by the mining algorithms.
				currentOperation = "Processing data";


				List<Pattern> bestPatterns = OCMManager.getNewRanking(listKeptPatterns, listNeutralPatterns, listTrashedPatterns);
				List<Pattern> rawBestPatterns = new ArrayList<>(bestPatterns);

				if(conditionPriority.equals(Condition.ActionPatternChoice.TRASH)){
					listTrashedPatterns = computeListPattern(bestPatterns, rawBestPatterns, listConditions, Condition.ActionPatternChoice.TRASH);
					bestPatterns.removeAll(listTrashedPatterns);
					listKeptPatterns = computeListPattern(bestPatterns, rawBestPatterns, listConditions, Condition.ActionPatternChoice.KEEP);
					bestPatterns.removeAll(listKeptPatterns);
					listNeutralPatterns = new ArrayList<>(bestPatterns);
				}else{
					listKeptPatterns = computeListPattern(bestPatterns, rawBestPatterns, listConditions, Condition.ActionPatternChoice.KEEP);
					bestPatterns.removeAll(listKeptPatterns);
					listTrashedPatterns = computeListPattern(bestPatterns, rawBestPatterns, listConditions, Condition.ActionPatternChoice.TRASH);
					bestPatterns.removeAll(listTrashedPatterns);
					listNeutralPatterns = new ArrayList<>(bestPatterns);
				}

				int nbPatternRank = rawBestPatterns.size();
				double nbQuality = listKeptPatterns.size() - listTrashedPatterns.size();
				nbQuality = ((nbQuality/nbPatternRank)+1)/2;

				System.err.println(listKeptPatterns.size() +"-"+ listNeutralPatterns.size()
									+ "-" + listTrashedPatterns.size() + " : " + nbQuality);

				DebugLogger.printQuality(i +";"+ nbQuality);

				progressLearning = ((i+1d)/numberRound);
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		currentOperation = "Finished";
		remainingTime = "Finished";

		finished = true;
	}

	//********** Internal Methods **********//

	private List<Pattern> computeListPattern(List<Pattern> listPattern,
	                                         List<Pattern> rawListPattern,
	                                         List<Condition> listCondition,
	                                         Condition.ActionPatternChoice actionOnPattern){
		List<Pattern> listResultPattern = new ArrayList<>();

		for(Pattern pattern : listPattern){
			//Check if one of the condition to trash is met by the pattern.
			for(Condition condition : listCondition){
				if(condition.getActionPatternChoice().equals(actionOnPattern) && condition.isMet(pattern, rawListPattern)){
					listResultPattern.add(pattern);
					break;
				}
			}
		}

		return listResultPattern;
	}

	//********** Getters/Setters Methods **********//

	// Getters //
	public boolean isFinished(){
		return finished;
	}

	public String getCurrentOperation() {
		return currentOperation;
	}

	public String getRemainingTime() {
		return remainingTime;
	}

	public Double getProgressLearning() {
		return progressLearning;
	}

	public Double getProgressMining() {
		return progressMining;
	}

	public List<Pattern> getListKeptPatterns() {
		return listKeptPatterns;
	}

	public List<Pattern> getListNeutralPatterns() {
		return listNeutralPatterns;
	}

	public List<Pattern> getListTrashedPatterns() {
		return listTrashedPatterns;
	}

	// Setters //
	public void setStopRequested() {
		this.stopRequested = true;
	}

}
