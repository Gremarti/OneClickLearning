package fr.insa.ocm.model.oneclicklearning.coactivelearning.set;


import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.AbstractCoactiveLearning;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.CoactiveLearning;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.utils.serialize.SearchSave;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public class CoactiveLearningSet extends AbstractCoactiveLearning {

	/**
	 * Deserializer constructor.
	 * @param weights Deserializer parameter.
	 * @param previousSystemState Deserializer parameter.
	 * @param nbCycles Deserializer parameter.
	 * @param nbInterestingnessMeasures Deserializer parameter.
	 * @param coactiveType Deserializer parameter.
	 */
	public CoactiveLearningSet(Vector weights,
	                           SystemState previousSystemState,
	                           int nbCycles,
	                           int nbInterestingnessMeasures,
	                           CoactiveType coactiveType){
		super(weights, previousSystemState, nbCycles, nbInterestingnessMeasures, coactiveType);
	}

	public CoactiveLearningSet(){
		super(CoactiveType.SET);
	}

	@Override
	public synchronized void updateWeight(SystemState newState) {
		if(!newState.getProposedRanking().isEmpty()){
			double s = OCMManager.cacheGetSizeListBestPatterns();
			double theta_t = 1/(2*s*Math.sqrt(Math.pow(2, Math.floor(Math.log10(nbCycles)))));
			double z = 0;

//			Vector phi_userRanking = auxiliaryFunctionPhi(newState.getUserRanking());
			Vector phi_intersting = auxiliaryFunctionPhi(newState.getInterestingPatterns());
			Vector phi_neutral = auxiliaryFunctionPhi(newState.getNeutralPatterns());
			Vector phi_proposedRanking = auxiliaryFunctionPhi(newState.getProposedRanking());

			for(int i = 0; i<nbInterestingnessMeasures; i++){
				weights.getValues()[i] = weights.get(i) * Math.exp(theta_t*((phi_intersting.get(i) + 0.5*phi_neutral.get(i)) - phi_proposedRanking.get(i)));
				z += weights.get(i);
			}

			for(int i = 0; i<nbInterestingnessMeasures; i++){
				weights.getValues()[i] = weights.get(i)/z;
			}
			previousSystemState = newState;

			nbCycles++;
		}
	}

	@Override
	public double getUtility(List<Pattern> listPatterns) {
		return Vector.scalarProduct(weights, auxiliaryFunctionPhi(listPatterns));
	}

	//********** Internal Methods **********//

	/**
	 * Computes the auxiliary function Phi.
	 * @param listPatterns The pattern rank
	 * @return The vector computes with the function Phi.
	 */
	private synchronized Vector auxiliaryFunctionPhi(List<Pattern> listPatterns){
		Vector phiVectorResult = new Vector();
		for(int i = 0; i < nbInterestingnessMeasures; ++i){
			double sum = 0d;
			for(Pattern pattern : listPatterns){
				sum += pattern.getAttributesVector().get(i);
			}
			phiVectorResult.put(sum);
		}
		return phiVectorResult;
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	@Override
	public synchronized double[] getWeights() {
		return weights.getValues();
	}

	@Override
	public int getNbCycles() {
		return nbCycles;
	}
}
