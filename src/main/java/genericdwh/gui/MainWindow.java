package genericdwh.gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow {

	private Stage stage;
	
	public MainWindow() {
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
}