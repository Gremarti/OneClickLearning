package fr.insa.ocm.model.utils.fastforward.condition;


import com.google.gson.annotations.Expose;
import com.sun.istack.internal.NotNull;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public abstract class Condition {

	public enum ActionPatternChoice {
		KEEP, NEUTRAL, TRASH;

		@Override
		public String toString() {
			switch(this){
				case KEEP:
					return "Keep";
				case NEUTRAL:
					return "Neutral";
				case TRASH:
					return "Trash";
				default:
					return "";
			}
		}
	}

	public enum ConditionType {
		CONDITION_ATTRIBUTE,
		CONDITION_ALGORITHM,
		CONDITION_MEASURE_STATIC,
		CONDITION_MEASURE_DYNAMIC,
		CONDITION_MEASURE_BETWEEN;

		@Override
		public String toString() {
			switch(this){
				case CONDITION_ATTRIBUTE:
					return "Condition Attribute";
				case CONDITION_ALGORITHM:
					return "Condition Algorithm";
				case CONDITION_MEASURE_STATIC:
					return "Condition Measure Static";
				case CONDITION_MEASURE_BETWEEN:
					return "Condition Measure Between";
				case CONDITION_MEASURE_DYNAMIC:
					return "Condition Measure Dynamic";
				default:
					return "";
			}
		}
	}

	@Expose private ActionPatternChoice actionPatternChoice;
	@Expose private ConditionType conditionType;

	/**
	 * For Deserialization purpose only.
	 */
	protected Condition(){}

	protected Condition(@NotNull ActionPatternChoice actionPatternChoice,
	                    @NotNull ConditionType conditionType){
		this.actionPatternChoice = actionPatternChoice;
		this.conditionType = conditionType;
	}

	public abstract boolean isMet(Pattern pattern, List<Pattern> listPattern);

	public ActionPatternChoice getActionPatternChoice(){ return actionPatternChoice; }

	public ConditionType getConditionType(){ return conditionType; }
}
