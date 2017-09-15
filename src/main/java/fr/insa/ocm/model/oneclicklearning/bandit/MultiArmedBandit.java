package fr.insa.ocm.model.oneclicklearning.bandit;

/**
 * <h1>Multi-armed bandit problem representation</h1>
 * <p>This interface provides simple methods that answer to
 * the multi-armed bandit problem</p>
 *
 * @see <a href="https://en.wikipedia.org/wiki/Multi-armed_bandit">Multi-armed bandit - Wikipedia</a>
 */
public interface MultiArmedBandit {

	/**
	 * Updates the reward for a given arm
	 * @param arm the arm associates to the reward
	 * @param reward reward value for the given arm
	 */
	void updateReward(int arm, double reward);

	/**
	 * Returns the arm to use.
	 * @return the arm
	 */
	int getArmToUse();

	/**
	 * Returns the current weights. Also called v.
	 * @return The current weights in an array of double.
	 */
	double[] getWeights();

}
