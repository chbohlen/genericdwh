package genericdwh.gui.editor.dialogpopups;

import genericdwh.gui.editor.EditorController;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.dialogpopup.DialogPopupController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class DiscardDialogPopupController extends DialogPopupController {
			
	public void createWindow() {
		super.createWindow("Discard?", this.getClass(), Icons.CONFIRMATION_DIALOG);
		lMessage1.setText("Discard all changes made to the core data?");
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
