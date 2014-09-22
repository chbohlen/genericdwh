package genericdwh.gui.editor.dialogpopups;

import genericdwh.gui.editor.EditorController;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.dialogpopup.DialogPopupController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class SaveDialogPopupController extends DialogPopupController {
	
	public void createWindow() {
		super.createWindow("Save?", this.getClass(), Icons.CONFIRMATION_DIALOG);
		lMessage1.setText("Save all changes made to the core data?");
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		stage.close();
		Main.getContext().getBean(EditorController.class).saveChanges();
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		stage.close();
	}

	@Override
	@FXML public void buttonCancelOnClickHandler() {
		stage.close();
	}
}
