package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SaveChangesDialogController extends ConfirmationDialogController {
	
	@FXML private Label lMessage;
	
	public void createWindow() {
		super.createWindow("Save Changes", this.getClass());
		lMessage.setText("Save all changes made to the core data?");
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		
	}

	@Override
	@FXML public void buttonCancelOnClickHandler() {
		stage.close();
	}
}
