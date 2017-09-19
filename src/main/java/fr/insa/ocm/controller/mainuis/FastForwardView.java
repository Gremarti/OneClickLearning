package fr.insa.ocm.controller.mainuis;

import org.jetbrains.annotations.NotNull;
import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.utils.serialize.condition.ConditionsDeserializer;
import fr.insa.ocm.model.utils.serialize.condition.ConditionsSerializer;
import fr.insa.ocm.controller.misc.FastForwardWaitingView;
import fr.insa.ocm.controller.misc.parts.fastforward.AddConditionView;
import fr.insa.ocm.controller.misc.parts.fastforward.ConditionView;
import fr.insa.ocm.controller.OCLController;
import fr.insa.ocm.model.utils.fastforward.condition.Condition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FastForwardView {

	@FXML private VBox mainVBox = null;
	@FXML private ListView<HBox> listViewMeasure = null;
	@FXML private Button buttonLaunch = null;
	@FXML private TextField textFieldNumberRound = null;
	@FXML private TextField textFieldSecondPerRound = null;
	@FXML private ChoiceBox<Condition.ActionPatternChoice> choiceBoxKeepTrash = null;

	// Menus to save or load a user list of conditions.
	@FXML private MenuItem menuItemSave = null;
	@FXML private MenuItem menuItemLoad = null;

	private List<ConditionView> listConditionView;
	private AddConditionView addConditionView;

	private ObservableList<HBox> listConditions;

	FastForwardView(){
		listConditionView = new ArrayList<>();
		listConditions = FXCollections.observableArrayList();
		addConditionView = new AddConditionView(this);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainuis/fastForwardView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		Scene scene = new Scene(mainVBox);
		scene.getStylesheets().add(getClass().getResource("/css/button.css").toExternalForm());

		buttonLaunch.setOnAction(event -> this.buttonLaunchPressed());

		choiceBoxKeepTrash.getItems().addAll(Condition.ActionPatternChoice.KEEP, Condition.ActionPatternChoice.TRASH);
		choiceBoxKeepTrash.setValue(Condition.ActionPatternChoice.TRASH);

		FXMLLoader loaderCondition = new FXMLLoader(getClass().getResource("/fxml/misc/parts/fastforward/addCondition.fxml"));
		loaderCondition.setController(addConditionView);
		HBox conditionHBox = new HBox();
		try {
			conditionHBox = loaderCondition.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		listConditions.add(conditionHBox);

		listViewMeasure.setItems(listConditions);

		Stage stage = new Stage();
		stage.setTitle("Choose your Patterns");
		stage.setScene(scene);
		stage.show();
	}

	private void buttonLaunchPressed(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();

		FastForwardWaitingView fastForwardWaitingView = new FastForwardWaitingView();

		List<Condition> listCondition = new ArrayList<>();

		for(ConditionView conditionMeasureStaticView : listConditionView){
			try{
				listCondition.add(conditionMeasureStaticView.getCondition());
			} catch (NumberFormatException e){
				System.err.println("ERR: No valid number was found to create a valid condition");
			}
		}

		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				OCLController.fastForward(Integer.valueOf(textFieldNumberRound.getText()),
						Double.valueOf(textFieldSecondPerRound.getText()),
						listCondition, fastForwardWaitingView, choiceBoxKeepTrash.getValue());
				return null;
			}
		}).start();

	}

	public void buttonAddConditionPressed(){
		// Remove the last HBox which make possible the user to add a condition.
		listConditions.remove(listConditions.size() - 1);

		// Add the condition required by the user.
		ConditionView conditionView = addConditionView.createConditionView();
		HBox conditionHBox = conditionView.getMainHBox();

		listConditions.add(conditionHBox);
		listConditionView.add(conditionView);

		// Add in the last position of the ListView the mean to add another condition.
		listConditions.add(addConditionView.getMainHBox());
	}

	public void buttonRemoveConditionPressed(@NotNull HBox measure){
		listConditions.remove(measure);
		listConditionView.removeIf(conditionView -> conditionView.getMainHBox().equals(measure));
	}

	@FXML
	private void menuItemSaveSelected(){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(DebugLogger.directorySave));
		fileChooser.setTitle("Save your condition list");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Json", "*.json"));
		fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));

		File fileSelected = fileChooser.showSaveDialog(mainVBox.getScene().getWindow());
		if(fileSelected != null){
			final List<Condition> listConditions = new ArrayList<>();
			listConditionView.forEach(conditionView -> listConditions.add(conditionView.getCondition()));

			ConditionsSerializer.saveConditions(fileSelected.getAbsolutePath(), listConditions);
		}
	}

	@FXML
	private void menuItemLoadSelected(){
		// Initialize the filechooser so the user can only choose Json files.
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(DebugLogger.directorySave));
		fileChooser.setTitle("Load your condition list");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Json", "*.json"));
		fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));

		File fileSelected = fileChooser.showOpenDialog(mainVBox.getScene().getWindow());
		if(fileSelected != null){
			// When the file is selected, delete the conditions in the view to replace them with the loaded conditions.
			List<Condition> listDeserializedConditions = ConditionsDeserializer.loadConditions(fileSelected.getAbsolutePath());

			listConditionView.clear();
			Platform.runLater(() -> listConditions.clear());

			for(Condition condition : listDeserializedConditions){
				ConditionView conditionView = addConditionView.createViewFromCondition(condition);
				listConditionView.add(conditionView);
				Platform.runLater(() ->listConditions.add(conditionView.getMainHBox()));
			}
			Platform.runLater(() -> listConditions.add(addConditionView.getMainHBox()));
		}
	}

}
