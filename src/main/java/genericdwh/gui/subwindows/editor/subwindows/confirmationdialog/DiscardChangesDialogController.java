package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DiscardChangesDialogController extends ConfirmationDialogController {
	
	@FXML private HBox buttonContainer;
	@FXML private Label lMessage;
	@FXML private Button btnCancel;
	
	public void createWindow() {
		super.createWindow("Discard Changes", this.getClass());
		lMessage.setText("Discard all changes made to the core data?");
		buttonContainer.getChildren().remove(btnCancel);
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		Main.getContext().getBean(EditorController.class).discardChanges();
		stage.close();
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		stage.close();
	}
}
