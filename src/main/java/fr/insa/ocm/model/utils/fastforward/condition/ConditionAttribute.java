package fr.insa.ocm.model.utils.fastforward.condition;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public class ConditionAttribute extends Condition {

	public enum AttributeState{
		PRESENT, ABSENT;

		@Override
		public String toString() {
			switch(this){
				case PRESENT:
					return "present";
				case ABSENT:
					return "absent";
				default:
					return "";
			}
		}
	}

	@Expose private String attributeName;
	@Expose private AttributeState attributeState;

	/**
	 * For Deserialization purpose only.
	 */
	public ConditionAttribute(){}

	public ConditionAttribute(ActionPatternChoice actionPatternChoice,
	                          String attributeName,
	                          AttributeState attributeState){
		super(actionPatternChoice, ConditionType.CONDITION_ATTRIBUTE);

		this.attributeName = attributeName;
		this.attributeState = attributeState;
	}

	@Override
	public boolean isMet(Pattern pattern, List<Pattern> listPattern) {
		return this.isMet(pattern);
	}

	private boolean isMet(Pattern pattern) {
		boolean isMet = false;
		if(attributeState.equals(AttributeState.ABSENT)){
			isMet = true;
		}

		// Search in all the attribute names of the pattern.
		// If the attribute should be absent to met the condition, if there is no such attribute in the pattern, isMet will remain true.
		// If the attribute should be present to met the condition, if there is a such attribute in the pattern, isMet will switch from false to true once.
		for (String patternAttributeName : pattern.getListAttributeNames()) {
			if(patternAttributeName.equals(attributeName)){
				isMet = !isMet;
				break;
			}
		}
		return isMet;
	}

	//********** Getters/Setters Methods **********//

	public String getAttributeName() {
		return attributeName;
	}

	public AttributeState getAttributeState() {
		return attributeState;
	}
}
