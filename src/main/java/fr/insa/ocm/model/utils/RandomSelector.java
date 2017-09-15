package fr.insa.ocm.model.utils;

import java.util.Random;

/**
 * RandomSelector contains method to operate probabilistic selection on an array.
 */
public final class RandomSelector {

    /**
     * Returns the selected index based on the weights (probabilities).
     * Linear O(n) version.
     * @param weights the probabilities
     * @return the selected index, -1 if an error occurs.
     */
    public static int randomPick(double[] weights) {
        double weightSum = 0;
        for (double weight : weights) {
            weightSum += weight;
        }

        double randomValue = new Random().nextDouble()*weightSum;

        for(int i=0; i<weights.length; ++i) {
            randomValue -= weights[i];
            if (randomValue <= 0) {
                return i;
            }
        }

        //return -1 if not found
        return -1;
    }
}
