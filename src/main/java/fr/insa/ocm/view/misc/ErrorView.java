package fr.insa.ocm.view.misc;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import jline.internal.Nullable;

import java.io.IOException;

public class ErrorView {

	@FXML private VBox mainVBox;
	@FXML private Button closeButton;
	@FXML private Label errorLabel;

	private final static String defaultText = "Oh no ! An error has occured ! D:";
	private String errorText = "";

	public ErrorView(@Nullable String errorTxt){
		this.errorText = errorTxt;
		if (errorTxt == null || errorTxt.equals("")){
			this.errorText = defaultText;
		}

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/errorView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		closeButton.setOnMouseClicked(event -> this.quit());
		errorLabel.setText(errorText);

		Stage stage = new Stage();
		stage.setScene(new Scene(mainVBox));
		stage.setTitle("Error");
		stage.show();
	}

	@FXML
	private void quit(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();
	}
}
