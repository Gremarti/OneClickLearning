package fr.insa.ocm.model.utils;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/***
 * This class should keep all the patterns the user has found interesting
 */
public class PatternWarehouse {

	@Expose private List<Pattern> patterns;

	public PatternWarehouse(List<Pattern> listPatterns){
		patterns = listPatterns;
	}

	public PatternWarehouse(){
		patterns = new ArrayList<>();
	}

	//********** Public Methods **********//

	public synchronized List<Pattern> getStockedPatterns(){
		List<Pattern> stockedPatterns = new ArrayList<>();

		patterns.forEach(pattern -> stockedPatterns.add(pattern.copy()));

		return stockedPatterns;
	}

	public synchronized void addToWarehouse(Collection<Pattern> collection){
		collection.forEach(pattern -> {
			if(!patterns.contains(pattern)){
				patterns.add(pattern);
			}
		});
	}

	private synchronized void emptyWarehouse(){ patterns.clear(); }
}
