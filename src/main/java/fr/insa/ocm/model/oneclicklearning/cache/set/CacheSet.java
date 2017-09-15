package fr.insa.ocm.model.oneclicklearning.cache.set;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.cache.api.AbstractCache;
import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.*;


public class CacheSet extends AbstractCache {

	private class PatternEntry{

		double utility;
		Pattern pattern;

		PatternEntry(Pattern pattern, double utility){
			this.pattern = pattern;
			this.utility = utility;
		}

		// Sorting desc
		int compare(PatternEntry patternEntry){
			double r = this.utility - patternEntry.utility;
			if(r < 0){
				return 1;
			} else if(r > 0){
				return -1;
			}
			return 0;
		}

		@Override
		public String toString(){
			return "" + utility;
		}
	}

	// Stocks the best patterns of each arms.
	private Map<Integer, List<PatternEntry>> cachePattern;

	// Stocks the previous utility for each arms.
	private Map<Integer, Double> previousUtility;

	/**
	 * Deserializing constructor.
	 * @param sizeListBestPatterns Deserializing parameter.
	 * @param cacheType Deserializing parameter.
	 */
	public CacheSet(int sizeListBestPatterns,
	                CacheType cacheType){
		super(sizeListBestPatterns, cacheType);

		this.previousUtility = previousUtility;

		cachePattern = Collections.synchronizedMap(new HashMap<>());
	}

	public CacheSet(){
		super(20, CacheType.SET);

		cachePattern = Collections.synchronizedMap(new HashMap<>());
		previousUtility = new HashMap<>();
	}

	@Override
	public List<Pattern> getBestPattern() {
		List<Pattern> listPattern = new ArrayList<>();
		List<PatternEntry> intermediateList = new ArrayList<>();
		final double[] weightsBandit = OCMManager.banditGetWeights();

		cachePattern.forEach((integer, patternEntries) -> {
			int numberPattern = (int) (weightsBandit[integer]*sizeListBestPatterns);
			numberPattern = numberPattern==0 ? 1 : numberPattern;

			intermediateList.addAll(patternEntries.subList(0, numberPattern));
		});

		intermediateList.sort(PatternEntry::compare);
		intermediateList.forEach(patternEntry -> listPattern.add(patternEntry.pattern));

		cachePattern.clear();
		return new Rank<>(listPattern);
	}

	// TODO -- Find how useful time could be ?
	public void addPatterns(List<Pattern> newPatterns, double time, int arm){
		if(!cachePattern.containsKey(arm)){
			List<PatternEntry> listPatternEntry = new ArrayList<>();
			Rank<Pattern> rank = new Rank<>();

			for(Pattern pattern : newPatterns){
				rank.add(pattern);

				double utility = OCMManager.coactiveGetUtility(rank);
				PatternEntry entry = new PatternEntry(pattern, utility);
				listPatternEntry.add(entry);

				rank.clear();
			}

			// Get the best patterns.
			listPatternEntry.sort(PatternEntry::compare);
			listPatternEntry = listPatternEntry.subList(0, sizeListBestPatterns);

			// Calculate the reward.
			List<Pattern> listPatterns = new ArrayList<>();
			listPatternEntry.forEach(patternEntry -> listPatterns.add(patternEntry.pattern));

			double newUtility = OCMManager.coactiveGetUtility(listPatterns);
			double oldUtility = previousUtility.getOrDefault(arm, newUtility);

			double reward = (newUtility - oldUtility) * 2.5 ;
			System.out.println("(arm, reward) = (" + arm + ", " + reward + ")");
			System.out.println("\tOld utility: "+oldUtility+"\n\tNew utility: "+newUtility);

			OCMManager.algorithmManagerUpdateReward(arm, reward);

			// Update informations
			cachePattern.put(arm, listPatternEntry);
			previousUtility.put(arm, newUtility);
		}
	}
}
