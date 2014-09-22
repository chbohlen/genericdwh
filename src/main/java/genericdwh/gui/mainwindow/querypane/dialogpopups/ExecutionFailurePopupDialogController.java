package genericdwh.gui.mainwindow.querypane.dialogpopups;

import javafx.fxml.FXML;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.dialogpopup.DialogPopupController;

public class ExecutionFailurePopupDialogController extends DialogPopupController {
	
	public void createWindow(String message) {
		super.createWindow("Execution Failure", this.getClass(), Icons.WARNING_DIALOG);
		lMessage1.setText("Could not execute query:");
		lMessage2.setText(message);
		btnYes.setText("OK");
		buttonContainer.getChildren().remove(btnNo);
		buttonContainer.getChildren().remove(btnCancel);
	}

	@Override
	@FXML public void buttonYesOnClickHandler() {
		stage.close();
	}
}
