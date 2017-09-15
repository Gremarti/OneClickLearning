package fr.insa.ocm.view.misc.parts.settings.library;


import fr.insa.ocm.model.DebugLogger;
import fr.insa.ocm.model.wrapper.spmf.AlgorithmLauncherSPMF;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SettingsSPMFView {

	@FXML private VBox mainVBox = null;

	@FXML private TextField minsupLCM = null;
	@FXML private TextField minsupBIDEPlus = null;
	@FXML private TextField minsupVMSP = null;
	@FXML private TextField maxgapVMSP = null;
	@FXML private TextField minsupPFPM = null;
	@FXML private TextField minPeriodicity = null;
	@FXML private TextField maxPeriodicity = null;

	public SettingsSPMFView(){
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/misc/parts/settings/library/settingsSPMFView.fxml"));
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//********** Initialization methods **********//

	@FXML
	public void initialize(){
		minsupLCM.setText(AlgorithmLauncherSPMF.getMinsupLCM() + "");
		minsupBIDEPlus.setText(AlgorithmLauncherSPMF.getMinsupBIDEPlus() + "");
		minsupVMSP.setText(AlgorithmLauncherSPMF.getMinsupVMSP() + "");
		maxgapVMSP.setText(AlgorithmLauncherSPMF.getMaxgapVMSP() + "");
		minsupPFPM.setText(AlgorithmLauncherSPMF.getMinsupPFPM() + "");
		minPeriodicity.setText(AlgorithmLauncherSPMF.getMinPeriodicityPFPM() + "");
		maxPeriodicity.setText(AlgorithmLauncherSPMF.getMaxPeriodicityPFPM() + "");
	}

	//********** Getters/Setters methods **********//

	// Getters //

	public VBox getMainVBox(){
		return mainVBox;
	}

	public void setSettingsAlgorithmLauncher(){
		this.setMinsupLCMValue();
		this.setMinsupBIDEPlusValue();
		this.setMinsupVMSPValue();
		this.setMaxgapVMSPValue();
		this.setMinsupPFPMValue();
		this.setMinPeriodicityValue();
		this.setMaxPeriodicityValue();
	}

	//********** Internal methods **********//

	private void setMinsupLCMValue(){
		try{
			AlgorithmLauncherSPMF.setMinsupLCM(Double.valueOf(minsupLCM.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsSPMFView: Impossible to get the value for the minsupLSM setting", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	private void setMinsupBIDEPlusValue(){
		try{
			AlgorithmLauncherSPMF.setMinsupBIDEPlus(Double.valueOf(minsupBIDEPlus.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsSPMFView: Impossible to get the value for the minsupBIDEPlus setting", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	private void setMinsupVMSPValue(){
		try{
			AlgorithmLauncherSPMF.setMinsupVMSP(Double.valueOf(minsupVMSP.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsSPMFView: Impossible to get the value for the minsupVMSP setting", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	private void setMaxgapVMSPValue(){
		try{
			AlgorithmLauncherSPMF.setMaxgapVMSP(Integer.valueOf(maxgapVMSP.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsSPMFView: Impossible to get the value for the maxgapVMSP setting", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	private void setMinsupPFPMValue(){
		try{
			AlgorithmLauncherSPMF.setMinsupPFPM(Double.valueOf(minsupPFPM.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsSPMFView: Impossible to get the value for the minsupPFPM setting", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	private void setMinPeriodicityValue(){
		try{
			AlgorithmLauncherSPMF.setMinPeriodicityPFPM(Integer.valueOf(minPeriodicity.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsSPMFView: Impossible to get the value for the minPeriodicity setting", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

	private void setMaxPeriodicityValue(){
		try{
			AlgorithmLauncherSPMF.setMaxPeriodicityPFPM(Integer.valueOf(maxPeriodicity.getText()));
		} catch (NumberFormatException e){
			DebugLogger.printDebug("SettingsSPMFView: Impossible to get the value for the maxPeriodicity setting", DebugLogger.MessageSeverity.MEDIUM);
		}
	}

}
