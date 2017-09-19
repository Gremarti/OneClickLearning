package fr.insa.ocm.controller;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.controller.mainuis.MainView;
import javafx.application.Platform;

public class InfoAlgorithm extends Thread {

	private static InfoAlgorithm INSTANCE = new InfoAlgorithm();

	private volatile boolean stopRequested;
	private volatile boolean paused;

	private InfoAlgorithm(){
		INSTANCE = this;


		stopRequested = false;
		paused = true;

		INSTANCE.start();
	}

	@Override
	public void run(){
		Thread.currentThread().setName("Information Gatherer");

		try {
			DebugLogger.initializeBanditLog();
			DebugLogger.initializeCoactiveLog();
			DebugLogger.initializeAlgorithmLog();
			DebugLogger.initializeSelectionDistributionLog();
		} catch (NullPointerException e){
			DebugLogger.printDebug("InfoAlgorithm: Unable to initialize the bandit or the coactive logger.", DebugLogger.MessageSeverity.MEDIUM);
		}

		while(!stopRequested){
			try {
				changeInfoLabel();
				DebugLogger.logBandit();
				DebugLogger.logCoactive();
				DebugLogger.logAlgorithm();
			} catch (NullPointerException exception){
				DebugLogger.printDebug("InfoAlgorithm: Unable to have the informations on the Algorithm Launcher.", DebugLogger.MessageSeverity.MEDIUM);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void changeInfoLabel(){
		MainView mainView = MainView.getCurrentMainView();

		int nbAlgoLaunched = OCMManager.algorithmManagerGetNbAlgoLaunched();
		int nbPatternFound = OCMManager.algorithmManagerGetNbPatternFound();
		String status = paused ? "Paused" : "Mining";

		Platform.runLater(() -> mainView.getLabelInfo().setText(
				"Mining Algorithm status : "+ status + "\n\n" +
				"Since the last mining round :\n  "+
				nbAlgoLaunched+" algorithms have been launched\n  "+
				nbPatternFound+" patterns have been found"));
	}


	static void requestStop(){
		INSTANCE.stopRequested = true;
	}

	public static void setPaused(boolean isPaused){
		INSTANCE.paused = isPaused;
	}
}
