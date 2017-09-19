package fr.insa.ocm.controller.misc.parts.fastforward;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.controller.mainuis.FastForwardView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class AddConditionView {

	@FXML private HBox mainHBox = null;
	@FXML private ChoiceBox<Condition.ConditionType> choiceBoxCondition = null;
	@FXML private Button buttonAddCondition = null;

	private FastForwardView parent;

	public AddConditionView(@NotNull FastForwardView parent){
		this.parent = parent;
	}

	@FXML
	public void initialize(){
		buttonAddCondition.setOnAction(event -> parent.buttonAddConditionPressed());

		choiceBoxCondition.getItems().addAll(Condition.ConditionType.values());
		choiceBoxCondition.setValue(Condition.ConditionType.CONDITION_ATTRIBUTE);
	}

	public HBox getMainHBox(){ return mainHBox; }

	@Nullable
	public ConditionView createConditionView(){
		ConditionView conditionView = null;
		try {
			FXMLLoader loader = null;
			switch (choiceBoxCondition.getValue()) {
				case CONDITION_ATTRIBUTE:
					conditionView = new ConditionAttributeView(parent);
					loader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/fastforward/conditionAttribute.fxml"));
					break;
				case CONDITION_ALGORITHM:
					conditionView = new ConditionAlgorithmView(parent);
					loader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/fastforward/conditionAlgorithm.fxml"));
					break;
				case CONDITION_MEASURE_STATIC:
					conditionView = new ConditionMeasureStaticView(parent);
					loader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/fastforward/conditionMeasureStatic.fxml"));
					break;
				case CONDITION_MEASURE_BETWEEN:
					conditionView = new ConditionMeasureBetweenView(parent);
					loader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/fastforward/conditionMeasureBetween.fxml"));
					break;
				case CONDITION_MEASURE_DYNAMIC:
					conditionView = new ConditionMeasureDynamicView(parent);
					loader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/fastforward/conditionMeasureDynamic.fxml"));
					break;
			}
			loader.setController(conditionView);
			loader.load();
		}catch (IOException | NullPointerException e){
			e.printStackTrace();
		}
		return conditionView;
	}

	@Nullable
	public ConditionView createViewFromCondition(@NotNull Condition condition){
		ConditionView conditionView = null;
		choiceBoxCondition.setValue(condition.getConditionType());

		conditionView = createConditionView();
		if (conditionView != null) {
			conditionView.importCondition(condition);
		}

		return conditionView;
	}
}
