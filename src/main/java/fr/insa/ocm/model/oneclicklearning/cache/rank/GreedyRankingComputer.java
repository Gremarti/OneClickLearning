package fr.insa.ocm.model.oneclicklearning.cache.rank;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.cache.api.Cache;
import fr.insa.ocm.model.utils.Rank;
import fr.insa.ocm.model.wrapper.api.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class GreedyRankingComputer extends Thread {

	private class GreedyCalculus implements Runnable{
		private Pattern bestPattern;
		private double bestUtility;
		private List<Pattern> candidatePatterns;
		private Rank<Pattern> greedyRankingCurrent;

		GreedyCalculus(@NotNull final Rank<Pattern> greedyRankingCurrent,
		               @NotNull List<Pattern> candidatePatterns){
			this.bestPattern = null;
			this.bestUtility = -1d;
			this.candidatePatterns = candidatePatterns;
			this.greedyRankingCurrent = new Rank<>(greedyRankingCurrent);
		}

		@Nullable
		Pattern getBestPattern(){
			return bestPattern;
		}

		double getBestUtility(){
			return bestUtility;
		}

		@Override
		public void run() {
			for (Pattern p : candidatePatterns) {
				greedyRankingCurrent.add(p);
				double utility = OCMManager.coactiveGetUtility(greedyRankingCurrent);
				if (utility > bestUtility) {
					this.bestPattern = p;
					this.bestUtility = utility;
				}
				greedyRankingCurrent.remove(p);
			}

		}
	}

	private class AlgorithmPatternResults {
		int arm;
		double time;
		List<Pattern> listFoundPatterns;

		AlgorithmPatternResults(int arm, double time, List<Pattern> listFoundPatterns){
			this.arm = arm;
			this.time = time;
			this.listFoundPatterns = listFoundPatterns;
		}
	}

	private volatile boolean stopRequested;

	private final List<AlgorithmPatternResults> queuedPatterns = new ArrayList<>();
	private final Rank<Pattern> greedyRanking = new Rank<>();
	private double rankingUtility;
	private CacheRanking cache;

	private final Set<Integer> armsPulled = new HashSet<>();

	GreedyRankingComputer(@NotNull CacheRanking cache){
		this.cache = cache;

		stopRequested = false;
		rankingUtility = 0;

		this.setDaemon(true);
		this.setName("Greedy Computer");
	}

	@Override
	public void run(){
		List<Pattern> candidatePatterns = new ArrayList<>();
		AlgorithmPatternResults currentCandidate = new AlgorithmPatternResults(-1, -1, new ArrayList<>());

		try {
			while (!stopRequested) {
				synchronized (queuedPatterns) {
					if (queuedPatterns.size() != 0) {
						currentCandidate = queuedPatterns.get(0);
						candidatePatterns.addAll(currentCandidate.listFoundPatterns);
						queuedPatterns.remove(0);
					}
				}

				try {
					if (!candidatePatterns.isEmpty()) {

						// The Patterns in the greedy ranking needs to be treated as simple Patterns to establish a new ranking.
						candidatePatterns.addAll(greedyRanking);

						int greedySize = Math.min(cache.getSizeListBestPatterns(), candidatePatterns.size());
						Rank<Pattern> greedyRankingTmp = new Rank<>();

						for (int i = 0; i < greedySize || candidatePatterns.isEmpty(); ++i) {
							Pattern bestPattern;
							double bestUtility;

							Thread[] greedyComputation = new Thread[8];
							GreedyCalculus[] greedyCalculus = new GreedyCalculus[greedyComputation.length];

							for (int j = 0; j < greedyComputation.length; ++j) {
								List<Pattern> subList = candidatePatterns.subList((j / greedyComputation.length) * candidatePatterns.size(), ((j + 1) / greedyComputation.length) * candidatePatterns.size());

								greedyCalculus[j] = new GreedyCalculus(greedyRankingTmp, subList);

								greedyComputation[j] = new Thread(greedyCalculus[j]);
								greedyComputation[j].setName("GreedyCalculus #" + j);
								greedyComputation[j].start();
							}

							for (Thread threadGreedy : greedyComputation) {
								try {
									threadGreedy.join();
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}

							bestPattern = greedyCalculus[0].getBestPattern();
							bestUtility = greedyCalculus[0].getBestUtility();

							for (int j = 1; j < greedyComputation.length; ++j) {
								if (bestUtility < greedyCalculus[j].getBestUtility()) {
									bestPattern = greedyCalculus[j].getBestPattern();
									bestUtility = greedyCalculus[j].getBestUtility();
								}
							}

							if (bestPattern == null) {
								throw new NullPointerException();
							}

							if (!greedyRankingTmp.contains(bestPattern)) {
								greedyRankingTmp.add(bestPattern);
							}
							candidatePatterns.remove(bestPattern); //It is ok because the cache will be resized with the greedyRankingCurrent

						}

						// Update the MultiArmedBandit
						double newUtility = OCMManager.coactiveGetUtility(greedyRankingTmp);
//					double reward = (newUtility - rankingUtility)/currentCandidate.time;
						double reward = (newUtility - rankingUtility) * (5 / currentCandidate.time);

						//TODO Find a better way to calculate the reward.
						System.out.println("(arm, reward) = (" + currentCandidate.arm + ", " + reward + ")");
						OCMManager.algorithmManagerUpdateReward(currentCandidate.arm, reward);

						rankingUtility = newUtility;

						synchronized (greedyRanking) {
							greedyRanking.clear();
							greedyRanking.addAll(greedyRankingTmp);
						}

					} else {
						Thread.sleep(1000);
					}
				} catch (NullPointerException e){
					DebugLogger.printDebug("GreedyRankingComputer: One of the best pattern found was null.", DebugLogger.MessageSeverity.HIGH);
				}

				candidatePatterns.clear();

			}
		} catch (InterruptedException e){
			e.printStackTrace();
		}
	}

	void addPatterns(List<Pattern> newPatterns, double time, int arm){
		AlgorithmPatternResults patternResults = new AlgorithmPatternResults(arm, time, newPatterns);
		synchronized (armsPulled){
			// We don't want to see each time the same algorithms results (only for SPMF)
			if(OCMManager.getCurrentUsedWrapperType().equals(Pattern.WrapperType.SPMF)
					&& armsPulled.contains(arm)){
				return;
			}
		}

		synchronized (queuedPatterns){
			if(queuedPatterns.size() <= 50) {
				queuedPatterns.add(patternResults);
				synchronized (armsPulled){
					armsPulled.add(arm);
				}
			}
		}
	}

	Rank<Pattern> getGreedyRanking(){
		Rank<Pattern> greedyRankingResult;

		synchronized (greedyRanking){
			greedyRankingResult = new Rank<>(greedyRanking);
		}

		synchronized (armsPulled){
			armsPulled.clear();
		}

		return greedyRankingResult;
	}

	void requestedStop(){
		stopRequested = true;
	}

}
