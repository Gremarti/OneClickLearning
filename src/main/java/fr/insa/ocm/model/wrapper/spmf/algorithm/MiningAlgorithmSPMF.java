package fr.insa.ocm.model.wrapper.spmf.algorithm;

import fr.insa.ocm.model.wrapper.spmf.PatternSPMF;

import java.util.List;

public interface MiningAlgorithmSPMF {

	void loadData(String csvPath);

	List<PatternSPMF> call();
}
