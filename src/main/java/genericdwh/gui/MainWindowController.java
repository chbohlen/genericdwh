package genericdwh.gui;

import genericdwh.db.DatabaseController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindowController {
	
	private Stage stage;
	
	public MainWindowController() {
		
	}
	
	public void createWindow(Stage stage) {
		try {
			this.stage = stage;
			
			Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
			Scene scene = new Scene(root, 700, 500);
			stage.setScene(scene);
			stage.show();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Stage getStage() {
		return stage;
	}
		
	@FXML protected void handleMenuItemExit() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		Main.getContext().getBean(MainWindowController.class).getStage().close();
    }

	@FXML public void handleMenuItemConnect() {
		Main.getContext().getBean(ConnectWindowController.class).createWindow();
	}

	@FXML public void handleMenuItemDisconnect() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
	}
}
