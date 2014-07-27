package genericdwh.gui;

import genericdwh.db.DatabaseController;
import genericdwh.main.MainApp;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class MainWindowController {
		
	@FXML protected void handleMenuItemExit() {
		MainApp.getContext().getBean(MainWindow.class).getStage().close();
    }

	@FXML public void handleMenuItemConnect() {
		MainApp.getContext().getBean(DatabaseController.class).connect("localhost", "3306", "genericdwh" , "root", "root");
	}

	@FXML public void handleMenuItemDisconnect() {
		MainApp.getContext().getBean(DatabaseController.class).disconnect();
	}
}
