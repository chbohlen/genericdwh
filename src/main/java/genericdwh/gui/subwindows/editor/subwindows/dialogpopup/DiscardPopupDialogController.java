package genericdwh.gui.subwindows.editor.subwindows.dialogpopup;

import genericdwh.gui.general.Icons;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class DiscardPopupDialogController extends DialogPopupController {
			
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
