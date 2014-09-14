package genericdwh.gui.subwindows.editor;

import java.net.URL;
import java.util.ResourceBundle;

import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.main.Main;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EditorController implements Initializable{
	
	private EditorSidebarController sidebarController;
	
	public EditorController(EditorSidebarController sidebarController) {
		this.sidebarController = sidebarController;
	}
	
	public void createWindow() {
		try {
			SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource("/fxml/subwindows/editor/Editor.fxml"), Main.getContext().getBean(EditorController.class));
			Parent root = loader.load();
			
			double width = Main.getContext().getBean(MainWindowController.class).getStage().getScene().getWidth() - 35;
			double height = Main.getContext().getBean(MainWindowController.class).getStage().getScene().getHeight() - 35;

			Scene scene = new Scene(root, width, height);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Editor");
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sidebarController.createSidebar();	
	}
}
