package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;

public class SaveOrDiscardDialogController extends ConfirmationDialogController {
		
	private int id;
	
	private boolean closeEditor = false;
	
	public void createWindow(int id) {
		super.createWindow("Save or Discard Changes?", this.getClass());
		lMessage.setText("Save or discard all changes made to the core data?");
		
		btnYes.setText("Save");
		btnNo.setText("Discard");
		
		this.id = id;
	}
	
	public void createWindow() {
		createWindow(-1);
		
		closeEditor = true;
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		EditorController editorController = Main.getContext().getBean(EditorController.class);
		editorController.saveChanges(id);
		stage.close();
		if (closeEditor) {
			editorController.close();
		}
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
