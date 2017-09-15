package fr.insa.ocm.viewmodel;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.wrapper.api.AbstractAlgorithmLauncher;
import fr.insa.ocm.model.wrapper.api.Pattern;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Monitor extends Thread {

	private static Monitor INSTANCE;

	// Monitor some stats about the launched algorithms
	private SimpleStringProperty monitorAlgorithmsLaunched;
	private SimpleStringProperty monitorBanditWeight;
	private SimpleStringProperty monitorCoactiveWeight;

	private static boolean stopRequested = false;
	private static boolean paused = false;

	private Monitor() {
		monitorAlgorithmsLaunched = new SimpleStringProperty("");
		monitorBanditWeight = new SimpleStringProperty("");
		monitorCoactiveWeight = new SimpleStringProperty("");
	}

	@Override
	public void run() {
		try {
			while (!stopRequested) {
				if(!paused) {
					monitorAlgorithmCalls();
					monitorBanditWeight();
					monitorCoactiveWeights();
				}

				DebugLogger.printDebug("Monitor: Stats refreshed.");
				Thread.sleep(1000);
			}
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}

	private void monitorAlgorithmCalls() {
		final Map<String, Integer> mapStatsAlgorithmsLaunched = AbstractAlgorithmLauncher.getStatsAlgorithmsLaunched();
		List<String> listAlgorithmUsed = OCMManager.algorithmLauncherGetListAlgorithmName();
		StringBuilder stringBuilder = new StringBuilder();
		for(String algorithmName : mapStatsAlgorithmsLaunched.keySet()){
			if(listAlgorithmUsed.contains(algorithmName)) {
				stringBuilder.append("The algorithm named: ");
				stringBuilder.append(algorithmName);
				stringBuilder.append(" has been launched ");
				stringBuilder.append(mapStatsAlgorithmsLaunched.get(algorithmName));
				stringBuilder.append(" times.\n");
			}
		}

		Platform.runLater(() -> monitorAlgorithmsLaunched.set(stringBuilder.toString()));
	}

	private void monitorBanditWeight() {
		double[] banditWeights = OCMManager.banditGetWeights();
		List<String> algorithmNames = OCMManager.algorithmLauncherGetListAlgorithmName();

		if (banditWeights.length == algorithmNames.size()) {
			Platform.runLater(() -> monitorBanditWeight.set(makeText(banditWeights, algorithmNames)));
		} else {
			DebugLogger.printDebug("Monitor: Unable to match the size of the bandit weights with the number of algorithms", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	private void monitorCoactiveWeights() {
		double[] coactiveWeights = OCMManager.coactiveGetWeights();
		List<String> names = new ArrayList<>();

		for(Pattern.MeasureType measure : Pattern.MeasureType.values()){
			names.add(measure.toString());
		}
		names.addAll(OCMManager.algorithmLauncherGetListAttributeName());
		names.addAll(OCMManager.algorithmLauncherGetListAlgorithmName());

		if(coactiveWeights.length == names.size()){
			Platform.runLater(() -> monitorCoactiveWeight.set(makeText(coactiveWeights, names)));
		} else {
			DebugLogger.printDebug("Monitor: Unable to match the size of the coactive weights and the names associated", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	@NotNull
	private String makeText(double[] weights, List<String> names){
		StringBuilder strBuild = new StringBuilder("");
		DecimalFormat decimalFormat = new DecimalFormat("#0.0000");

		for(int i = 0; i < weights.length; ++i){
			strBuild.append(names.get(i));
			strBuild.append(" has a coefficient of ");
			strBuild.append(decimalFormat.format(weights[i]));
			strBuild.append(".\n");
		}

		return strBuild.toString();
	}

	public static void startMonitoring(){
		INSTANCE = new Monitor();
		INSTANCE.setDaemon(true);
		INSTANCE.setName("Monitor");
		INSTANCE.start();
	}

	public static void pauseMonitoring(){
		paused = true;
	}

	public static void resumeMonitoring(){
		paused = false;
	}

	public static void requestStop(){
		stopRequested = true;
	}

	public static SimpleStringProperty getMonitorAlgorithmsLaunched(){
		return INSTANCE.monitorAlgorithmsLaunched;
	}

	public static SimpleStringProperty getMonitorBanditWeights(){
		return INSTANCE.monitorBanditWeight;
	}

	public static SimpleStringProperty getMonitorCoactiveWeights(){
		return INSTANCE.monitorCoactiveWeight;
	}
}
