package fr.insa.ocm.model.oneclicklearning.coactivelearning.api;


import fr.insa.ocm.model.oneclicklearning.coactivelearning.ranking.CoactiveLearningRanking;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.set.CoactiveLearningSet;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public interface CoactiveLearning {

	enum CoactiveType {
		RANKING, SET;

		public CoactiveLearning newInstance(){
			switch (this){
				case RANKING:
					return new CoactiveLearningRanking();
				case SET:
					return new CoactiveLearningSet();
				default:
					return new CoactiveLearningSet();
			}
		}

		@Override
		public String toString() {
			switch (this){
				case RANKING:
					return "Ranking";
				case SET:
					return "Set";
				default:
					return "";
			}
		}
	}

	void initialize();

	void updateWeight(SystemState newState);

	double getUtility(List<Pattern> listPatterns);

	double[] getWeights();

	int getNbCycles();
}
