package fr.insa.ocm.model.utils.fastforward.condition;


import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public class ConditionMeasureStatic extends Condition {

	public enum OperatorType{
		LT, LET, EQL, GET, GT;

		@Override
		public String toString() {
			String result;
			if(this == LT){
				result = "<";
			}else if(this == LET){
				result = "<=";
			}else if(this == EQL){
				result = "=";
			}else if(this == GET){
				result = ">=";
			}else{
				result = ">";
			}
			return result;
		}

		//TODO Javadoc, dire qu'on fait value1 Operation value2 et qu'on vérifie si c'est vrai
		public boolean isValid(double value1, double value2){
			switch (this){
				case LT:
					return value1 < value2;
				case LET:
					return value1 <= value2;
				case EQL:
					return value1 == value2;
				case GET:
					return value1 >= value2;
				case GT:
					return value1 > value2;
				default:
					return false;
			}
		}
	}

	@Expose private Pattern.MeasureType measure;
	@Expose private OperatorType operator;
	@Expose private double thresholdValue;

	/**
	 * For Deserialization purpose only.
	 */
	public ConditionMeasureStatic(){}

	public ConditionMeasureStatic(ActionPatternChoice actionPatternChoice,
	                              Pattern.MeasureType measure,
	                              OperatorType operator,
	                              double thresholdValue){
		super(actionPatternChoice, ConditionType.CONDITION_MEASURE_STATIC);

		this.measure = measure;
		this.operator = operator;
		this.thresholdValue = thresholdValue;
	}

	@Override
	public boolean isMet(Pattern pattern, List<Pattern> listPattern) {
		return this.isMet(pattern);
	}

	private boolean isMet(Pattern pattern) {
		return operator.isValid(pattern.getMeasureValue(measure), thresholdValue);
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	public Pattern.MeasureType getMeasure() {
		return measure;
	}

	public OperatorType getOperator() {
		return operator;
	}

	public double getThresholdValue() {
		return thresholdValue;
	}
}
