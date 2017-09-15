package oneclicklearning;

import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.realkd.AlgorithmLauncherRealKD;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class TestCache {
    //Tests realises avec RANK = 3
    List<? extends Pattern> listPatterns;
    List<? extends Pattern> listPatterns2;

    @Before
    public void setUp() throws Exception {
        AlgorithmLauncherRealKD algorithmLauncher = new AlgorithmLauncherRealKD("ressource/test.csv");
//        algorithmLauncher.importData("ressource/test.csv");
        AlgorithmLauncherRealKD algorithmLauncher2 = new AlgorithmLauncherRealKD("ressource/test.csv");
//        algorithmLauncher2.importData("ressource/test.csv");
        listPatterns = algorithmLauncher.startAlgorithm(0);
        listPatterns2 = algorithmLauncher2.startAlgorithm(0);
        for (int i=9; i>2; i--)
            listPatterns2.remove(i);
        for(int i=9; i>5; i--)
            listPatterns.remove(i);
        listPatterns.remove(0);
        listPatterns.remove(0);
        listPatterns.remove(0);
        Rank list1 = new Rank();
        Rank list2 = new Rank();
        Rank list3 = new Rank();
/*
        list1.add(listPatterns.get(0));
        list2.add(listPatterns.get(1));
        list3.add(listPatterns.get(2));



        System.out.println(listPatterns.get(0));
        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list1));

        System.out.println(listPatterns.get(1));
        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list2));

        System.out.println(listPatterns.get(2));
        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list3));

        list2.add(listPatterns.get(0));
//        System.out.println(list2);
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list2));

        list2.remove(1);
        list2.add(listPatterns.get(2));
//        System.out.println(list2);
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list2));

        list2.add(listPatterns.get(0));
        System.out.println(list2);
        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list2));
*/
        list1.add(listPatterns2.get(0));
        list2.add(listPatterns2.get(1));
        list3.add(listPatterns2.get(2));



//        System.out.println(listPatterns2.get(0));
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list1));

//        System.out.println(listPatterns2.get(1));
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list2));

//        System.out.println(listPatterns2.get(2));
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list3));

        list1.add(listPatterns2.get(1));
//        System.out.println(list1);
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list1));

        list1.add(listPatterns2.get(2));
//        System.out.println(list1);
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(list1));


//        CacheRanking.getInstance().addPatterns(listPatterns, 1, 0);
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(CacheRanking.getGreedyRanking()));
        //System.out.println(CacheRanking.getGreedyRanking());

//        CacheRanking.getInstance().addPatterns(listPatterns2, 2, 0);
        //System.out.println("nouveau greedy ranking:");
//        System.out.println(CoactiveLearningRanking.coactiveGetUtility(CacheRanking.getGreedyRanking()));



    }

 //   @Test
 //   public void getRankSize() throws Exception {
 //       assertNotEquals(9, CacheRanking.getRankSize());
 //       assertEquals(10, CacheRanking.getRankSize());
 //   }


    @Test
    public void addPatternsTest() throws Exception {
//        assertEquals(3, CacheRanking.getInstance().getGreedyRanking().size());
//        assertArrayEquals(listPatterns2.toArray(), CacheRanking.getGreedyRanking().toArray());
    }

}