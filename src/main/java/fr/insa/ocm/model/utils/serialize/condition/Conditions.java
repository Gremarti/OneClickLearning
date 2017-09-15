package fr.insa.ocm.model.utils.serialize.condition;

import com.google.gson.annotations.Expose;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;

import java.util.List;

public class Conditions {

	@Expose private List<Condition> conditions;

	public List<Condition> getConditions() {
		return conditions;
	}

	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}
}
