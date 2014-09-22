package genericdwh.gui.editor.dialogpopups;

import genericdwh.dataobjects.DataObject;
import genericdwh.gui.editor.EditorController;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.dialogpopup.DialogPopupController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;

public class DeleteDialogPopupController extends DialogPopupController {
		
	private TreeItem<DataObject> tiObjToDelete;
	
	public void createWindow(TreeItem<DataObject> tiObjToDelete) {
		super.createWindow("Delete Object?", this.getClass(), Icons.CONFIRMATION_DIALOG);
		lMessage1.setText("Do you want to delete the selected object?");
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
