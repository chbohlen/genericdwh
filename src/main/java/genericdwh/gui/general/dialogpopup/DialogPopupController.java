package genericdwh.gui.general.dialogpopup;

import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public abstract class DialogPopupController {
	
	@FXML protected HBox buttonContainer;
	@FXML protected Label lMessage1;
	@FXML protected Label lMessage2;
	@FXML protected Button btnYes;
	@FXML protected Button btnNo;
	@FXML protected Button btnCancel;
		
	protected Stage stage;
	
	public void createWindow(String title, Class<?> controllerClass, Image icon) {
		try {
			SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource("/fxml/dialogpopup/DialogPopup.fxml"), Main.getContext().getBean(controllerClass));
			Parent root = loader.load();
			Scene scene = new Scene(root, 285, 85);
			stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(Main.getContext().getBean(MainWindowController.class).getStage().getScene().getWindow());
			stage.setScene(scene);
			stage.setTitle(title);
			stage.getIcons().add(icon);
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FXML public void buttonYesOnClickHandler() {}

	@FXML public void buttonNoOnClickHandler() {}
	
	@FXML public void buttonCancelOnClickHandler() {}
}
