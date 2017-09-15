package fr.insa.ocm.model.oneclicklearning.cache.rank;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.oneclicklearning.cache.api.AbstractCache;
import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.utils.serialize.SearchSave;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

/**
 * <h1>CacheRanking</h1>
 * <p>Stocks the patterns while mining, and compute greedy ranking.</p>
 */
public class CacheRanking extends AbstractCache {

	// Patterns currently in cache
	@Expose private double utility;
	// Greedy ranking for the cache
	@Expose private Rank<Pattern> greedyRanking;
	// Do all the computation of the greedy ranking
	private static GreedyRankingComputer greedyComputer;

	/**
	 * Deserializing constructor.
	 * @param sizeListBestPatterns Deserializing parameter.
	 * @param cacheType Deserializing parameter.
	 * @param utility Deserializing parameter.
	 * @param greedyRanking Deserializing parameter.
	 */
	public CacheRanking(int sizeListBestPatterns,
	                    CacheType cacheType,
						double utility,
	                    Rank<Pattern> greedyRanking){
		super(sizeListBestPatterns, cacheType);

		this.utility = utility;
		this.greedyRanking = greedyRanking;

		greedyComputer = new GreedyRankingComputer(this);
		greedyComputer.start();
	}

	/**
	 * Creates a new CacheRanking instance.
	 */
	public CacheRanking(){
		super(20, CacheType.RANKING);

		greedyRanking = new Rank<>();
		utility = 0;

		greedyComputer = new GreedyRankingComputer(this);
		greedyComputer.start();
	}

	//********** Serializing Methods **********//

	public void reload(CacheRanking cache){
		this.utility = cache.utility;
		this.greedyRanking = cache.greedyRanking;

		greedyComputer = new GreedyRankingComputer(this);
		greedyComputer.start();
	}

	public void save(SearchSave searchSave){
		searchSave.setCache(this);
	}

	//********** Public Methods **********//

	@Override
	public List<Pattern> getBestPattern() {
		greedyRanking = greedyComputer.getGreedyRanking();

		return new Rank<>(greedyRanking);
	}

	/**
	 * Adds patterns from a given mining algorithm to the cache.
	 * @param newPatterns The patterns from the mining algorithm
	 * @param time The computation time of the mining algorithm
	 * @param arm The index of the mining algorithm
	 */
	public void addPatterns(List<Pattern> newPatterns, double time, int arm){
		greedyComputer.addPatterns(newPatterns, time, arm);
	}

	@Deprecated
	public void requestStop(){
		greedyComputer.requestedStop();
	}
}


