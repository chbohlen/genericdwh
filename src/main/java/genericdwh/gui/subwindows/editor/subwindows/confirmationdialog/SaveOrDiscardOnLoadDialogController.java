package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class SaveOrDiscardOnLoadDialogController extends ConfirmationDialogController {
		
	private int id;
	
	public void createWindow(int id) {
		super.createWindow("Save or Discard Changes?", this.getClass());
		lMessage.setText("Save or discard all changes made to the core data?");
		
		btnYes.setText("Save");
		btnNo.setText("Discard");
		
		this.id = id;
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		Main.getContext().getBean(EditorController.class).saveChanges(id);
		stage.close();
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		Main.getContext().getBean(EditorController.class).discardChanges(id);
		stage.close();
	}

	@Override
	@FXML public void buttonCancelOnClickHandler() {
		stage.close();
	}
}
