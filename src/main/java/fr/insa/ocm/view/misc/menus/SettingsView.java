package fr.insa.ocm.view.misc.menus;

import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.oneclicklearning.cache.set.CacheSet;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.view.misc.parts.settings.library.SettingsRealKDView;
import fr.insa.ocm.view.misc.parts.settings.library.SettingsSPMFView;
import fr.insa.ocm.view.misc.parts.settings.pattern.SettingsPatternView;
import fr.insa.ocm.viewmodel.OCLController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsView {

	@FXML private VBox mainVBox = null;
	@FXML private ChoiceBox<Pattern.WrapperType> choiceBoxWrapper = null;

	// Library tab
	@FXML private ScrollPane paneSettings = null;

	// Pattern tab
	@FXML private ListView<HBox> listViewPattern = null;
	@FXML private TextField textFieldNumberPattern = null;

	// Main buttons
	@FXML private Button buttonConfirm = null;
	@FXML private Button buttonCancel = null;

	private SettingsSPMFView settingsSPMFView;
	private SettingsRealKDView settingsRealKDView;
	private static Pattern.WrapperType previousValue = Pattern.WrapperType.SPMF;
	private List<SettingsPatternView> listSettingsPatternView;

	public SettingsView(){
		settingsSPMFView = new SettingsSPMFView();
		settingsRealKDView = new SettingsRealKDView();
		listSettingsPatternView = new ArrayList<>();

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/menus/settingsView.fxml"));
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
		choiceBoxWrapper.getItems().setAll(Pattern.WrapperType.values());
		choiceBoxWrapper.setValue(previousValue);

		this.choiceBoxWrapperModified();

		choiceBoxWrapper.setOnAction(event -> this.choiceBoxWrapperModified());

		// Pattern tab initialization
		ObservableList<HBox> observableSettings = FXCollections.observableArrayList();
		for(Pattern.MeasureType measureType : Pattern.MeasureType.values()){
			SettingsPatternView settingsPV = new SettingsPatternView(measureType);

			listSettingsPatternView.add(settingsPV);
			observableSettings.add(settingsPV.getMainHBox());
		}
		listViewPattern.setItems(observableSettings);

		textFieldNumberPattern.setText(OCMManager.cacheGetSizeListBestPatterns() + "");

		// Buttons initialization
		buttonCancel.setOnAction(event -> this.buttonCancelPressed());
		buttonConfirm.setOnAction(event -> this.buttonConfirmPressed());

		Stage stage = new Stage();
		stage.setScene(new Scene(mainVBox));
		stage.setTitle("Settings");
		stage.show();
	}

	//********** Action methods **********//

	private void choiceBoxWrapperModified(){

		switch (choiceBoxWrapper.getValue()){
			case REALKD:
				paneSettings.setContent(settingsRealKDView.getMainVBox());
				break;
			case SPMF:
				paneSettings.setContent(settingsSPMFView.getMainVBox());
				break;
		}
	}

	private void buttonCancelPressed(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();
	}

	private void buttonConfirmPressed(){
		OCLController.setWrapperType(choiceBoxWrapper.getValue());
		previousValue = choiceBoxWrapper.getValue();

		switch (choiceBoxWrapper.getValue()){
			case REALKD:
				break;
			case SPMF:
				settingsSPMFView.setSettingsAlgorithmLauncher();
				listSettingsPatternView.forEach(SettingsPatternView::setMeasureChoice);
				break;
		}

		try{
			OCMManager.cacheSetSizeListBestPatterns(Integer.valueOf(textFieldNumberPattern.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsView: Wrong number given to the ranking size in the settings.", DebugLogger.MessageSeverity.MEDIUM);
		}

		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();
	}
}
