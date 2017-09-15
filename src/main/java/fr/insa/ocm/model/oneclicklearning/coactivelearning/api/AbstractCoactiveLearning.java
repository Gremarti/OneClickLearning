package fr.insa.ocm.model.oneclicklearning.coactivelearning.api;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCoactiveLearning implements CoactiveLearning {

	@Expose
	protected CoactiveType coactiveType;

	@Expose
	protected Vector weights;
	@Expose
	protected SystemState previousSystemState;
	@Expose
	protected int nbCycles;
	@Expose
	protected int nbInterestingnessMeasures;

	/**
	 * Deserializer constructor.
	 *
	 * @param weights                   Deserializer parameter.
	 * @param previousSystemState       Deserializer parameter.
	 * @param nbCycles                  Deserializer parameter.
	 * @param nbInterestingnessMeasures Deserializer parameter.
	 * @param coactiveType              Deserializer parameter.
	 */
	protected AbstractCoactiveLearning(Vector weights,
	                                   SystemState previousSystemState,
	                                   int nbCycles,
	                                   int nbInterestingnessMeasures,
	                                   CoactiveType coactiveType) {
		this.weights = weights;
		this.previousSystemState = previousSystemState;
		this.nbCycles = nbCycles;
		this.nbInterestingnessMeasures = nbInterestingnessMeasures;

		this.coactiveType = coactiveType;
	}

	protected AbstractCoactiveLearning(CoactiveType coactiveType) {
		previousSystemState = new SystemState(new ArrayList<>());
		nbCycles = 1;
	}

	//********** Initializing Methods **********//

	@Override
	public void initialize() {
		nbInterestingnessMeasures = Pattern.MeasureType.values().length + OCMManager.algorithmLauncherGetListAlgorithmName().size() + OCMManager.algorithmLauncherGetListAttributeName().size();
		double[] initWeights = new double[nbInterestingnessMeasures];
		for (int i = 0; i < nbInterestingnessMeasures; i++) {
			initWeights[i] = 1.0 / nbInterestingnessMeasures;
		}
		weights = new Vector(initWeights);
	}

	//********** Public Methods **********//

	@Override
	public abstract void updateWeight(SystemState newState);

	@Override
	public abstract double getUtility(List<Pattern> listPatterns);

	//********** Getters/Setters Methods **********//

	// Getters //

	@Override
	public abstract double[] getWeights();

	@Override
	public abstract int getNbCycles();
}
