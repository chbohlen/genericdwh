package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class DiscardDialogController extends ConfirmationDialogController {
			
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
