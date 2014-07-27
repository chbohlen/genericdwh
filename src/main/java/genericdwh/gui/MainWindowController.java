package genericdwh.gui;

import genericdwh.db.DatabaseController;
import genericdwh.main.MainApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindowController {
		
	@FXML protected void handleMenuItemExit() {
		MainApp.getContext().getBean(DatabaseController.class).disconnect();
		MainApp.getContext().getBean(MainWindow.class).getStage().close();
    }

	@FXML public void handleMenuItemConnect() {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("ConnectWindow.fxml"));
			Scene scene = new Scene(root, 305, 240);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@FXML public void handleMenuItemDisconnect() {
		MainApp.getContext().getBean(DatabaseController.class).disconnect();
	}
}
