package fr.insa.ocm.controller.misc.parts.fastforward;


import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.controller.mainuis.FastForwardView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.utils.fastforward.condition.ConditionAttribute;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConditionAttributeView extends ConditionView {

	@FXML private ChoiceBox<String> choiceBoxAttributeName = null;
	@FXML private ChoiceBox<ConditionAttribute.AttributeState> choiceBoxAttributeState = null;

	public ConditionAttributeView(@NotNull FastForwardView parent) {
		super(parent);
	}

	@Override
	public void initialize(){
		super.initialize();

		choiceBoxAttributeState.getItems().addAll(ConditionAttribute.AttributeState.values());
		choiceBoxAttributeState.setValue(ConditionAttribute.AttributeState.PRESENT);

		List<String> listAttributeName = OCMManager.algorithmLauncherGetListAttributeName();

		choiceBoxAttributeName.getItems().addAll(listAttributeName);
		choiceBoxAttributeName.setValue(listAttributeName.get(0));
	}

	@Override
	public Condition getCondition() {
		return new ConditionAttribute(getActionPatternChoice(), getAttributeNameSelected(), getAttributeStateSelected());
	}

	//********** Serialization Methods **********//

	@Override
	public void importCondition(@NotNull Condition condition){
		super.importCondition(condition);
		if(condition instanceof ConditionAttribute){
			ConditionAttribute conditionAttribute = (ConditionAttribute) condition;

			choiceBoxAttributeName.setValue(conditionAttribute.getAttributeName());
			choiceBoxAttributeState.setValue(conditionAttribute.getAttributeState());
		}
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	private String getAttributeNameSelected(){ return choiceBoxAttributeName.getValue(); }

	private ConditionAttribute.AttributeState getAttributeStateSelected(){ return choiceBoxAttributeState.getValue(); }
}
