package fr.insa.ocm.controller.misc.parts.fastforward;

import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.controller.mainuis.FastForwardView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.utils.fastforward.condition.ConditionMeasureStatic;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

public class ConditionMeasureStaticView extends ConditionView{

	@FXML private ChoiceBox<Pattern.MeasureType> choiceBoxMeasure = null;
	@FXML private ChoiceBox<ConditionMeasureStatic.OperatorType> choiceBoxOperator = null;
	@FXML private TextField textFieldValue = null;


	public ConditionMeasureStaticView(@NotNull FastForwardView parent){
		super(parent);
	}

	@FXML
	@Override
	public void initialize(){
		super.initialize();

		choiceBoxMeasure.getItems().addAll(Pattern.MeasureType.values());
		choiceBoxMeasure.setValue(Pattern.MeasureType.FREQUENCY);

		choiceBoxOperator.getItems().addAll(ConditionMeasureStatic.OperatorType.values());
		choiceBoxOperator.setValue(ConditionMeasureStatic.OperatorType.EQL);
	}

	@Override
	public Condition getCondition() {
		return new ConditionMeasureStatic(getActionPatternChoice(), getSelectedMeasureType(), getSelectedOperatorType(), getThresholdValue());
	}

	//********** Serialization Methods **********//

	public void importCondition(@NotNull Condition condition){
		super.importCondition(condition);
		if(condition instanceof ConditionMeasureStatic){
			ConditionMeasureStatic conditionMeSt = (ConditionMeasureStatic) condition;

			choiceBoxMeasure.setValue(conditionMeSt.getMeasure());
			choiceBoxOperator.setValue(conditionMeSt.getOperator());
			textFieldValue.setText(Double.toString(conditionMeSt.getThresholdValue()));
		}
	}

	//********** Getters/Setters Methods **********//

	private ConditionMeasureStatic.OperatorType getSelectedOperatorType(){
		return choiceBoxOperator.getValue();
	}

	private Pattern.MeasureType getSelectedMeasureType(){
		return choiceBoxMeasure.getValue();
	}

	private double getThresholdValue(){
		return Double.valueOf(textFieldValue.getText());
	}
}
