package fr.insa.ocm.model.utils.fastforward.condition;

import com.google.gson.annotations.Expose;
import com.sun.istack.internal.NotNull;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.ArrayList;
import java.util.List;

public class ConditionMeasureDynamic extends Condition {

	public enum CatogoryType{
		HIGHEST, LOWEST;

		@Override
		public String toString() {
			switch(this){
				case HIGHEST:
					return "highest";
				case LOWEST:
					return "lowest";
				default:
					return "";
			}
		}
	}

	@Expose private Pattern.MeasureType measure;
	@Expose private CatogoryType catogory;
	@Expose private int index;

	/**
	 * For Deserialization purpose only.
	 */
	public ConditionMeasureDynamic(){}

	public ConditionMeasureDynamic(@NotNull ActionPatternChoice actionPatternChoice,
	                               Pattern.MeasureType measure,
	                               CatogoryType catogory,
	                               int index){
		super(actionPatternChoice, ConditionType.CONDITION_MEASURE_DYNAMIC);

		this.measure = measure;
		this.catogory = catogory;
		this.index = index;
	}

	@Override
	public boolean isMet(Pattern pattern, List<Pattern> listPattern) {
		double measureValue = this.getMeasureFromPattern(pattern);
		double measureThresholdValue;
		boolean conditionIsMet = false;
		List<Pattern> sortedListPattern = this.getSortedListPattern(listPattern);

		switch(catogory){
			case HIGHEST:
				measureThresholdValue = this.getMeasureFromPattern(sortedListPattern.get(index));
				conditionIsMet = measureValue >= measureThresholdValue;
				break;
			case LOWEST:
				measureThresholdValue = this.getMeasureFromPattern(sortedListPattern.get(sortedListPattern.size() - index));
				conditionIsMet = measureValue <= measureThresholdValue;
				break;
		}

		return conditionIsMet;
	}

	private double getMeasureFromPattern(Pattern pattern){
		return pattern.getMeasureValue(measure);
	}

	private List<Pattern> getSortedListPattern(List<Pattern> listPattern){
		List<Pattern> sortedListPattern = new ArrayList<>(listPattern);

		sortedListPattern.sort((pattern1, pattern2) -> {
			double measurePattern1 = this.getMeasureFromPattern(pattern1);
			double measurePattern2 = this.getMeasureFromPattern(pattern2);
			double difference = measurePattern1 - measurePattern2;
			if(difference < 0){
				return 1;
			}else if(difference > 0){
				return -1;
			}else{
				return 0;
			}
		});

		return sortedListPattern;
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	public Pattern.MeasureType getMeasure() {
		return measure;
	}

	public CatogoryType getCatogory() {
		return catogory;
	}

	public int getIndex() {
		return index;
	}
}
