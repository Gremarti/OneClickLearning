package fr.insa.ocm.view.misc.parts.fastforward;

import fr.insa.ocm.view.mainuis.FastForwardView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;

public abstract class ConditionView {

	@FXML protected HBox mainHBox = null;
	@FXML protected ToggleButton buttonAlwaysKeep = null;
	@FXML protected ToggleButton buttonAlwaysTrash = null;
	@FXML protected Button buttonRemoveCondition = null;

	private FastForwardView parent;

	ConditionView(@NotNull FastForwardView parent){
		this.parent = parent;
	}

	public HBox getMainHBox(){ return mainHBox; }

	@FXML
	public void initialize(){
		ToggleGroup toggleGroup = new ToggleGroup();
		buttonAlwaysKeep.setToggleGroup(toggleGroup);
		buttonAlwaysTrash.setToggleGroup(toggleGroup);

		buttonAlwaysKeep.getStyleClass().addAll("buttonKeep");
		buttonAlwaysTrash.getStyleClass().addAll("buttonTrash");

		buttonRemoveCondition.setOnAction(event -> parent.buttonRemoveConditionPressed(mainHBox));
	}

	Condition.ActionPatternChoice getActionPatternChoice(){
		Condition.ActionPatternChoice actionPatternChoice = Condition.ActionPatternChoice.NEUTRAL;
		if(buttonAlwaysKeep.isSelected()){
			actionPatternChoice = Condition.ActionPatternChoice.KEEP;
		}else if(buttonAlwaysTrash.isSelected()){
			actionPatternChoice = Condition.ActionPatternChoice.TRASH;
		}

		return actionPatternChoice;
	}

	public abstract Condition getCondition();

	@Override
	public boolean equals(Object obj){
		if(obj instanceof ConditionView){
			ConditionView conditionView = (ConditionView) obj;
			return conditionView.mainHBox.equals(this.mainHBox);
		}
		return false;
	}

	//********** Serialization Methods **********//

	public void importCondition(@NotNull Condition condition){
		switch (condition.getActionPatternChoice()){
			case KEEP:
				buttonAlwaysKeep.setSelected(true);
				break;
			case TRASH:
				buttonAlwaysTrash.setSelected(true);
				break;
		}
	}
}
