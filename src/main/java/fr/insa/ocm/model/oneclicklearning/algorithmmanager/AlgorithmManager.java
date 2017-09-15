package fr.insa.ocm.model.oneclicklearning.algorithmmanager;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.bandit.CesaBianciBandit;
import fr.insa.ocm.model.oneclicklearning.bandit.MultiArmedBandit;
import fr.insa.ocm.model.utils.exceptions.NotInitializedException;
import fr.insa.ocm.model.utils.serialize.SearchSave;
import fr.insa.ocm.model.wrapper.api.AbstractPattern;
import fr.insa.ocm.model.wrapper.api.AlgorithmLauncher;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * <h1>Algorithm Manager</h1>
 * <p>Manages the startMonitoring and stop of the different data mining algorithm. Also manages their resultsts</p>
 */
public class AlgorithmManager {

	// These semaphores are used to synchronize differents mining threads.
	private static Semaphore operationBandit = new Semaphore(1);
	private static Semaphore operationInfo = new Semaphore(1);
	private static Semaphore operationCoactive = new Semaphore(1);

	/**
	 * <h1>Mining Thread</h1>
	 * <p>Thread that runs algorithms one by one while not stop.</p>
	 */
	private class MiningThread extends Thread {

		private volatile boolean stopRequested;
		private int currentAlgorithm;

		/**
		 * Creates a new MiningThread instance
		 */
		private MiningThread() {
			this.stopRequested = false;
			this.setName("Miner #"+nextThreadID);
			nextThreadID++;
		}


		/**
		 * Runs the thread until {@link #stopMining()} is called.
		 * The method runs data mining algorithm selected by the bandit one by one.
		 * If the thread is stop before adding data to the cache, this step is skip to end the thread.
		 */
		@Override
		public void run() {
			DebugLogger.printDebug("MiningThread (" + Thread.currentThread().getName() + "): is mining.");
		    while (!stopRequested) {
		    	AlgorithmManager.operationBandit.acquireUninterruptibly();
				currentAlgorithm = bandit.getArmToUse();
				AlgorithmManager.operationBandit.release();

				double startTime = System.currentTimeMillis();
				List<Pattern> results = algorithmLauncher.startAlgorithm(currentAlgorithm);
				double elapsedTime = System.currentTimeMillis() - startTime;

				if (!stopRequested) {

					operationInfo.acquireUninterruptibly();
					nbAlgoLaunched++;
					nbPatternFound += results.size();
					operationInfo.release();


					operationCoactive.acquireUninterruptibly();
					OCMManager.cacheAddPatterns(results, elapsedTime, currentAlgorithm);
					operationCoactive.release();
				}
			}

			DebugLogger.printDebug(Thread.currentThread().getName() +": is stopping.");
		}

		/**
		 * Stops the thread and stop, if possible, the current running algorithm
		 */
		private void stopMining(){
			this.stopRequested = true;
			algorithmLauncher.stopAlgorithm();
		}
	}

	//private static final AlgorithmManager INSTANCE = new AlgorithmManager();
	private static final int MAX_THREAD = 1; // TODO : Il faut prendre en compte dans les wrappers d'algorithm le fait qu'on soit en multithreadé pour > 1;

	private MultiArmedBandit bandit;
	private AlgorithmLauncher algorithmLauncher;
	private List<MiningThread> listCurrentThread;

	private static int nextThreadID = 0;

	private volatile boolean isMining;
	private volatile boolean hasBeenInitialized;
	private volatile int nbAlgoLaunched;
	private volatile int nbPatternFound;

	/**
	 * Creates a new instance of AlgorithmManager
	 */
	public AlgorithmManager(){
		nbAlgoLaunched = 0;
		nbPatternFound = 0;

		listCurrentThread = new ArrayList<>();

		isMining = false;
		hasBeenInitialized = false;
    }

	//********** Initializing Methods **********//

    public void initialize(AbstractPattern.WrapperType wrapperType,
                           String filePath){

		if(!this.isMining) {
			algorithmLauncher = wrapperType.getAlgorithmLauncher(filePath);

			if (algorithmLauncher != null) {
				bandit = new CesaBianciBandit(algorithmLauncher.getNbAlgorithms());
				hasBeenInitialized = true;
			} else {
				DebugLogger.printDebug("ERR: Unable to loadData the AlgorithmManager, have you correctly set the type of library you want to use ?", DebugLogger.MessageSeverity.CRITICAL);
			}
		} else {
			DebugLogger.printDebug("LOG: Unable to loadData the AlgorithmManger when it is currently mining.", DebugLogger.MessageSeverity.HIGH);
		}
    }

