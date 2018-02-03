package fr.insa.ocm.view.mainuis;

import org.jetbrains.annotations.NotNull;
import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.view.misc.parts.mining.PatternView;
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

public class MiningView {

	@FXML private VBox mainVBox = null;
	@FXML private ListView<HBox> listProposedPatterns = null;
	@FXML private Button buttonConfirm = null;
	@FXML private Button buttonSetKeep = null;
	@FXML private Button buttonSetTrash = null;

	@FXML private Label firstLabel = null;
	@FXML private Label labelRound = null;

	private List<Pattern> patterns;

	private List<PatternView> patternControllers;

	MiningView(@NotNull List<Pattern> userRank){
		patternControllers = new ArrayList<>();
		patterns = userRank;

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainuis/miningView.fxml"));
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
		scene.getStylesheets().add(getClass().getResource("/css/hbox.css").toExternalForm());

		buttonConfirm.setOnAction(event -> this.buttonConfirmPressed());
		buttonSetKeep.setOnAction(event -> this.buttonSetKeepPressed());
		buttonSetTrash.setOnAction(event -> this.buttonSetTrashPressed());

		labelRound.setText("Round "+ OCMManager.coactiveGetNbRound());

		ObservableList<HBox> hBoxes = FXCollections.observableArrayList();

		patterns.forEach(pattern -> {
			PatternView patternView = new PatternView(pattern, patterns.indexOf(pattern));
			patternControllers.add(patternView);
			hBoxes.add(patternView.getMainHBox());
		});

		listProposedPatterns.setItems(hBoxes);

		Stage stage = new Stage();
		stage.setTitle("Choose your Patterns");
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	private void buttonConfirmPressed(){
		List<Pattern> keptPatterns = new ArrayList<>();
		List<Pattern> neutralPatterns = new ArrayList<>();
		List<Pattern> trashedPatterns = new ArrayList<>();

		for(PatternView patternView : patternControllers){
			Pattern pattern = patterns.get(patternView.getIndex());

			if(patternView.isKept()){
				keptPatterns.add(pattern);
			}else if(patternView.isTrashed()){
				trashedPatterns.add(pattern);
			}else{
				neutralPatterns.add(pattern);
			}
		}

		OCLController.setPatternList(keptPatterns, neutralPatterns, trashedPatterns);

		MainView.getCurrentMainView().refreshKeptPatternsList();

		Stage stage = (Stage) buttonConfirm.getScene().getWindow();
		stage.close();
	}

	private void buttonSetKeepPressed(){
		patternControllers.forEach(PatternView::setIsKept);
	}

	private void buttonSetTrashPressed(){
		patternControllers.forEach(PatternView::setIsTrashed);
	}
}
