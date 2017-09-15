package fr.insa.ocm.view.misc.parts.settings.pattern;

import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;

import java.io.IOException;

public class SettingsPatternView {

	@FXML private HBox mainHBox = null;
	@FXML private CheckBox checkBox = null;

	private Pattern.MeasureType measure;

	public SettingsPatternView(Pattern.MeasureType measure){
		this.measure = measure;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/settings/pattern/settingsPatternView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//********** Initialization methods **********//

	@FXML
	public void initialize(){
		checkBox.setText(measure.toString());

		checkBox.setSelected(AlgorithmLauncherSPMF.getMeasurePatternDescriptor(measure.getIndex()));
	}

	//********** Getters/Setters methods **********//

	// Getters //

	public HBox getMainHBox(){
		return mainHBox;
	}

	public void setMeasureChoice(){
		AlgorithmLauncherSPMF.setMeasuresPatternDescriptor(checkBox.isSelected(), measure.getIndex());
	}

}
