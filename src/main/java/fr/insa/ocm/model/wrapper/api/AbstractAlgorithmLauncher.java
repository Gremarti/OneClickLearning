package fr.insa.ocm.model.wrapper.api;

import fr.insa.ocm.model.wrapper.realkd.AlgorithmLauncherRealKD;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;

import java.util.*;

public abstract class AbstractAlgorithmLauncher implements AlgorithmLauncher {

	// The monitoring the system.
	// A map of the number of calls for each algorithms.
	protected static Map<String, Integer> mapNumberCallsPerAlgorithm = Collections.synchronizedMap(new HashMap<>());

	// The list of all the attribute names in the current dataset.
	protected List<String> listAttributeName;

	protected AbstractAlgorithmLauncher(){
		listAttributeName = new ArrayList<>();
	}

	public abstract int getNbAlgorithms();

	public abstract List<Pattern> startAlgorithm(int algorithmNumber);

	public abstract void stopAlgorithm();

	@Override
	public int getNbAttributes() {
		return listAttributeName.size();
	}

	@Override
	public List<String> getListAttributeName() {
		return new ArrayList<>(listAttributeName);
	}

	public abstract List<String> getListAlgorithmName();

	public static Map<String, Integer> getStatsAlgorithmsLaunched() {
		return mapNumberCallsPerAlgorithm;
	}

	public static List<String> getAllAvailableAlgorithm(){
		List<String> listAllAvailableAlgorithm = new ArrayList<>(AlgorithmLauncherRealKD.getListAvailableMiningAlgorithm());
		listAllAvailableAlgorithm.addAll(AlgorithmLauncherSPMF.getListAvailableMiningAlgorithm());
		return listAllAvailableAlgorithm;
	}
}
