package fr.insa.ocm.view.misc;

import fr.insa.ocm.viewmodel.OCLController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class FastForwardWaitingView {

	@FXML private VBox mainVBox = null;
	@FXML private Label labelCurrentOperation = null;
	@FXML private Label labelTime = null;
	@FXML private ProgressBar progressBarMining = null;
	@FXML private ProgressBar progressBarRound = null;
	@FXML private Button buttonAbortFinish = null;

	private boolean hasFinished;

	public FastForwardWaitingView(){
		hasFinished = false;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/fastForwardWaitingView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		labelCurrentOperation.setText("Current operation: Initializing the mining process.");
		labelTime.setText("Estimated time before completion: Unknown");
		buttonAbortFinish.setText("Abort");

		progressBarMining.setProgress(0);
		progressBarRound.setProgress(0);

		buttonAbortFinish.setOnAction(event -> this.buttonAbortFinishPressed());

		Stage stage = new Stage();
		stage.setScene(new Scene(mainVBox));
		stage.setTitle("Fast Forward Mining");
		stage.show();
	}

	public void setHasFinished(boolean b){
		hasFinished = b;
		if(hasFinished){
			Platform.runLater(() -> buttonAbortFinish.setText("Finished"));
		}
	}

	public void bindCurrentOperation(StringProperty currentOpration){
		Platform.runLater(() -> labelCurrentOperation.textProperty().bind(currentOpration));
	}

	public void bindCurrentTime(StringProperty remainingTime){
		Platform.runLater(() -> labelTime.textProperty().bind(remainingTime));
	}

	public void bindProgressMining(DoubleProperty progressMining){
		Platform.runLater(() -> progressBarMining.progressProperty().bind(progressMining));
	}

	public void bindProgressLearning(DoubleProperty progressLearning) {
		Platform.runLater(() -> progressBarRound.progressProperty().bind(progressLearning));
	}

	private void buttonAbortFinishPressed(){
		if(!hasFinished){
			OCLController.requestFFStop();
		}
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();
	}
}
