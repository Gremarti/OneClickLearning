package fr.insa.ocm.controller.misc.menus;


import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.controller.Monitor;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MonitorView {

	public enum MonitorType{
		ALGORITHM, BANDIT, COACTIVE;

		public SimpleStringProperty getTextProperty(){
			switch (this){
				case ALGORITHM:
					return Monitor.getMonitorAlgorithmsLaunched();
				case BANDIT:
					return Monitor.getMonitorBanditWeights();
				case COACTIVE:
					return Monitor.getMonitorCoactiveWeights();
				default:
					DebugLogger.printDebug("MonitorView: Unable to retrieve the correct monitor type.", DebugLogger.MessageSeverity.MEDIUM);
					return new SimpleStringProperty("");
			}
		}

		@NotNull
		public String getStageTitle(){
			switch (this){
				case ALGORITHM:
					return "Monitoring Launched Algorithms";
				case BANDIT:
					return "Monitoring Bandit Weights";
				case COACTIVE:
					return "Monitoring Coactive Weights";
				default:
					return "Monitor";
			}
		}
	}

	@FXML private VBox mainVBox = null;
	@FXML private Label labelInfo = null;
	@FXML private Button buttonClose = null;

	private MonitorType monitorType;

	public MonitorView(MonitorType monitorType){
		this.monitorType = monitorType;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/menus/monitor/monitorView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		buttonClose.setOnAction(event -> this.buttonClosePressed());

		labelInfo.textProperty().bind(monitorType.getTextProperty());

		Stage stage = new Stage();
		stage.setScene(new Scene(mainVBox));
		stage.setTitle(monitorType.getStageTitle());
		stage.show();
	}

	private void buttonClosePressed(){
		Stage stage = (Stage) mainVBox.getScene().getWindow();
		stage.close();
	}
}
