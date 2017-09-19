package fr.insa.ocm.controller.misc.parts.fastforward;

import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.controller.mainuis.FastForwardView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.utils.fastforward.condition.ConditionMeasureBetween;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

public class ConditionMeasureBetweenView extends ConditionView{

	@FXML private ChoiceBox<Pattern.MeasureType> choiceBoxMeasure = null;
	@FXML private ChoiceBox<ConditionMeasureBetween.IntervalChoice> choiceBoxIncluded = null;
	@FXML private TextField textFieldLowValue = null;
	@FXML private TextField textFieldHighValue = null;

	public ConditionMeasureBetweenView(@NotNull FastForwardView parent){
		super(parent);
	}

	@Override
	@FXML
	public void initialize(){
		super.initialize();

		choiceBoxMeasure.getItems().addAll(Pattern.MeasureType.values());
		choiceBoxMeasure.setValue(Pattern.MeasureType.FREQUENCY);

		choiceBoxIncluded.getItems().addAll(ConditionMeasureBetween.IntervalChoice.values());
		choiceBoxIncluded.setValue(ConditionMeasureBetween.IntervalChoice.INSIDE_IN_IN);
	}

	@Override
	public Condition getCondition() {
		return new ConditionMeasureBetween(getActionPatternChoice(), getMeasureType(), getIntervalChoice(), getThresholdValueLowerBound(), getThresholdValueHigherBound());
	}

	//********** Serialization Methods **********//

	@Override
	public void importCondition(@NotNull Condition condition){
		super.importCondition(condition);
		if(condition instanceof ConditionMeasureBetween){
			ConditionMeasureBetween conditionMeBe = (ConditionMeasureBetween) condition;

			choiceBoxMeasure.setValue(conditionMeBe.getMeasureType());
			choiceBoxIncluded.setValue(conditionMeBe.getIntervalChoice());
			textFieldLowValue.setText(Double.toString(conditionMeBe.getLowerBound().getThresholdValue()));
			textFieldHighValue.setText(Double.toString(conditionMeBe.getHigerBound().getThresholdValue()));
		}
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	private ConditionMeasureBetween.IntervalChoice getIntervalChoice(){
		return choiceBoxIncluded.getValue();
	}

	private Pattern.MeasureType getMeasureType(){
		return choiceBoxMeasure.getValue();
	}

	private double getThresholdValueLowerBound(){
		return Double.valueOf(textFieldLowValue.getText());
	}

	private double getThresholdValueHigherBound(){
		return Double.valueOf(textFieldHighValue.getText());
	}
}
