package genericdwh.gui.subwindows.editor.subwindows.confirmationdialog;

import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SaveChangesDialogController extends ConfirmationDialogController {
	
	@FXML private Label lMessage;
	
	public void createWindow() {
		super.createWindow("Save Changes", this.getClass());
		lMessage.setText("Save all changes made to the core data?");
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		Main.getContext().getBean(EditorController.class).saveChanges();
		stage.close();
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
