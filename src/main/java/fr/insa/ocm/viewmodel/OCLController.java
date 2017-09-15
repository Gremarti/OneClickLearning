package fr.insa.ocm.viewmodel;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.cache.api.Cache;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.CoactiveLearning;
import fr.insa.ocm.model.utils.fastforward.FastForward;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.view.misc.FastForwardWaitingView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OCLController {

	public static final String VERSION = "0.5.0.1";

	private static OCLController INSTANCE = new OCLController();

	private volatile boolean stopRequested = false;

	private List<Pattern> interestingPattern;
	private List<Pattern> neutralPattern;
	private List<Pattern> trashedPattern;

	private String pathDataFile = null;

	// Keeps a track of the last fastforward done.
	private FastForward fastForward;

	// Settings of the controller
	private Pattern.WrapperType wrapperTypeSelected;
	private CoactiveLearning.CoactiveType coactiveLearningSelected;
	private Cache.CacheType cacheTypeSelected;

	//This constructor is valid only if used by the Application launcher
	private OCLController(){
		INSTANCE = this;

		interestingPattern = new ArrayList<>();
		neutralPattern = new ArrayList<>();
		trashedPattern = new ArrayList<>();

		// Initializing default settings
		wrapperTypeSelected = Pattern.WrapperType.SPMF;
		coactiveLearningSelected = CoactiveLearning.CoactiveType.SET;
		cacheTypeSelected = Cache.CacheType.SET;
	}

	//********** Initializing Methods **********//

	public static void initialize(String pathDataFile){
		DebugLogger.printDebug("OCLController: Initializing.");
		OCMManager.initialize(INSTANCE.wrapperTypeSelected, pathDataFile);
		OCMManager.algorithmManagerStartMining();

		INSTANCE.pathDataFile = pathDataFile;

		InfoAlgorithm.setPaused(false);
		Monitor.startMonitoring();

		INSTANCE.waitForOCMResults();

		DebugLogger.printDebug("OCLController: initialized correctly.");
	}

	public static void reload(String pathDataFile){
		DebugLogger.printDebug("OCLController: Reloading OCLController.");
		Monitor.pauseMonitoring();
		OCMManager.requestStop();

		INSTANCE.stopRequested = false;

		INSTANCE.interestingPattern = new ArrayList<>();
		INSTANCE.neutralPattern = new ArrayList<>();
		INSTANCE.trashedPattern = new ArrayList<>();

		INSTANCE.pathDataFile = pathDataFile;

		DebugLogger.printDebug("OCLController: Reloading OCMManager.");
		OCMManager.reload(INSTANCE.wrapperTypeSelected,	INSTANCE.cacheTypeSelected, INSTANCE.coactiveLearningSelected, pathDataFile);
		OCMManager.algorithmManagerStartMining();

		Monitor.resumeMonitoring();

		INSTANCE.waitForOCMResults();

		DebugLogger.printDebug("OCLController: Reloaded correctly.");
	}

	//********** Public Methods **********//

	public static void exportInterestingPatterns(String pathFile){
		List<Pattern> patterns = OCMManager.patternWarehouseGetPatterns();

		BufferedWriter writerTXT = null;
		try {
			writerTXT = new BufferedWriter(new FileWriter(new File(pathFile)));
			writerTXT.write("Interesting patterns:");
			writerTXT.newLine();
			writerTXT.write("---------------------");
			writerTXT.newLine();

			for(Pattern p : patterns){
				String toWrite = p.toString();
				writerTXT.write(toWrite);
				writerTXT.newLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(writerTXT != null){
				try {
					writerTXT.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//********** Fast Forward Methods **********//

	public static void fastForward(int numberRounds, double numberSecPerRound, List<Condition> listConditions,
	                        FastForwardWaitingView fastForwardWaitingView,
	                        Condition.ActionPatternChoice conditionPriority){

		FastForward fastForward = new FastForward(numberRounds, numberSecPerRound,
													listConditions,
													conditionPriority,
													INSTANCE.interestingPattern,
													INSTANCE.neutralPattern,
													INSTANCE.trashedPattern);

		INSTANCE.fastForward = fastForward;

		final StringProperty currentOperation = new SimpleStringProperty("");
		final StringProperty remainingTime = new SimpleStringProperty("");
		final DoubleProperty progressLearning = new SimpleDoubleProperty(0d);
		final DoubleProperty progressMining = new SimpleDoubleProperty(0d);

		Platform.runLater(() -> {
			fastForwardWaitingView.bindCurrentOperation(currentOperation);
			fastForwardWaitingView.bindCurrentTime(remainingTime);
			fastForwardWaitingView.bindProgressLearning(progressLearning);
			fastForwardWaitingView.bindProgressMining(progressMining);
		});

		Thread threadFF = new Thread(fastForward);
		threadFF.setName("FastForward");
		threadFF.setDaemon(true);
		threadFF.start();

		try {
			while (!fastForward.isFinished()) {
				String strCurrentOperation = fastForward.getCurrentOperation();
				String strRemainingTime = fastForward.getRemainingTime();
				Double dProgressLearning = fastForward.getProgressLearning();
				Double dProgressMining = fastForward.getProgressMining();

				Platform.runLater(() -> {
					currentOperation.setValue("Current Operation: "+ strCurrentOperation + ".");
					remainingTime.setValue("Estimated time before completion: "+ strRemainingTime);
					progressLearning.setValue(dProgressLearning);
					progressMining.setValue(dProgressMining);
				});
				Thread.sleep(100);
			}
		} catch (InterruptedException e){
			e.printStackTrace();
		}

//		final int nbIter = 100;
//		try {
//			for (int i = 0; i < nbRound && !INSTANCE.stopRequested && !INSTANCE.ffStopRequested; ++i) {
//				fastForwardWaitingView.setCurrentOperation("Mining");
//				for (int j = 0; j < nbIter && !INSTANCE.stopRequested && !INSTANCE.ffStopRequested; ++j) {
//					Thread.sleep((long)secPerRound*1000/nbIter);
//					double pbm = (j+1.)/nbIter;
//					fastForwardWaitingView.setProgressBarMining(pbm);
//
//					int remainingTimeSec = (int)((nbRound-i) * secPerRound - (secPerRound/nbIter) * j);
//					fastForwardWaitingView.setCurrentTime(remainingTimeSec/60 + " min " + remainingTimeSec%60 + " sec");
//				}
//
//				// We process the data gathered by the mining algorithms.
//				fastForwardWaitingView.setCurrentOperation("Processing data");
//
//
//
//				Rank<Pattern> rank = OCMManager.getNewRanking(INSTANCE.interestingPattern, INSTANCE.neutralPattern, INSTANCE.trashedPattern);
//				Rank<Pattern> rawRank = new Rank<>(rank);
//
//				if(mainPriority.equals(Condition.ActionPatternChoice.TRASH)){
//					INSTANCE.trashedPattern = computeListPattern(rank, rawRank, listCondition, Condition.ActionPatternChoice.TRASH);
//					rank.removeAll(INSTANCE.trashedPattern);
//					INSTANCE.interestingPattern = computeListPattern(rank, rawRank, listCondition, Condition.ActionPatternChoice.KEEP);
//					rank.removeAll(INSTANCE.interestingPattern);
//					INSTANCE.neutralPattern = new ArrayList<>(rank);
//				}else{
//					INSTANCE.interestingPattern = computeListPattern(rank, rawRank, listCondition, Condition.ActionPatternChoice.KEEP);
//					rank.removeAll(INSTANCE.interestingPattern);
//					INSTANCE.trashedPattern = computeListPattern(rank, rawRank, listCondition, Condition.ActionPatternChoice.TRASH);
//					rank.removeAll(INSTANCE.trashedPattern);
//					INSTANCE.neutralPattern = new ArrayList<>(rank);
//				}
//
//
//				// Print in the error channel the results of the automated selection.
//
//				System.err.println("Kept Patterns :");
//				INSTANCE.interestingPattern.forEach(System.err::println);
//				System.err.println("Neutral Patterns :");
//				INSTANCE.neutralPattern.forEach(System.err::println);
//				System.err.println("Trashed Patterns :");
//				INSTANCE.trashedPattern.forEach(System.err::println);
//				System.err.println("");
//
//				int nbPatternRank = rawRank.size();
//				double nbQuality = INSTANCE.interestingPattern.size() - INSTANCE.trashedPattern.size();
//				DebugLogger.printQuality(i +";"+ ((nbQuality/nbPatternRank)+1)/2);
//
//				double pbr = (i+1.)/nbRound;
//				fastForwardWaitingView.setProgressBarRound(pbr);
//			}
//			fastForwardWaitingView.setCurrentOperation("Finished");
//			fastForwardWaitingView.setCurrentTime("Finished");
//			MainView.getCurrentMainView().refreshKeptPatternsList();
//		}catch(InterruptedException e){
//			e.printStackTrace();
//		}

		fastForwardWaitingView.setHasFinished(true);
	}

	//********** Internal Methods **********//

	private void waitForOCMInitialization(){
		try {
			while(!OCMManager.isInitialized() || !OCMManager.algorithmManagerIsMining()){
				Thread.sleep(500);
				if(stopRequested){
					return;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void waitForOCMResults(){
//		try{
//			DebugLogger.printDebug("OCLController: Trying to find better result.");
//			Rank<Pattern> rank = OCMManager.getNewRanking(interestingPattern, neutralPattern, trashedPattern);
//			while (rank.size() <= 0){
//				Thread.sleep(250);
//				DebugLogger.printDebug("OCLController: Trying to find better result.");
//				rank = OCMManager.getNewRanking(interestingPattern, neutralPattern, trashedPattern);
//				if(stopRequested){
//					return;
//				}
//			}
//			synchronized (OCLController.class) {
//				userRank.clear();
//				userRank.addAll(rank);
//			}
//		} catch (InterruptedException e){
//			e.printStackTrace();
//		}
	}

	//********** Getters/Setters Methods **********//

	// Getters //
	public static String getPathDataFile(){
		return INSTANCE.pathDataFile;
	}

	// Setters //
	public static void setPatternList(List<Pattern> interestingPattern, List<Pattern> neutralPattern, List<Pattern> trashedPattern){
		INSTANCE.interestingPattern = interestingPattern;
		INSTANCE.neutralPattern = neutralPattern;
		INSTANCE.trashedPattern = trashedPattern;
	}

	public static void setWrapperType(Pattern.WrapperType wrapperType){
		INSTANCE.wrapperTypeSelected = wrapperType;
	}

	//********** Request From View Methods **********//

	public static List<Pattern> getUserRank(){
		return OCMManager.getNewRanking(INSTANCE.interestingPattern, INSTANCE.neutralPattern, INSTANCE.trashedPattern);
	}

	public static void requestStop(){
		INSTANCE.stopRequested = true;
		DebugLogger.printDebug("OCLController: Stopping the controller.");
		OCMManager.requestStop();
		InfoAlgorithm.requestStop();
		Monitor.requestStop();
	}

	public static void requestFFStop(){
		DebugLogger.printDebug("OCLController: Stopping the Fast Forward.");
		INSTANCE.fastForward.setStopRequested();
	}

}
