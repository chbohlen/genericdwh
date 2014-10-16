package genericdwh.gui.editor.dialogpopups;

import genericdwh.gui.editor.EditorController;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.dialogpopup.DialogPopupController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class SaveFailureDialogPopupController extends DialogPopupController {
	
	private boolean closeEditor;

	public void createWindow(boolean closeEditor) {
		super.createWindow("Validation Failure", this.getClass(), Icons.WARNING_DIALOG);
		lMessage1.setText("Could not save any or all changes.");
		lMessage2.setText("Please check the log for further informations.");
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

