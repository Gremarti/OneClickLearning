package fr.insa.ocm.view.misc.parts.fastforward;

import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.view.mainuis.FastForwardView;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import fr.insa.ocm.model.utils.fastforward.condition.ConditionAlgorithm;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConditionAlgorithmView extends ConditionView {

	@FXML private ChoiceBox<String> choiceBoxAlgorithmName = null;
	@FXML private ChoiceBox<ConditionAlgorithm.AlgorithmState> choiceBoxAlgorithmState = null;

	public ConditionAlgorithmView(FastForwardView parent) {
		super(parent);
	}

	public void initialize(){
		super.initialize();

		choiceBoxAlgorithmState.getItems().addAll(ConditionAlgorithm.AlgorithmState.values());
		choiceBoxAlgorithmState.setValue(ConditionAlgorithm.AlgorithmState.CREATEDBY);

		List<String> listAlgorithmName = OCMManager.algorithmLauncherGetListAlgorithmName();

		choiceBoxAlgorithmName.getItems().addAll(listAlgorithmName);
		choiceBoxAlgorithmName.setValue(listAlgorithmName.get(0));
	}

	@Override
	public Condition getCondition() {
		return new ConditionAlgorithm(getActionPatternChoice(),
				getAlgorithmNameSelected(),
				getAlgorithmStateSelected());
	}

	//********** Serialization Methods **********//

	@Override
	public void importCondition(@NotNull Condition condition){
		super.importCondition(condition);
		if(condition instanceof ConditionAlgorithm) {
			ConditionAlgorithm conditionAlgorithm = (ConditionAlgorithm) condition;

			choiceBoxAlgorithmName.setValue(conditionAlgorithm.getAlgorithmName());
			choiceBoxAlgorithmState.setValue(conditionAlgorithm.getAlgorithmState());
		}
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	private String getAlgorithmNameSelected(){
		return choiceBoxAlgorithmName.getValue();
	}

	private ConditionAlgorithm.AlgorithmState getAlgorithmStateSelected(){
		return choiceBoxAlgorithmState.getValue();
	}
}
