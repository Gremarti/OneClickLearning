package fr.insa.ocm.model.utils.fastforward.condition;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.wrapper.api.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ConditionMeasureBetween extends Condition {

	public enum IntervalChoice{
		INSIDE_IN_IN, INSIDE_IN_OUT, INSIDE_OUT_IN, INSIDE_OUT_OUT,
		OUTSIDE_IN_IN, OUTSIDE_IN_OUT, OUTSIDE_OUT_IN, OUTSIDE_OUT_OUT;

		@Override
		public String toString() {
			switch(this){
				case INSIDE_IN_IN:
					return "is between [X;Y]";
				case INSIDE_IN_OUT:
					return "is between [X;Y[";
				case INSIDE_OUT_IN:
					return "is between ]X;Y]";
				case INSIDE_OUT_OUT:
					return "is between ]X;Y[";
				case OUTSIDE_IN_IN:
					return "is not between [X;Y]";
				case OUTSIDE_IN_OUT:
					return "is not between [X;Y[";
				case OUTSIDE_OUT_IN:
					return "is not between ]X;Y]";
				case OUTSIDE_OUT_OUT:
					return "is not between ]X;Y[";
				default:
					return "Oh no D:";
			}
		}
	}

	@Expose private ConditionMeasureStatic lowerBound;
	@Expose private ConditionMeasureStatic higerBound;
	@Expose private IntervalChoice intervalChoice;

	public ConditionMeasureBetween(){}

	public ConditionMeasureBetween(@NotNull ActionPatternChoice actionPatternChoice,
	                               Pattern.MeasureType measureType,
	                               IntervalChoice intervalChoice, double thresholdValueLowerBound, double thresholdValueHigherBound){
		super(actionPatternChoice, ConditionType.CONDITION_MEASURE_BETWEEN);

		ConditionMeasureStatic.OperatorType operatorTypeLowerBound = null;
		ConditionMeasureStatic.OperatorType operatorTypeHigherBound = null;

		switch(intervalChoice){
			case INSIDE_IN_IN:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.GET;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.LET;
				break;
			case INSIDE_IN_OUT:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.GET;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.LT;
				break;
			case INSIDE_OUT_IN:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.GT;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.LET;
				break;
			case INSIDE_OUT_OUT:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.GT;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.LT;
				break;
			case OUTSIDE_IN_IN:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.LET;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.GET;
				break;
			case OUTSIDE_IN_OUT:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.LET;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.GT;
				break;
			case OUTSIDE_OUT_IN:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.LT;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.GET;
				break;
			case OUTSIDE_OUT_OUT:
				operatorTypeLowerBound = ConditionMeasureStatic.OperatorType.LT;
				operatorTypeHigherBound = ConditionMeasureStatic.OperatorType.GT;
				break;
		}

		this.intervalChoice = intervalChoice;
		this.lowerBound = new ConditionMeasureStatic(actionPatternChoice, measureType, operatorTypeLowerBound, thresholdValueLowerBound);
		this.higerBound = new ConditionMeasureStatic(actionPatternChoice, measureType, operatorTypeHigherBound, thresholdValueHigherBound);
	}

	@Override
	public boolean isMet(Pattern pattern, List<Pattern> listPattern) {
		switch(intervalChoice){
			case INSIDE_IN_IN:
			case INSIDE_IN_OUT:
			case INSIDE_OUT_IN:
			case INSIDE_OUT_OUT:
				return lowerBound.isMet(pattern, listPattern) && higerBound.isMet(pattern, listPattern);
			case OUTSIDE_IN_IN:
			case OUTSIDE_IN_OUT:
			case OUTSIDE_OUT_IN:
			case OUTSIDE_OUT_OUT:
				return lowerBound.isMet(pattern, listPattern) || higerBound.isMet(pattern, listPattern);
			default:
				return false;
		}
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	public ConditionMeasureStatic getHigerBound() {
		return higerBound;
	}

	public ConditionMeasureStatic getLowerBound() {
		return lowerBound;
	}

	public IntervalChoice getIntervalChoice() {
		return intervalChoice;
	}

	public Pattern.MeasureType getMeasureType(){ return lowerBound.getMeasure(); }
}
