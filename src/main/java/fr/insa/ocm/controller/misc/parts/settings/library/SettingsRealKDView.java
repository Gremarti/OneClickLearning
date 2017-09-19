package fr.insa.ocm.controller.misc.parts.settings.library;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SettingsRealKDView {

	@FXML private VBox mainVBox = null;

	public SettingsRealKDView(){
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/settings/library/settingsRealKDView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//********** Getters/Setters methods **********//

	// Getters //

	public VBox getMainVBox(){
		return mainVBox;
	}
}
