package fr.insa.ocm.controller.mainuis;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DataView {

	@FXML private VBox mainVBox = null;

	@FXML private GridPane gridPaneData = null;
	@FXML private Button buttonClose = null;

	DataView(){
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mainuis/dataView.fxml"));
		loader.setController(this);

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
//
//		buttonClose.setOnAction(event -> this.buttonClosePressed());
//
//		List<List<String>> data = new ArrayList<>();
//
//		String pathDataFile = OCLController.getInstance().getPathDataFile();
//		if(pathDataFile != null && !pathDataFile.equals("")){
//			CSVLoaderRealKD csvLoader = new CSVLoaderRealKD(pathDataFile);
//			data = csvLoader.loadCSV();
//		}
//
//
//		int index = 0;
//		for(List<String> entry : data){
//			Label[] labels = new Label[entry.size()];
//
//			for (int i = 0; i < labels.length; i++) {
//				if(index == 0){
//					Label label = new Label(entry.get(i));
//
//					label.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
//					label.setPadding(new Insets(5));
//					label.setStyle("" +
//							"-fx-border-color: black;" +
//							"-fx-border-width: 1px;");
//					label.setMaxWidth(1E300);
//					label.setMaxHeight(1E300);
//
//					labels[i] = label;
//				}else {
//					Label label = new Label(entry.get(i));
//
//					label.setFont(Font.font("Verdana", 14));
//					label.setPadding(new Insets(5));
//					label.setStyle("" +
//							"-fx-border-color: black;" +
//							"-fx-border-width: 1px;");
//					label.setMaxWidth(1E300);
//					label.setMaxHeight(1E300);
//
//					labels[i] = label;
//				}
//			}
//
//			gridPaneData.addRow(index, labels);
//			index++;
//		}
//
//		Stage stage = new Stage();
//		stage.setScene(new Scene(mainVBox));
//		stage.setTitle("Data Viewer");
//		stage.show();
//
	}

	private void buttonClosePressed(){
		Stage stage = (Stage) buttonClose.getScene().getWindow();
		stage.close();
	}
}