	//********** Serializing Methods **********//

	public void reload(MultiArmedBandit bandit){
		this.stopAllMiners();

		this.bandit = bandit;
		this.nbAlgoLaunched = 0;
	}

	public void save(SearchSave searchSave){
		searchSave.setMultiArmedBandit(bandit);
	}

	//********** Public Methods **********//

	/**
	 * Starts the data mining in a thread, {@link MiningThread}.
	 */
	public void startMining() {
		if(hasBeenInitialized) {
			try {
				operationInfo.acquire();
				nbAlgoLaunched = 0;
				nbPatternFound = 0;
				operationInfo.release();
				resumeMining();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			DebugLogger.printDebug("LOG: Cannot startMonitoring mining since the AlgorithmManager has not been initialized", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	public synchronized void resumeMining(){
		if(hasBeenInitialized) {
			isMining = true;

			// Creating the new threads.
			for (int i = 0; i < MAX_THREAD; i++) {
				MiningThread newMiningThread = new MiningThread();
				newMiningThread.setDaemon(true);
				newMiningThread.start();
				listCurrentThread.add(newMiningThread);
			}
		} else {
			DebugLogger.printDebug("LOG: Cannot resume mining since the AlgorithmManager has not been initialized", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	public void pauseMining(){
		this.stopAllMiners();
	}

	/**
	 * Stops the data mining.
	 */
	public void stopMining(){
		pauseMining();
	}

	//********** Internal Methods **********//

	private synchronized void stopAllMiners(){
		listCurrentThread.forEach(miningThread -> {
			if(miningThread != null){
				miningThread.stopMining();
				try {
					DebugLogger.printDebug("AlgorithmManager: Waiting for the stop of "+ miningThread.getName());
					miningThread.join();
					DebugLogger.printDebug("AlgorithmManager: "+ miningThread.getName() +" has stopped");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		listCurrentThread.clear();

		this.isMining = false;
	}

	//********** Proxy Methods **********//


	// Bandit //
	public void banditUpdateReward(int arm, double reward){
		bandit.updateReward(arm, reward);
	}

	public double[] banditGetWeights(){
		return bandit.getWeights();
	}

	// Algorithm Launcher //

	public List<String> algorithmLauncherGetListAlgorithmName(){
		return algorithmLauncher.getListAlgorithmName();
	}

	public List<String> algorithmLauncherGetListAttributeName(){ return algorithmLauncher.getListAttributeName(); }

	//********** Getters/Setters Methods **********//

	public int getNbAlgoLaunched(){
		return nbAlgoLaunched;
	}

	public int getNbPatternFound(){
		return nbPatternFound;
	}

	public boolean isMining(){
		return isMining;
	}

	public int getNbAttributeMeasures() throws NotInitializedException {
		if(hasBeenInitialized){
			return algorithmLauncher.getNbAlgorithms() + algorithmLauncher.getNbAttributes();
		} else {
			DebugLogger.printDebug("ERR: Cannot get the number of attribute measure if the AlgorithmManager is not initialized", DebugLogger.MessageSeverity.HIGH);
			throw new NotInitializedException();
		}
	}

	//********** Old Methods **********//


//	/**
//	 * Returns the bandit instance used by the {@link AlgorithmManager}.
//	 * @return The instance of the bandit used by the {@link AlgorithmManager}.
//	 */
//	public MultiArmedBandit getBandit(){
//		return bandit;
//	}
//
//	/**
//	 * Imports the data from a CSV file.
//	 * Do not check if the file exists.
//	 * @param filePath The path to the CSV file.
//	 */
//	public void importData(String filePath) {
//		algorithmLauncher.importData(filePath);
//		CoactiveLearningRanking.getInstance().init(algorithmLauncher.getNbAttributes() + algorithmLauncher.getNbAlgorithms());
//	}
//
//	public void reloadData(String filePath){
//		algorithmLauncher = new AlgorithmLauncherRealKD();
//		algorithmLauncher.importData(filePath);
//	}




}
