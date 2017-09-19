package fr.insa.ocm;

import fr.insa.ocm.controller.OCLController;
import fr.insa.ocm.controller.mainuis.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class OCLApplication extends Application{

	private static OCLApplication INSTANCE;

	private static Stage mainStage;

	public OCLApplication(){
		INSTANCE = this;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init(){}

	@Override
	public void start(Stage primaryStage) throws IOException {
		mainStage = primaryStage;

		new MainView();
	}

	@Override
	public void stop(){
		OCLController.requestStop();
	}

	public static Stage getMainStage(){return mainStage;}

	public static Application getInstance(){ return INSTANCE; }
}
