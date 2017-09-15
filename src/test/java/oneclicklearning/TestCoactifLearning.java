package oneclicklearning;

import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.realkd.AlgorithmLauncherRealKD;

import org.junit.Before;
import org.junit.Test;


import java.util.ArrayList;
import java.util.List;

public class TestCoactifLearning {


    SystemState firstSystemState;
    SystemState emptySystemState;
    List<? extends Pattern> listPatterns;

    Rank interestingPatterns;
    Rank neutralPatterns;
    Rank trashedPatterns;
    Rank userRanking;
    @Before
    public void initData(){
        //On génère des patterns grace a nos algorithmes de fouille de données
        AlgorithmLauncherRealKD algorithmLauncher = new AlgorithmLauncherRealKD("ressource/data_people.csv");
//        algorithmLauncher.importData("ressource/data_people.csv");
        listPatterns = algorithmLauncher.startAlgorithm(0);
        firstSystemState = new SystemState(listPatterns);


        /********************************** Retour utilisateur ****************************************/
        interestingPatterns = new Rank();
        interestingPatterns.add(listPatterns.get(0));
        //interestingPatterns.add(listPatterns.get(2));
        interestingPatterns.add(listPatterns.get(3));
        //interestingPatterns.add(listPatterns.get(5));

        neutralPatterns = new Rank();
        neutralPatterns.add(listPatterns.get(1));
        neutralPatterns.add(listPatterns.get(7));
        //neutralPatterns.add(listPatterns.get(9));

        trashedPatterns = new Rank();
        trashedPatterns.add(listPatterns.get(4));
        trashedPatterns.add(listPatterns.get(6));
        trashedPatterns.add(listPatterns.get(8));

        firstSystemState.update(interestingPatterns,neutralPatterns,trashedPatterns);

        userRanking = firstSystemState.getUserRanking();

    }

    @Test
    public void auxiliaryFunctionPhiTest() {
        //System.out.println(userRanking);
        System.out.println(firstSystemState.getProposedRanking());
        //CoactiveLearningRanking.auxiliaryFunctionPhi(firstSystemState.getProposedRanking());
//        System.out.println(CoactiveLearningRanking.getInstance().auxiliaryFunctionPhi(firstSystemState.getProposedRanking()));
        //System.out.println(firstSystemState.getUserRanking());
        //System.out.println(CoactiveLearningRanking.auxiliaryFunctionPhi(firstSystemState.getProposedRanking()));

    }

    @Test
    public void getUtilityFunctionTest(){
        System.out.println(firstSystemState.getProposedRanking());
//        System.out.println(CoactiveLearningRanking.getInstance().getUtility(firstSystemState.getProposedRanking()));

    }

    @Test
    public void updateWeightsTest(){
        System.out.println("-----------------------Proposed Ranking-------------------------");
        System.out.println(firstSystemState.getProposedRanking());
        System.out.println("-----------------------User Ranking-------------------------");
        System.out.println(userRanking);
        //System.out.println(CoactiveLearningRanking.auxiliaryFunctionPhi(userRanking));
        //System.out.println(CoactiveLearningRanking.coactiveGetUtility(userRanking));
//        CoactiveLearningRanking.getInstance().updateWeight(firstSystemState);


    }

    @Test
    public void updateWeightsEmptySystemStateTest(){
        emptySystemState = new SystemState(new ArrayList<Pattern>());
//        CoactiveLearningRanking.getInstance().updateWeight(emptySystemState);

    }
}
