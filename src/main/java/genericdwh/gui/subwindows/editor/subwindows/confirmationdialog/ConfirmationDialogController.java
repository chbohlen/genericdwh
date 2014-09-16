package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.gui.SpringFXMLLoader;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public abstract class ConfirmationDialogController {
	
	protected Stage stage;
	
	public void createWindow(String title, Class<?> controllerClass) {
		try {
			SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource("/fxml/subwindows/editor/ConfirmationDialog.fxml"), Main.getContext().getBean(controllerClass));
			Parent root = loader.load();
			Scene scene = new Scene(root, 255, 85);
			stage = new Stage();
			stage.setScene(scene);
			stage.setTitle(title);
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FXML public void buttonYesOnClickHandler() {
	}

	@FXML public void buttonNoOnClickHandler() {
	}
	
	@FXML public void buttonCancelOnClickHandler() {
	}
}
