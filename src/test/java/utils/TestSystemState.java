package utils;

import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.realkd.AlgorithmLauncherRealKD;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

public class TestSystemState {
    SystemState firstSystemState;
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
        interestingPatterns.add(listPatterns.get(2));
        interestingPatterns.add(listPatterns.get(3));
        interestingPatterns.add(listPatterns.get(5));

        neutralPatterns = new Rank();
        neutralPatterns.add(listPatterns.get(1));
        neutralPatterns.add(listPatterns.get(7));
        neutralPatterns.add(listPatterns.get(9));

        trashedPatterns = new Rank();
        trashedPatterns.add(listPatterns.get(4));
        trashedPatterns.add(listPatterns.get(6));
        trashedPatterns.add(listPatterns.get(8));

        userRanking = new Rank();
        userRanking.add(listPatterns.get(0));
        userRanking.add(listPatterns.get(2));
        userRanking.add(listPatterns.get(3));
        userRanking.add(listPatterns.get(5));
        userRanking.add(listPatterns.get(1));
        userRanking.add(listPatterns.get(7));
        userRanking.add(listPatterns.get(9));

        /*firstSystemState.update(interestingPatterns,neutralPatterns,trashedPatterns);

        userRanking = firstSystemState.getUserRanking();*/

    }

    @Test
    public void systemStateConstructorTest() {
        firstSystemState = new SystemState(listPatterns);
        assertArrayEquals(firstSystemState.getProposedRanking().toArray(),listPatterns.toArray());
    }

    @Test
    public void updateTest(){
        firstSystemState = new SystemState(listPatterns);
        firstSystemState.update(interestingPatterns,neutralPatterns,trashedPatterns);
        assertArrayEquals(firstSystemState.getInterestingPatterns().toArray(),interestingPatterns.toArray());
        assertArrayEquals(firstSystemState.getNeutralPatterns().toArray(),neutralPatterns.toArray());
        assertArrayEquals(firstSystemState.getTrashedPatterns().toArray(),trashedPatterns.toArray());
    }

    @Test
    public void updateEmptySystemStateTest(){
        firstSystemState = new SystemState(new ArrayList<>());
        firstSystemState.update(new Rank(),new Rank(),new Rank());
    }

    @Test
    public void getUserRankingTest(){
        firstSystemState = new SystemState(listPatterns);
        firstSystemState.update(interestingPatterns,neutralPatterns,trashedPatterns);
        firstSystemState.getUserRanking();
        System.out.println(firstSystemState.getUserRanking());
        assertArrayEquals(firstSystemState.getUserRanking().toArray(),userRanking.toArray());
    }
}
