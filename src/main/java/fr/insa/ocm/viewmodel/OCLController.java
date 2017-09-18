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

/**
 *
 * Note: Singleton class.
 */
public class OCLController {

	public static final String VERSION = "0.5.0.1";

	private static OCLController INSTANCE = new OCLController();

	// The information about the last mining round.
	private List<Pattern> interestingPattern;
	private List<Pattern> neutralPattern;
	private List<Pattern> trashedPattern;

	// Keeps a track of the last fastforward done.
	private FastForward fastForward;

	// Settings of the controller
	private Pattern.WrapperType wrapperTypeSelected;
	private CoactiveLearning.CoactiveType coactiveLearningSelected;
	private Cache.CacheType cacheTypeSelected;

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

		InfoAlgorithm.setPaused(false);
		Monitor.startMonitoring();

		DebugLogger.printDebug("OCLController: initialized correctly.");
	}

	public static void reload(String pathDataFile){
		DebugLogger.printDebug("OCLController: Reloading OCLController.");
		Monitor.pauseMonitoring();
		OCMManager.requestStop();

		INSTANCE.interestingPattern = new ArrayList<>();
		INSTANCE.neutralPattern = new ArrayList<>();
		INSTANCE.trashedPattern = new ArrayList<>();

		DebugLogger.printDebug("OCLController: Reloading OCMManager.");
		OCMManager.reload(INSTANCE.wrapperTypeSelected,	INSTANCE.cacheTypeSelected, INSTANCE.coactiveLearningSelected, pathDataFile);
		OCMManager.algorithmManagerStartMining();

		Monitor.resumeMonitoring();

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

		fastForwardWaitingView.bindCurrentOperation(currentOperation);
		fastForwardWaitingView.bindCurrentTime(remainingTime);
		fastForwardWaitingView.bindProgressLearning(progressLearning);
		fastForwardWaitingView.bindProgressMining(progressMining);

		Thread threadFF = new Thread(fastForward);
		threadFF.setName("FastForward");
		threadFF.setDaemon(true);
		threadFF.start();

		try {
			while (!fastForward.isFinished()) {
				Platform.runLater(() -> {
					currentOperation.setValue("Current Operation: "+ fastForward.getCurrentOperation() + ".");
					remainingTime.setValue("Estimated time before completion: "+ fastForward.getRemainingTime());
					progressLearning.setValue(fastForward.getProgressLearning());
					progressMining.setValue(fastForward.getProgressMining());
				});
				Thread.sleep(100);
			}
		} catch (InterruptedException e){
			e.printStackTrace();
		}

		fastForwardWaitingView.setHasFinished(true);
	}

	//********** Internal Methods **********//

	//********** Getters/Setters Methods **********//

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
