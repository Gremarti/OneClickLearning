package fr.insa.ocm.model.utils.serialize;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.oneclicklearning.algorithmmanager.AlgorithmManager;
import fr.insa.ocm.model.oneclicklearning.cache.api.Cache;
import fr.insa.ocm.model.oneclicklearning.cache.rank.CacheRanking;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.api.CoactiveLearning;
import fr.insa.ocm.model.oneclicklearning.coactivelearning.ranking.CoactiveLearningRanking;
import fr.insa.ocm.model.oneclicklearning.bandit.MultiArmedBandit;
import fr.insa.ocm.model.utils.PatternWarehouse;
import fr.insa.ocm.model.utils.SystemState;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public class SearchSave {

	@Expose private MultiArmedBandit bandit;
	@Expose	private Cache cache;
	@Expose private CoactiveLearning coactiveLearning;
	@Expose private PatternWarehouse patternWarehouse;
	@Expose private SystemState currentState;
	@Expose private boolean isInitialized;
	@Expose private boolean firstRanking;

	public SearchSave(){}

	//********** Saving Methods **********//

	public void setMultiArmedBandit(MultiArmedBandit bandit){
		this.bandit = bandit;
	}

	public void setCache(Cache cache){
		this.cache = cache;
	}

	public void setCoactiveLearning(CoactiveLearning coactiveLearning){
		this.coactiveLearning = coactiveLearning;
	}

	public void setPatternWarehouse(PatternWarehouse patternWarehouse){
		this.patternWarehouse = patternWarehouse;
	}

	public void setCurrentState(SystemState currentState){
		this.currentState = currentState;
	}

	public void setInitialized(boolean initialized) {
		isInitialized = initialized;
	}

	public void setFirstRanking(boolean firstRanking) {
		this.firstRanking = firstRanking;
	}

	//********** Loading Methods **********//

	public MultiArmedBandit getMultiArmedBandit(){
		return bandit;
	}

	public Cache getCache(){
		return cache;
	}

	public CoactiveLearning getCoactiveLearning(){
		return coactiveLearning;
	}

	public PatternWarehouse getPatternWarehouse(){
		return patternWarehouse;
	}

	public SystemState getCurrentState() {
		return currentState;
	}

	public boolean getInitialized() {
		return isInitialized;
	}

	public boolean getFirstRanking() {
		return firstRanking;
	}
}
