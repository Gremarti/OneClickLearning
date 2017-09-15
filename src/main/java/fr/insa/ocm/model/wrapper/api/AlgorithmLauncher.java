package fr.insa.ocm.model.wrapper.api;

import java.util.List;

public interface AlgorithmLauncher{
	
	int getNbAlgorithms();

	List<Pattern> startAlgorithm(int algorithmNumber);

	void stopAlgorithm();

	int getNbAttributes();

	List<String> getListAttributeName();

	List<String> getListAlgorithmName();
}
