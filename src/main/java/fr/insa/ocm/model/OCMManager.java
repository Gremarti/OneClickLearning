package fr.insa.ocm.model;


import fr.insa.ocm.model.oneclicklearning.cache.api.Cache;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.CoactiveLearning;
import fr.insa.ocm.model.oneclicklearning.algorithmmanager.AlgorithmManager;
import fr.insa.ocm.model.utils.PatternWarehouse;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.utils.exceptions.NotInitializedException;
import fr.insa.ocm.model.utils.serialize.SearchSave;
import fr.insa.ocm.model.wrapper.api.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OCMManager {

	private static Pattern.WrapperType currentWrapperType;
	private static Cache.CacheType currentCacheType = Cache.CacheType.SET;
	private static CoactiveLearning.CoactiveType currentCoactiveType = CoactiveLearning.CoactiveType.SET;

	private static final OCMManager INSTANCE = new OCMManager();

	// Main modules of OCM.
	private AlgorithmManager algorithmManager;
	private CoactiveLearning coactiveLearning;
	private Cache cache;

	private PatternWarehouse patternWarehouse;

	// The current System State.
	private SystemState currentState;

	// Boolean to check if OCMManager has been initialized
	private boolean isInitialized = false;

	// OCMManager does not call the same method if the user asked for the first ranking of not.
	private boolean firstRanking;

	private OCMManager() {
		algorithmManager = new AlgorithmManager();
		coactiveLearning = currentCoactiveType.newInstance();
		cache = currentCacheType.newInstance();
		patternWarehouse = new PatternWarehouse();

		firstRanking = true;
	}

	//********** Initializing Methods **********//

	public static void initialize(Pattern.WrapperType wrapperType,
	                              String filePath){

		DebugLogger.printDebug("OCMManager: Initializing.");

		INSTANCE.currentState = new SystemState(new Rank<>());
		INSTANCE.algorithmManager.initialize(wrapperType, filePath);

		try{
			INSTANCE.coactiveLearning.initialize();

			INSTANCE.isInitialized = true;
		} catch (NotInitializedException exception) {
			DebugLogger.printDebug("OCMManager: The AlgorithmManager has not been initialized correctly, thus it is impossible to load data in the CoactiveLearningRanking module", DebugLogger.MessageSeverity.CRITICAL);
			throw exception;
		}

		currentWrapperType = wrapperType;

		DebugLogger.printDebug("OCMManager: Initialized correctly.");
	}

	/**
	 * Called when the user loads a new data file, but not for the first time.
	 * @param wrapperType The type of wrapper the user has selected.
	 * @param cacheType The type of cache the user has selected.
	 * @param coactiveType The type of coactive learning the user has selected.
	 * @param filePath The path leading to the data to load.
	 */
	public static void reload(Pattern.WrapperType wrapperType,
	                          @NotNull Cache.CacheType cacheType,
	                          @NotNull CoactiveLearning.CoactiveType coactiveType,
	                          String filePath){

		INSTANCE.algorithmManager.stopMining();

		INSTANCE.algorithmManager = new AlgorithmManager();
		INSTANCE.coactiveLearning = coactiveType.newInstance();
		INSTANCE.cache = cacheType.newInstance();
		INSTANCE.patternWarehouse = new PatternWarehouse();

		currentCacheType = cacheType;

		initialize(wrapperType, filePath);
	}

	//********** Public Methods **********//

	@NotNull
	public static List<Pattern> getNewRanking(List<Pattern> interestingPatterns,
	                                   List<Pattern> neutralPatterns,
	                                   List<Pattern> trashedPatterns) {
		if (isInitialized()) {
			List<Pattern> bestPatterns;

			if(INSTANCE.firstRanking) {
				bestPatterns = firstNewRanking();
				INSTANCE.firstRanking = false;
			} else {
				DebugLogger.printDebug("OCMManager: Creating a new ranking.");

				INSTANCE.algorithmManager.stopMining();
				INSTANCE.patternWarehouse.addToWarehouse(interestingPatterns);
				INSTANCE.currentState.update(interestingPatterns, neutralPatterns, trashedPatterns);
				INSTANCE.coactiveLearning.updateWeight(INSTANCE.currentState);
				bestPatterns = INSTANCE.cache.getBestPattern();
				INSTANCE.currentState = new SystemState(bestPatterns);
				INSTANCE.algorithmManager.startMining();

				DebugLogger.printDebug("OCMManager: Ranking created.");
			}

			return bestPatterns;
		} else {
			DebugLogger.printDebug("OCMManger: The AlgorithmManager has not been initialized correctly, thus it is impossible to get a new ranking", DebugLogger.MessageSeverity.CRITICAL);
			throw new NotInitializedException();
		}
	}

	@NotNull
	private static List<Pattern> firstNewRanking(){
		return INSTANCE.cache.getBestPattern();
	}

	public static void requestStop(){
		INSTANCE.algorithmManager.stopMining();
	}

	//********** Proxy Methods **********//

	// Coactive Learning //
	public static double coactiveGetUtility(List<Pattern> rank){
		return INSTANCE.coactiveLearning.getUtility(rank);
	}

	public static double[] coactiveGetWeights(){
		return INSTANCE.coactiveLearning.getWeights();
	}

	public static int coactiveGetNbRound(){
		return INSTANCE.coactiveLearning.getNbCycles();
	}

	// Algorithm Manager //
	public static void algorithmManagerUpdateReward(int arm, double reward){
		INSTANCE.algorithmManager.banditUpdateReward(arm, reward);
	}

	public static void algorithmManagerPauseMining(){
		INSTANCE.algorithmManager.pauseMining();
	}

	public static void algorithmManagerResumeMining(){
		INSTANCE.algorithmManager.resumeMining();
	}

	public static void algorithmManagerStartMining(){
		INSTANCE.algorithmManager.startMining();
	}

	public static void algorithmManagerStopMining(){
		INSTANCE.algorithmManager.stopMining();
	}

	public static boolean algorithmManagerIsMining(){
		return INSTANCE.algorithmManager.isMining();
	}

	public static int algorithmManagerGetNbAlgoLaunched() {
		return INSTANCE.algorithmManager.getNbAlgoLaunched();
	}

	public static int algorithmManagerGetNbPatternFound() {
		return INSTANCE.algorithmManager.getNbPatternFound();
	}

	// Bandit //
	public static double[] banditGetWeights(){
		return INSTANCE.algorithmManager.banditGetWeights();
	}

	// Algorithm Launcher //
	public static List<String> algorithmLauncherGetListAlgorithmName(){
		return INSTANCE.algorithmManager.algorithmLauncherGetListAlgorithmName();
	}

	public static List<String> algorithmLauncherGetListAttributeName(){
		return INSTANCE.algorithmManager.algorithmLauncherGetListAttributeName();
	}

	// Cache //
	public static void cacheAddPatterns(List<Pattern> newPatterns, double time, int arm){
		INSTANCE.cache.addPatterns(newPatterns, time, arm);
	}

	public static int cacheGetSizeListBestPatterns(){
		return INSTANCE.cache.getSizeListBestPatterns();
	}

	public static void cacheSetSizeListBestPatterns(int size){
		INSTANCE.cache.setSizeListBestPatterns(size);
	}

	// Pattern Warehouse //
	public static List<Pattern> patternWarehouseGetPatterns(){
		return INSTANCE.patternWarehouse.getStockedPatterns();
	}

	//********** Serializing Methods **********//

	/**
	 * Static entry point to restore a previous OCMManager, aka to restore a previous user search.
	 * @param searchSave The deserialized form of the user search state.
	 */
	public static void deserialize(SearchSave searchSave){
		INSTANCE.deserializeInstance(searchSave);
	}

	private void deserializeInstance(SearchSave searchSave){
		algorithmManager.stopMining();
		algorithmManager.reload(searchSave.getMultiArmedBandit());

		coactiveLearning = searchSave.getCoactiveLearning();
		cache = searchSave.getCache();
		patternWarehouse = searchSave.getPatternWarehouse();
		currentState = searchSave.getCurrentState();
		isInitialized = searchSave.getInitialized();
		firstRanking = searchSave.getFirstRanking();
	}

	public static void serialize(SearchSave searchSave){
		INSTANCE.serializeInstance(searchSave);
	}

	private void serializeInstance(SearchSave searchSave){
		algorithmManager.save(searchSave);

		searchSave.setCoactiveLearning(coactiveLearning);
		searchSave.setCache(cache);
		searchSave.setPatternWarehouse(patternWarehouse);
		searchSave.setCurrentState(currentState);
		searchSave.setInitialized(isInitialized);
		searchSave.setFirstRanking(firstRanking);
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	public static boolean isInitialized(){ return INSTANCE.isInitialized; }

	public static Pattern.WrapperType getCurrentUsedWrapperType(){
		return currentWrapperType;
	}

	// Setters //
}
