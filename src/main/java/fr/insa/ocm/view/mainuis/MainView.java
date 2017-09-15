package fr.insa.ocm.view.mainuis;

import fr.insa.ocm.model.OCMManager;
import fr.insa.ocm.model.utils.serialize.OCMSerializer;
import fr.insa.ocm.model.wrapper.api.Pattern;
import fr.insa.ocm.view.OCLApplication;
import fr.insa.ocm.view.misc.*;
import fr.insa.ocm.view.misc.menus.AboutView;
import fr.insa.ocm.view.misc.menus.MonitorView;
import fr.insa.ocm.view.misc.menus.LoadStateView;
import fr.insa.ocm.view.misc.menus.SettingsView;
import fr.insa.ocm.viewmodel.InfoAlgorithm;
import fr.insa.ocm.viewmodel.OCLController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainView {

	@FXML private BorderPane mainPane = null;

	@FXML private ListView<String> listKeptPatterns = null;
	private ObservableList<String> keptPatternsStrings = FXCollections.observableArrayList();

	@FXML private Button buttonMining = null;
	@FXML private Button buttonData = null;
	@FXML private Button buttonPause = null;
	@FXML private Button buttonFastForward = null;

	@FXML private Label labelInfo = null;

	private static MainView currentMainView;
	private Stage mainStage = OCLApplication.getMainStage();

	private boolean dataLoaded;

	// Manage the file inputs of the user
	private final FileChooser fileChooser = new FileChooser();
	private boolean firstFile = true;

	public MainView(){
		dataLoaded = false;

		currentMainView = this;
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainuis/mainView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void initialize(){
		listKeptPatterns.setItems(keptPatternsStrings);

		MenuBar menuBar = new MenuBar();
		menuBar.prefWidthProperty().bind(mainStage.widthProperty());
		mainPane.setTop(menuBar);

		// File menu
		Menu fileMenu = new Menu("File");
		MenuItem newDataMenuItem = new MenuItem("Import a new dataset");
		MenuItem savePatternMenuItem = new MenuItem("Export the patterns");
		MenuItem saveStateMenuItem = new MenuItem("Save the current search state");
		MenuItem loadStateMenuItem = new MenuItem("Load a search state");
		MenuItem exitMenuItem = new MenuItem("Quit");

		Menu editMenu = new Menu("Edit");
		MenuItem menuItemSettings = new MenuItem("Settings");

		Menu monitoringMenu = new Menu("Monitor");
		MenuItem menuItemNumberAlgorithmLaunched = new MenuItem("Algorithms Launched Stats");
		MenuItem menuItemBanditWeights = new MenuItem("Bandit Weights");
		MenuItem menuItemCoactiveWeights = new MenuItem("Coactive Learning Weights");

		Menu helpMenu = new Menu("Help");
		MenuItem aboutMenuItem = new MenuItem("About");
		MenuItem helpMenuItem = new MenuItem("Help");

		exitMenuItem.setOnAction(event -> Platform.exit());
		newDataMenuItem.setOnAction(event -> this.menuItemNewDataSelected());
		savePatternMenuItem.setOnAction(event -> this.menuItemSavePatternSelected());
		saveStateMenuItem.setOnAction(event -> this.menuItemSaveStateSelected());
		loadStateMenuItem.setOnAction(event -> this.menuItemLoadStateSelected());

		menuItemSettings.setOnAction(event -> this.menuItemSettingsSelected());

		menuItemNumberAlgorithmLaunched.setOnAction(event -> this.menuItemNumberAlgorithmLaunchedSelected());
		menuItemBanditWeights.setOnAction(event -> this.menuItemBanditWeightsSelected());
		menuItemCoactiveWeights.setOnAction(event -> this.menuItemCoactiveWeightsSelected());

		aboutMenuItem.setOnAction(event -> this.menuItemAboutSelected());
		helpMenuItem.setOnAction(event -> this.menuItemHelpSelected());


		//add item to fileMenu
		fileMenu.getItems().addAll(newDataMenuItem, savePatternMenuItem, saveStateMenuItem, loadStateMenuItem,
				new SeparatorMenuItem(), exitMenuItem);
		editMenu.getItems().addAll(menuItemSettings);
		monitoringMenu.getItems().addAll(menuItemNumberAlgorithmLaunched, menuItemBanditWeights, menuItemCoactiveWeights);
		helpMenu.getItems().addAll(helpMenuItem, aboutMenuItem);
		//Add menus to menubar
		menuBar.getMenus().addAll(fileMenu, editMenu, monitoringMenu, helpMenu);

		buttonMining.setOnAction(event -> this.buttonMiningClicked());
		buttonData.setOnAction(event -> this.buttonDataPressed());
		buttonPause.setOnAction(event -> this.buttonPausePressed());
		buttonPause.setText("Pause Mining");
		buttonFastForward.setOnAction(event -> this.buttonFastForwardPressed());

		mainStage.setScene(new Scene(mainPane, 900, 600));
		mainStage.setTitle("OneClick Mining");
		mainStage.show();
	}

	public static MainView getCurrentMainView(){
		return currentMainView;
	}

	public void setDataLoaded(){
		dataLoaded = true;
	}

	public Label getLabelInfo(){
		return labelInfo;
	}

	public void refreshKeptPatternsList(){
		List<Pattern> patterns = OCMManager.patternWarehouseGetPatterns();
		Platform.runLater(() -> keptPatternsStrings.clear());

		for(Pattern p : patterns){
			Platform.runLater(() -> keptPatternsStrings.add(p.toString()));
		}
	}

	private void buttonMiningClicked(){
		if(dataLoaded && OCMManager.isInitialized() && OCMManager.algorithmManagerIsMining()){
			new MiningView(OCLController.getUserRank());
		} else if(dataLoaded && OCMManager.isInitialized()){
			new ErrorView("You can only request a knowledge gathering when the algorithms are running.");
		} else if(dataLoaded){
			new ErrorView("Please wait a few seconds for the initialization of the data handler.");
 		} else {
			new ErrorView("No data has been found. Have you imported any data ?");
		}
	}

	private void buttonDataPressed(){
		new ErrorView("It's broken D:");
	}

	private void buttonPausePressed(){
		if(OCMManager.algorithmManagerIsMining()){
			new Thread(OCMManager::algorithmManagerPauseMining).start();
			buttonPause.setText("Resume Mining");
			InfoAlgorithm.setPaused(true);
		}else{
			new Thread(OCMManager::algorithmManagerResumeMining).start();
			buttonPause.setText("Pause Mining");
			InfoAlgorithm.setPaused(false);
		}
	}

	private void buttonFastForwardPressed(){
		if(dataLoaded && OCMManager.isInitialized() && OCMManager.algorithmManagerIsMining()){
			new FastForwardView();
		} else if(dataLoaded && OCMManager.isInitialized()){
			new ErrorView("You can only request a fast forward process when the algorithms are running.");
		} else if(dataLoaded){
			new ErrorView("Please wait a few seconds for the initialization of the data handler.");
		} else {
			new ErrorView("No data has been found. Have you imported any data ?");
		}
	}

	//********** Menu Item Methods **********//

	// Menu File //

	private void menuItemNewDataSelected(){
		fileChooser.setTitle("Choose a file containing your data");
		File file = fileChooser.showOpenDialog(mainStage);
		if(file != null){
			if(firstFile) {
				new Thread(() -> OCLController.initialize(file.getAbsolutePath())).start();
				firstFile = false;
			} else {
				new Thread(() -> OCLController.reload(file.getAbsolutePath())).start();
			}
			dataLoaded = true;
		}else{
			new ErrorView("No file was selected !");
		}
	}

	private void menuItemSavePatternSelected(){
		fileChooser.setTitle("Choose a file where to export your results");
		File file = fileChooser.showSaveDialog(mainStage);
		if (file != null){
			OCLController.exportInterestingPatterns(file.getAbsolutePath());
		}
	}

	private void menuItemSaveStateSelected(){
		if(!OCMManager.algorithmManagerIsMining()) {
			fileChooser.setTitle("Choose a file where to serialize your current research");
			File file = fileChooser.showSaveDialog(mainStage);
			if (file != null) {
				InfoView infoView = new InfoView("Saving your state", "Currently saving your state.");
				infoView.setDisableContinue(true);

				new Thread(new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						new OCMSerializer(file.getAbsolutePath());
						Thread.sleep(500);

						infoView.setProgress(1.);
						infoView.setDisableContinue(false);

						return null;
					}
				}).start();
			}
		}else{
			new ErrorView("You should pause the mining algorithms before saving your current search state.");
		}
	}

	private void menuItemLoadStateSelected(){
		if(!OCMManager.algorithmManagerIsMining()) {
			new LoadStateView();
		}else{
			new ErrorView("You should pause the mining algorithms before loading any search state.");
		}
	}

	// Menu Edit //

	private void menuItemSettingsSelected(){
		new SettingsView();
	}

	// Menu Monitor //

	private void menuItemNumberAlgorithmLaunchedSelected(){
		new MonitorView(MonitorView.MonitorType.ALGORITHM);
	}

	private void menuItemBanditWeightsSelected(){
		new MonitorView(MonitorView.MonitorType.BANDIT);
	}

	private void menuItemCoactiveWeightsSelected(){
		new MonitorView(MonitorView.MonitorType.COACTIVE);
	}

	// Menu Help //

	private void menuItemHelpSelected(){
		new ErrorView("This menu is currently unavailable :c"); //TODO -- Faire une fenêtre d'aide pour l'utilisateur.
	}

	private void menuItemAboutSelected(){
		new AboutView();
	}
}
