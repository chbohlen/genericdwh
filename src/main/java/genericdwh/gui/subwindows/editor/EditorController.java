package genericdwh.gui.subwindows.editor;

import java.net.URL;
import java.util.ResourceBundle;

import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.main.Main;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class EditorController implements Initializable{
	
	private EditorSidebarController sidebarController;
	private EditingViewController resultViewController;
	
	@FXML MenuItem miSave;
	@FXML MenuItem miDiscard;
	
	private Stage stage;
	
	public EditorController(EditorSidebarController sidebarController, EditingViewController resultViewController) {
		this.sidebarController = sidebarController;
		this.resultViewController = resultViewController;
	}
	
	public void createWindow() {
		try {
			SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource("/fxml/subwindows/editor/Editor.fxml"), Main.getContext().getBean(EditorController.class));
			Parent root = loader.load();
			
			double width = Main.getContext().getBean(MainWindowController.class).getStage().getScene().getWidth() - 35;
			double height = Main.getContext().getBean(MainWindowController.class).getStage().getScene().getHeight() - 35;

			Scene scene = new Scene(root, width, height);
			stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Editor");
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		resultViewController.hideEditingView();		
		sidebarController.createSidebar();
		
		miSave.disableProperty().bind(Bindings
				.when(resultViewController.getHasUnsavedChanges())
				.then(false)
				.otherwise(true));
		
		miDiscard.disableProperty().bind(Bindings
				.when(resultViewController.getHasUnsavedChanges())
				.then(false)
				.otherwise(true));
	}
	
	public void createEditorTreeTable(int id) {
		resultViewController.createEditorTreeTable(id);
		resultViewController.showEditingView();
	}
	
	@FXML public void menuBarSaveOnClickHandler() {
	}
	
	@FXML public void menuBarDiscardOnClickHandler() {
	}
	
	@FXML public void menuBarExitOnClickHandler() {
		stage.close();
	}
}
