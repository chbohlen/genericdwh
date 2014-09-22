package genericdwh.gui.editor.dialogpopups;

import genericdwh.gui.editor.EditorController;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.dialogpopup.DialogPopupController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class SaveOrDiscardDialogPopupController extends DialogPopupController {
		
	private int id;
	
	private boolean closeEditor;
	
	public void createWindow(int id) {
		super.createWindow("Save or Discard?", this.getClass(), Icons.CONFIRMATION_DIALOG);
		lMessage1.setText("Save or discard all changes?");
		
		btnYes.setText("Save");
		btnNo.setText("Discard");
		
		this.id = id;
		
		closeEditor = false;
	}
	
	public void createWindow() {
		createWindow(-1);
		
		closeEditor = true;
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		EditorController editorController = Main.getContext().getBean(EditorController.class);
		stage.close();
		editorController.saveChanges(id);
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		EditorController editorController = Main.getContext().getBean(EditorController.class);
		editorController.discardChanges(id);
		stage.close();
		if (closeEditor) {
			editorController.close();
		}
	}

	@Override
	@FXML public void buttonCancelOnClickHandler() {
		stage.close();
	}
}
