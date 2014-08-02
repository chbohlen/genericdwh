package genericdwh.gui;

import genericdwh.db.DatabaseController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class MainWindowController {
	
	private Stage stage;
	
	public MainWindowController() {
	}
	
	public void createWindow(Stage stage) {
		try {
			this.stage = stage;
			
			Parent root = SpringFXMLLoader.load(getClass().getResource("MainWindow.fxml"));
			Scene scene = new Scene(root, 1000, 750);
			stage.setScene(scene);
			stage.setTitle("Generic DWH");
			stage.show();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Stage getStage() {
		return stage;
	}
	
	@FXML private TreeView<?> treeView;
	public void showTreeView() {
		treeView.setVisible(true);
	}
	public void hideTreeView() {
		treeView.setVisible(false);
	}
		
	@FXML protected void onClickMenuBarExit() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		Main.getContext().getBean(MainWindowController.class).getStage().close();
    }

	@FXML public void onClickMenuBarConnect() {
		Main.getContext().getBean(ConnectWindowController.class).createWindow();
	}

	@FXML public void onClickMenuBarDisconnect() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		hideTreeView();
	}

	@FXML public void onClickContextMenuCreateDimension() {
		
	}
}
