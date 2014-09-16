package genericdwh.gui.subwindows.editor;

import java.net.URL;
import java.util.ResourceBundle;

import genericdwh.dataobjects.ChangeManager;
import genericdwh.dataobjects.DataObject;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.DiscardChangesDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.SaveChangesDialogController;
import genericdwh.main.Main;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class EditorController implements Initializable{
	
	private ChangeManager changeManager;
	
	private EditorSidebarController sidebarController;
	private EditingViewController resultViewController;
	private SaveChangesDialogController saveChangesDialogController;
	private DiscardChangesDialogController discardChangesDialogController;
	
	private Stage stage;
	
	@FXML MenuItem miSave;
	@FXML MenuItem miDiscard;
		
	public EditorController(ChangeManager changeManager, EditorSidebarController sidebarController, EditingViewController resultViewController,
			SaveChangesDialogController saveChangesDialogController, DiscardChangesDialogController discardChangesDialogController) {
		
		this.changeManager = changeManager;
		
		this.sidebarController = sidebarController;
		this.resultViewController = resultViewController;
		this.saveChangesDialogController = saveChangesDialogController;
		this.discardChangesDialogController = discardChangesDialogController;
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
		showSaveChangesDialog();
	}
	
	@FXML public void menuBarDiscardOnClickHandler() {
		discardChangesDialogController.createWindow();
	}
	
	@FXML public void menuBarExitOnClickHandler() {
		stage.close();
	}

	public DataObject changeName(DataObject obj, String newName) {
		return changeManager.changeName(obj, newName);
	}

	public DataObject changeCategory(DataObject obj, long newCategoryId) {
		return changeManager.changeCategory(obj, newCategoryId);
	}

	public DataObject changeDimension(DataObject obj, long newDimensionId) {
		return changeManager.changeDimension(obj, newDimensionId);
	}

	public void showSaveChangesDialog() {
		saveChangesDialogController.createWindow();
	}
}
