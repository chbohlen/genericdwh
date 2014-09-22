package genericdwh.gui.subwindows.editor.subwindows.dialogpopup;

import genericdwh.gui.general.Icons;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class ValidationFailurePopupDialogController extends DialogPopupController {
	
	private boolean closeEditor;

	public void createWindow(String message, boolean closeEditor) {
		super.createWindow("Validation Failure", this.getClass(), Icons.WARNING_DIALOG);
		lMessage1.setText("Could not save changes:");
		lMessage2.setText(message);
		btnYes.setText("OK");
		buttonContainer.getChildren().remove(btnNo);
		buttonContainer.getChildren().remove(btnCancel);
		
		this.closeEditor = closeEditor;
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		stage.close();
		if (closeEditor) {
			Main.getContext().getBean(EditorController.class).close();
		}
	}
}

