package fr.insa.ocm.model.utils;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public class SystemState {

    @Expose private Rank<Pattern> proposedRanking;
    @Expose private List<Pattern> interestingPatterns;
    @Expose private List<Pattern> neutralPatterns;
    @Expose private List<Pattern> trashedPatterns;

    public List<Pattern> getInterestingPatterns() {
        return interestingPatterns;
    }

    public List<Pattern> getNeutralPatterns() {
        return neutralPatterns;
    }

    public List<Pattern> getTrashedPatterns() {
        return trashedPatterns;
    }

    public SystemState(List<? extends Pattern> proposedRank) {
        proposedRanking = new Rank<>();
        this.proposedRanking.addAll(proposedRank);

    }

    public Rank<Pattern> getProposedRanking() {
        return proposedRanking;
    }

    public Rank<Pattern> getUserRanking() {
        Rank<Pattern> userRanking = new Rank<>();
        userRanking.addAll(interestingPatterns);
        userRanking.addAll(neutralPatterns);
        return userRanking;
    }

    private void setInterestingPatterns(List<Pattern> interestingPatterns) {
        this.interestingPatterns = interestingPatterns;
    }

    private void setNeutralPatterns(List<Pattern> neutralPatterns) {
        this.neutralPatterns = neutralPatterns;
    }

    private void setTrashedPatterns(List<Pattern> trashedPatterns) {
        this.trashedPatterns = trashedPatterns;
    }

    public void update(List<Pattern> newInterestingPattern,
                       List<Pattern> newNeutralPattern,
                       List<Pattern> newTrashedPattern){
        setInterestingPatterns(newInterestingPattern);
        setNeutralPatterns(newNeutralPattern);
        setTrashedPatterns(newTrashedPattern);
    }
}
