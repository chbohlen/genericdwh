package genericdwh.gui.subwindows.editor;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ResourceBundle;

import genericdwh.dataobjects.ChangeManager;
import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.DeleteObjectDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.DiscardChangesDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.SaveChangesDialogController;
import genericdwh.main.Main;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class EditorController implements Initializable{
	
	private ChangeManager changeManager;
	
	private EditorSidebarController sidebarController;
	private EditingViewController editingViewController;
	
	private SaveChangesDialogController saveChangesDialogController;
	private DiscardChangesDialogController discardChangesDialogController;
	
	private DeleteObjectDialogController deleteObjectDialogController;
	
	private Stage stage;
	
	@FXML MenuItem miSave;
	@FXML MenuItem miDiscard;
		
	public EditorController(ChangeManager changeManager, EditorSidebarController sidebarController, EditingViewController resultViewController,
			SaveChangesDialogController saveChangesDialogController, DiscardChangesDialogController discardChangesDialogController,
			DeleteObjectDialogController deleteObjectDialogController) {
		
		this.changeManager = changeManager;
		
		this.sidebarController = sidebarController;
		this.editingViewController = resultViewController;
		
		this.saveChangesDialogController = saveChangesDialogController;
		this.discardChangesDialogController = discardChangesDialogController;
		
		this.deleteObjectDialogController = deleteObjectDialogController;
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
		sidebarController.createSidebar();
		editingViewController.hideEditingView();		
		
		editingViewController.setHasUnsavedChanges(false);
		
		miSave.disableProperty().bind(Bindings
				.when(editingViewController.getHasUnsavedChanges())
				.then(false)
				.otherwise(true));
		
		miDiscard.disableProperty().bind(Bindings
				.when(editingViewController.getHasUnsavedChanges())
				.then(false)
				.otherwise(true));
	}
	
	public void createEditorTreeTable(int id) {
		editingViewController.createEditorTreeTable(id);
		editingViewController.showEditingView();
	}
	
	@FXML public void menuBarSaveOnClickHandler() {
		showSaveChangesDialog();
	}
	
	@FXML public void menuBarDiscardOnClickHandler() {
		showDiscardChangesDialog();
	}
	
	@FXML public void menuBarExitOnClickHandler() {
		stage.close();
	}
	
	public void showSaveChangesDialog() {
		saveChangesDialogController.createWindow();
	}
	
	public void showDiscardChangesDialog() {
		discardChangesDialogController.createWindow();
	}
	
	public void saveChanges() {
		editingViewController.saveChanges();
		changeManager.saveChanges();
	}
	
	public void discardChanges() {
		editingViewController.discardChanges();
		changeManager.discardChanges();
	}
	
	public void stageUpdate(DataObject obj) {
		changeManager.stageUpdate(obj);
	}
	
	public void stageDeletion(TreeItem<DataObject> tiObj) {
		editingViewController.deleteObject(tiObj);
		changeManager.stageDeletion(tiObj.getValue());
	}
	
	public void confirmDeletion(TreeItem<DataObject> tiObjToDelete) {
		deleteObjectDialogController.createWindow(tiObjToDelete);
	}

	public void duplicateObject(DataObject obj) {
		
	}

	public DataObject addObject(Class<? extends DataObject> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		
		Object[] params = null;
		if (clazz == Dimension.class) {
			params = new Object[] { -1, "New Dimension", -1 };
		} else if (clazz == DimensionCategory.class) {
			params = new Object[] { -1, "New Category" };
		} else if (clazz == Fact.class) {
			params = new Object[] { -1, -1, 0, -1 };
		} else if (clazz == Ratio.class) {
			params = new Object[] { -1, "New Ratio", -1 };
		}  else if (clazz == RatioCategory.class) {
			params = new Object[] { -1, "New Category" };
		} else if (clazz == ReferenceObject.class) {
			params = new Object[] { -1, -1, "New Reference Object" };
		} else if (clazz == Dimension.class) {
			params = new Object[] { -1, "New Unit", "" };
		}
		
		DataObject newObj = null;
		try {
			newObj = (DataObject)constructors[0].newInstance(params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		changeManager.stageCreation(newObj);
		
		return newObj;
	}
}
