package genericdwh.gui;

import java.net.URL;
import java.util.ResourceBundle;

import genericdwh.main.Main;
import genericdwh.dataobjects.DataObject;
import genericdwh.db.DatabaseController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class MainWindowController implements Initializable {
	
	private Stage stage;
	
	private SidebarController sidebarController;
	private ConnectWindowController connectWindowController;
	
	@FXML private TreeView<DataObject> sidebar;
	
	public MainWindowController(ConnectWindowController connectWindowController, SidebarController sidebarController) {
		this.connectWindowController = connectWindowController;
		this.sidebarController = sidebarController;
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

	public void initialize(URL location, ResourceBundle resources) {
		sidebarController.setSidebar(sidebar);
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public void buildSidebar() {
		sidebarController.buildSidebar();
	}
	
	public void showSidebar() {
		sidebarController.showSidebar();
	}
	
	public void hideSidebar() {
		sidebarController.hideSidebar();
	}
		
	@FXML protected void onClickMenuBarExit() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		stage.close();
    }

	@FXML public void onClickMenuBarConnect() {
		connectWindowController.createWindow();
	}

	@FXML public void onClickMenuBarDisconnect() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		hideSidebar();
	}

	@FXML public void onClickContextMenuNewDimension() {
		
	}
}
