package fr.insa.ocm.model.oneclicklearning.coactivelearning.ranking;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.AbstractCoactiveLearning;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.CoactiveLearning;
import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.utils.serialize.SearchSave;
import fr.insa.ocm.model.wrapper.api.Pattern;
import java.lang.Math;
import java.util.List;

/**
 * <h1>Coactive Learning Algorithm</h1>
 * <p>Provides methods for the coactive learning algorithm.</p>
 */
public class CoactiveLearningRanking extends AbstractCoactiveLearning {

	private static final int D_MAX_BORN = 250; //born d to this max value
	@Expose private int d;

	/**
	 * Deserializer constructor.
	 * @param weights Deserializer parameter.
	 * @param previousSystemState Deserializer parameter.
	 * @param nbCycles Deserializer parameter.
	 * @param nbInterestingnessMeasures Deserializer parameter.
	 * @param coactiveType Deserializer parameter.
	 * @param d Deserializer parameter.
	 */
	public CoactiveLearningRanking(Vector weights,
	                               SystemState previousSystemState,
	                               int nbCycles,
	                               int nbInterestingnessMeasures,
	                               CoactiveType coactiveType,
	                               int d){
		super(weights, previousSystemState, nbCycles, nbInterestingnessMeasures, coactiveType);

		this.d = d;
	}

	/**
	 * Create a new CoactiveLearningRanking instance.
	 */
	public CoactiveLearningRanking() {
		super(CoactiveType.RANKING);
		d = 1;
	}

	//********** Public Methods **********//

	/**
	 * Updates the weight vector w(t) with the user selection.
	 * The user selection is the patterns he has selected, rejected and not selected or rejected.
	 * @param newState Actual system state which contains the selected, rejected and neutral patterns.
	 */
	@Override
	public synchronized void updateWeight(SystemState newState){
		if(!newState.getProposedRanking().isEmpty()){
			double s = Math.pow(OCMManager.cacheGetSizeListBestPatterns(), 1/d);
			double theta_t = 1/(2*s*Math.sqrt(Math.pow(2, Math.floor(Math.log10(nbCycles)))));
			double z = 0;

			Vector phi_userRanking = auxiliaryFunctionPhi(newState.getUserRanking());
			Vector phi_proposedRanking = auxiliaryFunctionPhi(newState.getProposedRanking());
			for(int i = 0; i<nbInterestingnessMeasures; i++){
				weights.getValues()[i] = weights.getValues()[i]* Math.exp(theta_t*(phi_userRanking.getValues()[i] - phi_proposedRanking.getValues()[i]));
				z += weights.getValues()[i];
			}

			for(int i = 0; i<nbInterestingnessMeasures; i++){
				weights.getValues()[i] = weights.getValues()[i]/z;
			}
			previousSystemState = newState;
			if (d < D_MAX_BORN) {
				d++;
			}
			nbCycles++;
		}
	}

	@Override
	public double getUtility(List<Pattern> listPatterns){
		if(listPatterns instanceof Rank<?>){
			return this.getUtilityRanking(new Rank<>(listPatterns));
		} else {
			DebugLogger.printDebug("CoactiveLearningRanking: getUtility recieved a List<Pattern> instead of a Rank<Pattern>.", DebugLogger.MessageSeverity.HIGH);
		}
		return Double.NaN;
	}

	/**
	 * Returns the utility of an given pattern rank.
	 * @param rank The rank to get its utility
	 * @return The utility value of the given rank
	 */
	private double getUtilityRanking(Rank<Pattern> rank){
		return Vector.scalarProduct(weights, auxiliaryFunctionPhi(rank));
	}

	//********** Internal Methods **********//

	/**
	 * Computes the auxiliary function Phi.
	 * @param rank The pattern rank
	 * @return The vector computes with the function Phi.
	 */
	private synchronized Vector auxiliaryFunctionPhi(Rank<Pattern> rank){
		Vector phiVectorResult = new Vector();
		for(int i = 0; i<nbInterestingnessMeasures; i++){
			Vector phiVector_f = new Vector();
			int count = 1;
			for (Pattern p: rank) {
				int notPresent = 1;
				double interestingnessValue;
				if(nbCycles!=1 && (previousSystemState.getInterestingPatterns().contains(p) || previousSystemState.getTrashedPatterns().contains(p))){
					notPresent = 0;
				}

				interestingnessValue = p.getAttributesVector().getValues()[i];

				phiVector_f.put(notPresent*interestingnessValue/Math.log10(count+1));
				count++;
			}
			phiVectorResult.put(phiVector_f.norm(d));
		}
		return phiVectorResult;
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	public synchronized int getNbCycles(){ return nbCycles; }

	public synchronized double[] getWeights(){
		return weights.getValues();
	}
}
