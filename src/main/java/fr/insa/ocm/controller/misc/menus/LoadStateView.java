package fr.insa.ocm.controller.misc.menus;

import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.utils.serialize.OCMDeserializer;
import fr.insa.ocm.controller.misc.ErrorView;
import fr.insa.ocm.controller.misc.InfoView;
import fr.insa.ocm.controller.mainuis.MainView;
import fr.insa.ocm.controller.OCLController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class LoadStateView {

	@FXML private VBox mainVBox = null;

	@FXML private Button buttonSearch = null;
	@FXML private Button buttonData = null;
	@FXML private Button buttonLoad = null;

	@FXML private Label labelSearch = null;
	@FXML private Label labelData = null;

	private SimpleStringProperty msgSearch;
	private SimpleStringProperty msgData;

	private File fileSearch;
	private File fileData;

	private boolean isSearchFileSelected;
	private boolean isDataFileSelected;

	public LoadStateView(){
		msgSearch = new SimpleStringProperty("Please select a search state file to load");
		msgData = new SimpleStringProperty("Please select a the corresponding data file to load");

		isSearchFileSelected = false;
		isDataFileSelected = false;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/menus/loadStateView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		labelSearch.textProperty().bind(msgSearch);
		labelData.textProperty().bind(msgData);

		labelSearch.setWrapText(true);
		labelData.setWrapText(true);

		buttonSearch.setOnAction(event -> this.buttonSearchPressed());
		buttonData.setOnAction(event -> this.buttonDataPressed());
		buttonLoad.setOnAction(event -> this.buttonLoadPressed());

		Stage stage = new Stage();
		stage.setScene(new Scene(mainVBox));
		stage.setTitle("Load a saved state");
		stage.show();
	}

	private void buttonSearchPressed(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		FileChooser fileChooserSearch = new FileChooser();

		fileChooserSearch.setTitle("Choose the file which contains the searching state");
		fileSearch = fileChooserSearch.showOpenDialog(stage);

		if(fileSearch != null){
			msgSearch.set(fileSearch.getAbsolutePath());

			buttonLoad.setDisable(!isDataFileSelected);
			isSearchFileSelected = true;
		}else{
			new ErrorView("No Search file were selected.");
		}
	}

	private void buttonDataPressed(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		FileChooser fileChooserData = new FileChooser();

		fileChooserData.setTitle("Choose the file which contains the data associated with the search state");
		fileData = fileChooserData.showOpenDialog(stage);

		if(fileData != null) {
			msgData.set(fileData.getAbsolutePath());

			buttonLoad.setDisable(!isSearchFileSelected);
			isDataFileSelected = true;
		}else{
			new ErrorView("No Data file were selected.");
		}
	}

	private void buttonLoadPressed(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();

		InfoView infoView = new InfoView("Loading a state", "Currently loading the selected state.");
		infoView.setDisableContinue(true);

		new Thread(new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				OCMManager.algorithmManagerStopMining();

				// Import the data
				OCLController.reload(fileData.getPath());
				Thread.sleep(250);
				Platform.runLater(() -> infoView.setProgress(0.5));

				// Import the saveState
				new OCMDeserializer(fileSearch.getAbsolutePath());
				Thread.sleep(250);
				Platform.runLater(() -> infoView.setProgress(1.));

				MainView.getCurrentMainView().setDataLoaded();
				Platform.runLater(() -> MainView.getCurrentMainView().refreshKeptPatternsList());

				Platform.runLater(() -> infoView.setDisableContinue(false));

//				OCMManager.getInstance().reloadData(fileData.getPath());
//				OCLController.getInstance().setFromLoadState();
//				Thread.sleep(500);
//				infoView.setProgress(0.5);
//
//				// Import the saveState
//				new OCMDeserializer(fileSearch.getAbsolutePath());
//				Thread.sleep(500);
//				infoView.setProgress(1.);
//
//				try{
//					OCMManager.getInstance().setInitialized(true);
//				} catch (Exception e){
//					e.printStackTrace();
//				}
//
//				MainView.getCurrentMainView().setDataLoaded();
//				Platform.runLater(() -> MainView.getCurrentMainView().refreshKeptPatternsList());
//
//				infoView.setDisableContinue(false);

				OCMManager.algorithmManagerStartMining();

				return null;
			}
		}).start();


	}
}
