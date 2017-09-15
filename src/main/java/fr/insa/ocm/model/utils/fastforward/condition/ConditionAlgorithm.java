package fr.insa.ocm.model.utils.fastforward.condition;

import com.google.gson.annotations.Expose;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.wrapper.api.Pattern;

import java.util.List;

public class ConditionAlgorithm extends Condition {

	public enum AlgorithmState{
		CREATEDBY, NOT_CREATEDBY;

		@Override
		public String toString() {
			switch(this){
				case CREATEDBY:
					return "has been created";
				case NOT_CREATEDBY:
					return "has not been created";
				default:
					return "";
			}
		}
	}

	@Expose private AlgorithmState algorithmState;
	@Expose private String algorithmName;

	/**
	 * For Deserialization purpose only.
	 */
	public ConditionAlgorithm(){}

	public ConditionAlgorithm(@NotNull ActionPatternChoice actionPatternChoice,
	                          String algorithmName,
	                          AlgorithmState algorithmState){
		super(actionPatternChoice, ConditionType.CONDITION_ALGORITHM);

		this.algorithmState = algorithmState;
		this.algorithmName = algorithmName;
	}

	@Override
	public boolean isMet(Pattern pattern, @Nullable List<Pattern> listPattern) {
		return (pattern.getAlgorithmName().equals(algorithmName) && algorithmState.equals(AlgorithmState.CREATEDBY))
				|| (!pattern.getAlgorithmName().equals(algorithmName) && algorithmState.equals(AlgorithmState.NOT_CREATEDBY));
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	public AlgorithmState getAlgorithmState() {
		return algorithmState;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}
}
