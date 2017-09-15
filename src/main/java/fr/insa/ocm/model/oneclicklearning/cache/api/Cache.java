package fr.insa.ocm.model.oneclicklearning.cache.api;

import fr.insa.ocm.model.oneclicklearning.cache.rank.CacheRanking;
import fr.insa.ocm.model.oneclicklearning.cache.set.CacheSet;
import fr.insa.ocm.model.wrapper.api.Pattern;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Cache {

	enum CacheType{
		RANKING, SET;

		@NotNull
		public Cache newInstance(){
			switch (this){
				case RANKING:
					return new CacheRanking();
				case SET:
					return new CacheSet();
				default:
					return new CacheSet();
			}
		}

		@Override
		public String toString() {
			switch (this){
				case RANKING:
					return "Ranking";
				case SET:
					return "Set";
				default:
					return "";
			}
		}
	}

	List<Pattern> getBestPattern();

	void addPatterns(List<Pattern> newPatterns, double time, int arm);

	int getSizeListBestPatterns();

	void setSizeListBestPatterns(int size);
}
