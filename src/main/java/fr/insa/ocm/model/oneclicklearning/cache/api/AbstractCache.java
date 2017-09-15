package fr.insa.ocm.model.oneclicklearning.cache.api;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public abstract class AbstractCache implements Cache {

	@Expose protected int sizeListBestPatterns;
	@Expose private CacheType cacheType;

	/**
	 * Deserializing constructor.
	 * @param sizeListBestPatterns Deserializing parameter.
	 * @param cacheType Deserializing parameter.
	 */
	protected AbstractCache(int sizeListBestPatterns,
	                     CacheType cacheType){
		this.sizeListBestPatterns = sizeListBestPatterns;
		this.cacheType = cacheType;
	}

	@Override
	public abstract List<Pattern> getBestPattern();

	@Override
	public abstract void addPatterns(List<Pattern> newPatterns, double time, int arm);

	@Override
	public int getSizeListBestPatterns() {
		return sizeListBestPatterns;
	}

	@Override
	public void setSizeListBestPatterns(int size) {
		if(size > 0){
			sizeListBestPatterns = size;
		}
	}
}
