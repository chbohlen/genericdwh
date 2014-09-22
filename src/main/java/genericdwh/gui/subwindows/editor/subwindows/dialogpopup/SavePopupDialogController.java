package genericdwh.gui.subwindows.editor.subwindows.dialogpopup;

import genericdwh.gui.general.Icons;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class SavePopupDialogController extends DialogPopupController {
	
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
