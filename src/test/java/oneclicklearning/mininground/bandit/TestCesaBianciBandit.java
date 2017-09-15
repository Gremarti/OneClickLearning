package oneclicklearning.mininground.bandit;

import fr.insa.ocm.model.oneclicklearning.bandit.CesaBianciBandit;
import org.junit.Assert;
import org.junit.Test;

public class TestCesaBianciBandit {

    private final double delta = 0.000000001;

    @Test
    public void getBetaTest() {
        Assert.assertEquals(0.6786140424, new CesaBianciBandit(10).getBeta(), delta);
        Assert.assertEquals(0.4192017181, new CesaBianciBandit(33).getBeta(), delta);
    }

    @Test
    public void getMixtureCoefficientTest() {
        Assert.assertEquals(5.714285714, new CesaBianciBandit(10).getMixtureCoefficient(0.5), delta);
        Assert.assertEquals(3.871266095, new CesaBianciBandit(10).getMixtureCoefficient(0.321456), delta);
    }

    @Test
    public void getLearningRateTest() {
        Assert.assertEquals(0.175, new CesaBianciBandit(10).getLearningRate(3.5), delta);
    }

    @Test
    public void getSelectionDistributionTest() {
        double[] pi = new CesaBianciBandit(10).getSelectionDistribution(7.379018666);
        for (double val : pi) {
            Assert.assertEquals(0.1, val, delta);
        }
    }

    private final double delta2 = 0.0000001;
    @Test
    public void updateRewardTest() {
        CesaBianciBandit bandit = new CesaBianciBandit(10);
        bandit.updateReward(3,0.15);
        double[] weights = bandit.getWeights();
        for(int i=0; i<10; ++i) {
            if (i == 3) {
                Assert.assertEquals(21.26748210, weights[i], delta2);
            }
            else {
                Assert.assertEquals(12.2282988, weights[i], delta2);
            }
        }
    }

    private final double deltaGlobal = 0.01;
    @Test
    public void globalTest() {
        CesaBianciBandit bandit = new CesaBianciBandit(3);
        double beta = bandit.getBeta();
        Assert.assertEquals(1.06, beta, deltaGlobal);
        double gamma = bandit.getMixtureCoefficient(beta);
        Assert.assertEquals(3.14, gamma, deltaGlobal);
        double eta = bandit.getLearningRate(gamma);
        Assert.assertEquals(0.52, eta, deltaGlobal);
        double[] pi = bandit.getSelectionDistribution(gamma);
        for(double p : pi) {
            Assert.assertEquals(0.33, p, deltaGlobal);
        }
        bandit.updateReward(0, 0.2);
        double[] v = bandit.getWeights();
        Assert.assertEquals(7.29, v[0], deltaGlobal);
        Assert.assertEquals(5.33, v[1], deltaGlobal);
        Assert.assertEquals(5.33, v[2], deltaGlobal);
    }

//    You need to uncomment the setter on round in the CesaBianciBandit since it's only for test.
//    @Test
//    public void betaRoundsTest() {
//        CesaBianciBandit bandit = new CesaBianciBandit(1);
//        for(int i=1; i<10; ++i) {
//            bandit.setRound(i);
//            Assert.assertEquals(1.51, bandit.getBeta(), deltaGlobal);
//        }
//        for(int i=10; i<100; ++i) {
//            bandit.setRound(i);
//            Assert.assertEquals(1.07, bandit.getBeta(), deltaGlobal);
//        }
//        for(int i=100; i<1000; ++i) {
//            bandit.setRound(i);
//            Assert.assertEquals(0.75, bandit.getBeta(), deltaGlobal);
//        }
//        for(int i=1000; i<10000; ++i) {
//            bandit.setRound(i);
//            Assert.assertEquals(0.53, bandit.getBeta(), deltaGlobal);
//        }
//    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void updateRewardsOutOfBoundTest() {
        new CesaBianciBandit(10).updateReward(20, 0);
    }
}