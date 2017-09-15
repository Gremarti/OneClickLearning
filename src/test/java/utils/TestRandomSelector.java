package utils;

import fr.insa.ocm.model.utils.RandomSelector;
import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestRandomSelector {

    private final int nbIterations = 1000000;
    private final double delta = 0.01f;

    @Test
    public void randomPickTest() {
        double[] weights = {0.5, 0.8, 1.3, 0.2, 0.6};
        int[] results = new int[5];
        double weightSum = 0;
        for(int i=0; i<results.length; ++i) {
            results[i] = 0;
            weightSum += weights[i];
        }

        for(int i=0; i<nbIterations; ++i) {
            int result = RandomSelector.randomPick(weights);
            results[result]++;
        }

        for(int i=0; i<weights.length; ++i) {
            Assert.assertEquals(weights[i]/weightSum, (double) results[i]/nbIterations, delta);
        }
    }

    @Test
    public void randomPickTest2() {
        Random random = new Random();
        int weightSize = random.nextInt(200);
        double[] weights = new double[weightSize];
        int[] results = new int[weightSize];
        double weightSum = 0;  
        for(int i=0; i<results.length; ++i) {
            results[i] = 0;
            weights[i] = random.nextDouble()*2f;
            weightSum += weights[i];
        }

        for(int i=0; i<nbIterations; ++i) {
            int result = RandomSelector.randomPick(weights);
            results[result]++;
        }

        for(int i=0; i<weights.length; ++i) {
            Assert.assertEquals(weights[i]/weightSum, (double)results[i]/nbIterations, delta);
        }
    }

}
