package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.dataobjects.DataObject;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

public class DeleteDialogController extends ConfirmationDialogController {
		
	private TreeItem<DataObject> tiObjToDelete;
	
	public void createWindow(TreeItem<DataObject> tiObjToDelete) {
		super.createWindow("Delete Object", this.getClass());
		lMessage.setText("Do you want to delete the selected Object?");
		buttonContainer.getChildren().remove(btnCancel);
		
		this.tiObjToDelete = tiObjToDelete;
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		Main.getContext().getBean(EditorController.class).deleteObject(tiObjToDelete);
		stage.close();
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		stage.close();
	}
}
