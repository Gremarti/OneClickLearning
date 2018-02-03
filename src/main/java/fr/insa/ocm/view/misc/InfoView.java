package fr.insa.ocm.view.misc;

import org.jetbrains.annotations.NotNull;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class InfoView {

	@FXML private VBox mainVBox = null;
	@FXML private Label labelInfo = null;
	@FXML private ProgressBar progressBar = null;
	@FXML private Button buttonContinue = null;

	private String title;
	private String mainText;

	public InfoView(@NotNull String title, @NotNull String mainText){
		this.title = title;
		this.mainText = mainText;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/infoView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		labelInfo.setText(mainText);

		buttonContinue.setOnAction(event -> this.buttonContinuePressed());

		Stage stage = new Stage();
		stage.setScene(new Scene(mainVBox));
		stage.setTitle(title);
		stage.show();
	}

	public void setDisableContinue(boolean b){
		buttonContinue.setDisable(b);
	}

	public void setProgress(double d){
		d = d > 1 ? 1 : d;
		d = d < 0 ? 0 : d;

		progressBar.setProgress(d);
	}

	private void buttonContinuePressed(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();
	}

}
