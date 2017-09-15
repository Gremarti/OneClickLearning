package fr.insa.ocm.view.misc.parts.mining;

import com.sun.istack.internal.NotNull;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.utils.Vector;
import fr.insa.ocm.model.wrapper.api.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PatternView {

	@FXML private HBox mainHBox = null;

	@FXML private ToggleButton buttonKeep = null;
	@FXML private ToggleButton buttonTrash = null;

	@FXML private Label labelPattern = null;
	@FXML private Label labelExploitation = null;
	@FXML private Label labelIndicator = null;

	private Pattern pattern;
	private int index;

	public PatternView(@NotNull Pattern pattern, int index){
		this.pattern = pattern;
		this.index = index;

		FXMLLoader loaderPattern = new FXMLLoader(getClass().getResource("/fxml/misc/parts/mining/patternView.fxml"));
		loaderPattern.setController(this);

		try {
			loaderPattern.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		ToggleGroup toggleGroup = new ToggleGroup();

		// Set if it is exploitation or exploration pattern
		if(isExploitation()) {
			labelExploitation.setText("Exploitation");
			labelExploitation.getStyleClass().addAll("exploitation");

			labelIndicator.getStyleClass().addAll("exploitation");
		} else {
			labelExploitation.setText("Exploration");
			labelExploitation.getStyleClass().addAll("exploration");

			labelIndicator.getStyleClass().addAll("exploration");
		}

		labelIndicator.setText(getMostUsefulWeightName());

		// Set the style of the buttons
		buttonKeep.getStyleClass().addAll("buttonKeep");
		buttonTrash.getStyleClass().addAll("buttonTrash");

		buttonKeep.setToggleGroup(toggleGroup);
		buttonTrash.setToggleGroup(toggleGroup);

		labelPattern.setText(pattern.toString());
	}

	private boolean isExploitation(){
		int indexAlgoName = OCMManager.algorithmLauncherGetListAlgorithmName().indexOf(pattern.getAlgorithmName());
		double[] weights = OCMManager.banditGetWeights();

		// Determine if the pattern is from Exploitation or Exploration.
		boolean isExploitation = true;
		for(int i = 0; i < weights.length; ++i){
			if(i != indexAlgoName){
				isExploitation = isExploitation && (weights[indexAlgoName] > weights[i]);
			}
		}

		return isExploitation;
	}

	private String getMostUsefulWeightName(){
		String weightName;
		double[] weightsCoactive = OCMManager.coactiveGetWeights();
		double[] weightsPattern = pattern.getAttributesVector().getValues();
		double[] weightsCartesian = new double[weightsCoactive.length];

		if(weightsPattern.length == weightsCoactive.length){
			for(int i = 0; i < weightsCoactive.length; ++i){
				weightsCartesian[i] = (weightsPattern[i] != 0d) ? weightsCoactive[i] : 0d;
			}
		} else {
			DebugLogger.printDebug("PatternView: Unable to match the weights of the Coactive Learning and the weight of the Pattern", DebugLogger.MessageSeverity.HIGH);
		}

		int index = 0;
		double max = weightsCartesian[index];
		for(int i = 1; i < weightsCartesian.length; ++i){
			if (weightsCartesian[i] > max){
				max = weightsCartesian[i];
				index = i;
			}
		}

		if (index < Pattern.MeasureType.values().length){
			weightName = Pattern.MeasureType.getName(index).toString();
		} else {
			List<String> listWeightName = new ArrayList<>();
			for (Pattern.MeasureType measure : Pattern.MeasureType.values()) {
				listWeightName.add(measure.toString());
			}
			listWeightName.addAll(OCMManager.algorithmLauncherGetListAttributeName());
			listWeightName.addAll(OCMManager.algorithmLauncherGetListAlgorithmName());
			weightName = listWeightName.get(index);
		}

		return weightName;
	}

	//********** Getters/Setters Methods **********//

	// Getters //

	public HBox getMainHBox(){
		return mainHBox;
	}

	public boolean isKept(){
		return buttonKeep.isSelected();
	}

	public boolean isTrashed(){
		return buttonTrash.isSelected();
	}

	public int getIndex(){
		return index;
	}

	// Setters //

	public void setIsKept(){
		buttonKeep.setSelected(true);
	}

	public void setIsTrashed(){
		buttonTrash.setSelected(true);
	}
}
