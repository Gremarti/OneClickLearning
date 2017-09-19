package fr.insa.ocm.controller.misc.menus;

import fr.insa.ocm.controller.OCLController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class AboutView {

	@FXML private VBox mainVBox = null;
	@FXML private Label versionLabel = null;
	@FXML private Button closeButton = null;

	public AboutView(){
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/menus/aboutView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		versionLabel.setText("OneClick Mining - Alpha v"+ OCLController.VERSION);

		closeButton.setOnAction(event -> this.close());

		Stage stage = new Stage();
		stage.setScene(new Scene(mainVBox));
		stage.setTitle("About OneClick Mining");
		stage.show();
	}

	private void close(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();
	}
}
