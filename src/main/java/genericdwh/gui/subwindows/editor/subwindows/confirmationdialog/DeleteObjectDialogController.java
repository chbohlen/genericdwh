package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.dataobjects.DataObject;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.HBox;

public class DeleteObjectDialogController extends ConfirmationDialogController {
	
	@FXML private HBox buttonContainer;
	@FXML private Label lMessage;
	@FXML private Button btnCancel;
	
	private TreeItem<DataObject> tiObjToDelete;
	
	public void createWindow(TreeItem<DataObject> tiObjToDelete) {
		super.createWindow("Delete Object", this.getClass());
		lMessage.setText("Do you want to delete the selected Object?");
		buttonContainer.getChildren().remove(btnCancel);
		
		this.tiObjToDelete = tiObjToDelete;
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		Main.getContext().getBean(EditorController.class).stageDeletion(tiObjToDelete);
		stage.close();
	}

	@Override
	@FXML public void buttonNoOnClickHandler() {
		stage.close();
	}
}
