package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class DiscardChangesDialogController extends ConfirmationDialogController {
	
	@FXML private Label lMessage;
	@FXML private Button btnCancel;
	
	public void createWindow() {
		super.createWindow("Discard Changes", this.getClass());
		lMessage.setText("Discard all changes made to the core data?");
		btnCancel.setVisible(false);
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		stage.close();
	}
}
