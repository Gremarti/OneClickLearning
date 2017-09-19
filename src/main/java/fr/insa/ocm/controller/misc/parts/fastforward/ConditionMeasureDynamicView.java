package fr.insa.ocm.controller.misc.parts.fastforward;

import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.controller.mainuis.FastForwardView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.utils.fastforward.condition.ConditionMeasureDynamic;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.jetbrains.annotations.NotNull;

public class ConditionMeasureDynamicView extends ConditionView {

	@FXML private ChoiceBox<Pattern.MeasureType> choiceBoxMeasure = null;
	@FXML private TextField textFieldValue = null;
	@FXML private ChoiceBox<ConditionMeasureDynamic.CatogoryType> choiceCategory = null;


	public ConditionMeasureDynamicView(@NotNull FastForwardView parent){
		super(parent);
	}

	@FXML
	@Override
	public void initialize(){
		super.initialize();

		choiceBoxMeasure.getItems().addAll(Pattern.MeasureType.values());
		choiceBoxMeasure.setValue(Pattern.MeasureType.FREQUENCY);

		choiceCategory.getItems().addAll(ConditionMeasureDynamic.CatogoryType.values());
		choiceCategory.setValue(ConditionMeasureDynamic.CatogoryType.HIGHEST);
	}

	@Override
	public Condition getCondition() {
		return new ConditionMeasureDynamic(getActionPatternChoice(), getMeasureType(), getCategoryType(), getIndexValue());
	}

	//********** Serialization Methods **********//

	public void importCondition(@NotNull Condition condition){
		super.importCondition(condition);
		if(condition instanceof ConditionMeasureDynamic){
			ConditionMeasureDynamic conditionMeDy = (ConditionMeasureDynamic) condition;

			choiceBoxMeasure.setValue(conditionMeDy.getMeasure());
			textFieldValue.setText(Integer.toString(conditionMeDy.getIndex()));
			choiceCategory.setValue(conditionMeDy.getCatogory());
		}
	}

	//********** Getters/Setters Methods **********//

	private Pattern.MeasureType getMeasureType(){
		return choiceBoxMeasure.getValue();
	}

	private ConditionMeasureDynamic.CatogoryType getCategoryType(){
		return choiceCategory.getValue();
	}

	private int getIndexValue(){
		return Integer.valueOf(textFieldValue.getText());
	}
}
