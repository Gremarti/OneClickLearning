package fr.insa.ocm.model.oneclicklearning.bandit;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.utils.RandomSelector;

/**
 * <h1>Cesa Bianci Bandit algorithm</h1>
 * <p>Provides an implementation of the CesiaBianci Bandit algorithm which solved the
 * multi-armed bandit problem</p>
 */
public class CesaBianciBandit implements MultiArmedBandit {

	// The currend weights, v
	@Expose private volatile double[] weights;
	// The currend round
	@Expose private int round;
	// The value that normalizes the sum of v
	@Expose private double V;

	//TODO Javadoc for serializing purpose only
	@SuppressWarnings("unused")
	public CesaBianciBandit(){}

	public CesaBianciBandit(double[] weights, int round, double V){
		this.weights = weights;
		this.round = round;
		this.V = V;
	}

	/**
	 * Creates a new CesaBianciBandit instances, with a given number of arms/
	 * @param nbOfArms the number of arms for the instance
	 */
	public CesaBianciBandit(int nbOfArms){
		round = 1;
		V = 0;
		weights = new double[nbOfArms];
		for(int i = 0; i< weights.length; ++i) {
			weights[i] = 1./ (double) weights.length;
		}
		V = 1;
	}

	@Override
	public void updateReward(int arm, double reward) {
		if (arm >= weights.length) {
			throw new IndexOutOfBoundsException("Arm value should be at most: "+(weights.length-1));
		}


		double beta = getBeta();
		double gamma = getMixtureCoefficient(beta);
		double n = getLearningRate(gamma);
		double[] pi = getSelectionDistribution(gamma);

		double[] g = new double[weights.length];
		V = 0;
		for(int i=0; i<g.length; ++i) {
			if (i==arm) {
				g[i] = (reward + beta)/pi[arm];
			}
			else {
				g[i] = beta/pi[arm];
			}
			weights[i] = weights[i] * Math.exp(n*g[i]);
			V += weights[i];
		}
		//Normalize the sum of weights to 1, so the weights don't grow exponentially
		for (int i=0; i<weights.length; ++i) {
			weights[i] = weights[i]/V;
		}
		V = 1;

		round++;
	}

	@Override
	public int getArmToUse() {
//		double beta = getBeta();
//		double gamma = getMixtureCoefficient(beta);
//		return RandomSelector.randomPick(getSelectionDistribution(gamma));
		return RandomSelector.randomPick(getSelectionDistribution(1/(0.01*((double)round)+1)));
	}

	/**
	 * Computes the value beta for the CesaBianci algorithm
	 * @return the beta value
	 */
	public double getBeta() {
		double temp = Math.log(10*weights.length);
		temp = temp / (weights.length*Math.pow(2,Math.floor(Math.log10(round))));
		temp = Math.sqrt(temp);

		return temp;
	}

	/**
	 * Computes the mixture coefficient for the CesaBianci algorithm. Also called gamma.
	 * @param beta the beta value for the CesaBianci algorithm
	 * @return the mixture coefficient, gamma
	 */
	public double getMixtureCoefficient(double beta) {
//		return (4*weights.length*beta)/(3+beta);
		return (4*beta)/(3+beta);
	}

	/**
	 * Computes the learning rate for the CesaBianci algorithm. Also called eta
	 * @param mixtureCoeff the mixture coefficient for the CesaBianci algorithm
	 * @return the learning rate, eta
	 */
	public double getLearningRate(double mixtureCoeff) {
		return (mixtureCoeff/(2*weights.length));
	}

	/**
	 * Computes the selection distribution for the CesaBianci algorithm. Also called pi.
	 * @param mixtureCoef the mixture coefficient for the CesaBianci algorithm
	 * @return the selection distribution, pi
	 */
	public double[] getSelectionDistribution(double mixtureCoef) {
		double[] selectionDistibution = new double[weights.length];
		for (int i=0; i<selectionDistibution.length; ++i) {
			double temp = (1-mixtureCoef)*weights[i];
			selectionDistibution[i] = temp/V + mixtureCoef/weights.length;
		}

		//FIXME

//		System.arraycopy(weights, 0, selectionDistibution, 0, weights.length);
		DebugLogger.logSelectionDistribution(selectionDistibution);

		return selectionDistibution;
	}

	/**
	 * Returns the current weights. Also called v.
	 * @return The current weights in an array of double.
	 */
	public double[] getWeights() {
		return weights;
	}

}
