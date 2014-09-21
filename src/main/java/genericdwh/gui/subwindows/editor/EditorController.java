package genericdwh.gui.subwindows.editor;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.ResourceBundle;

import genericdwh.dataobjects.ChangeManager;
import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectHierarchy;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.StatusBarController;
import genericdwh.gui.general.StatusMessage;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.DeleteDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.DiscardDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.SaveDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.SaveOrDiscardDialogController;
import genericdwh.main.Main;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;

public class EditorController implements Initializable{
	
	private ChangeManager changeManager;
	
	private StatusBarController statusBarController;
	
	private EditorSidebarController sidebarController;
	private EditingViewController editingViewController;
	
	private SaveDialogController saveChangesDialogController;
	private DiscardDialogController discardChangesDialogController;
	
	private SaveOrDiscardDialogController saveOrDiscardOnLoadDialogController;
	
	private DeleteDialogController deleteObjectDialogController;
	
	@Getter private Stage stage;
	
	@FXML MenuItem miSave;
	@FXML MenuItem miDiscard;
		
	public EditorController(ChangeManager changeManager,
			StatusBarController statusBarController,
			EditorSidebarController sidebarController, EditingViewController resultViewController,
			SaveDialogController saveChangesDialogController, DiscardDialogController discardChangesDialogController,
			SaveOrDiscardDialogController saveOrDiscardOnLoadDialogController,
			DeleteDialogController deleteObjectDialogController) {
		
		this.changeManager = changeManager;
		
		this.statusBarController = statusBarController;
		
		this.sidebarController = sidebarController;
		this.editingViewController = resultViewController;
		
		this.saveChangesDialogController = saveChangesDialogController;
		this.discardChangesDialogController = discardChangesDialogController;
		
		this.saveOrDiscardOnLoadDialogController = saveOrDiscardOnLoadDialogController;
		
		this.deleteObjectDialogController = deleteObjectDialogController;
	}
	
	public void createWindow() {
		try {
			SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource("/fxml/subwindows/editor/Editor.fxml"), Main.getContext().getBean(EditorController.class));
			Parent root = loader.load();
			
			double width = Main.getContext().getBean(MainWindowController.class).getStage().getScene().getWidth() - 35;
			double height = Main.getContext().getBean(MainWindowController.class).getStage().getScene().getHeight() - 35;

			Scene scene = new Scene(root, width, height);
			scene.getStylesheets().add("/css/StatusBarStyle.css");
			stage = new Stage();
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(Main.getContext().getBean(MainWindowController.class).getStage().getScene().getWindow());
			stage.setScene(scene);
			stage.setTitle("Core Data Editor");
			stage.getIcons().add(Icons.EDITOR_WINDOW);
			
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					serveCloseRequest();
					event.consume();
				}
			});
			
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sidebarController.createSidebar();
		editingViewController.hideEditingView();		
		
		miSave.disableProperty().bind(Bindings
				.when(editingViewController.getHasUnsavedChanges())
				.then(false)
				.otherwise(true));
		
		miDiscard.disableProperty().bind(Bindings
				.when(editingViewController.getHasUnsavedChanges())
				.then(false)
				.otherwise(true));
	}
	
	public void loadEditingView(int id) {
		if (editingViewController.getHasUnsavedChanges().get()) {
			saveOrDiscardOnLoadDialogController.createWindow(id);
		} else {
			createEditorTreeTable(id);
		}
		clearStatus();
	}
	
	public void createEditorTreeTable(int id) {
		editingViewController.createEditorTreeTable(id);
		editingViewController.showEditingView();
	}
	
	@FXML public void menuBarSaveOnClickHandler() {
		saveChangesDialogController.createWindow();
	}
	
	@FXML public void menuBarDiscardOnClickHandler() {
		discardChangesDialogController.createWindow();
	}
	
	@FXML public void menuBarExitOnClickHandler() {
		serveCloseRequest();
	}
	
	public void saveChanges() {
		saveChanges(editingViewController.getCurrEditingViewType().ordinal());
	}
	
	public void saveChanges(int id) {
		changeManager.saveChanges();
		postStatus(StatusMessage.CHANGES_SAVED);
		if (id == -1) {
			editingViewController.setHasUnsavedChanges(false);
			Main.getContext().getBean(MainWindowController.class).postStatus(StatusMessage.CHANGES_SAVED);
		} else {
			postStatus(StatusMessage.CHANGES_SAVED);
			createEditorTreeTable(id);
		}
	}
	
	public void discardChanges() {
		discardChanges(editingViewController.getCurrEditingViewType().ordinal());
	}
	
	public void discardChanges(int id) {
		changeManager.discardChanges();
		if (id == -1) {
			editingViewController.setHasUnsavedChanges(false);
			Main.getContext().getBean(MainWindowController.class).postStatus(StatusMessage.CHANGES_DISCARDED);
		} else {
			postStatus(StatusMessage.CHANGES_DISCARDED);
			createEditorTreeTable(id);
		}
	}

	public void stageCreation(DataObject obj) {
		changeManager.stageCreation(obj);
	}
	
	public void stageUpdate(DataObject obj) {
		changeManager.stageUpdate(obj);
	}
	
	public void stageDeletion(DataObject obj) {
		changeManager.stageDeletion(obj);
	}
	
	public void confirmDeletion(TreeItem<DataObject> tiObjToDelete) {
		deleteObjectDialogController.createWindow(tiObjToDelete);
	}

	public DataObject createObject(Class<? extends DataObject> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		
		Object[] params = null;
		if (clazz == Dimension.class) {
			params = new Object[] { -1, "New Dimension", 0 };
		} else if (clazz == DimensionCategory.class) {
			params = new Object[] { -1, "New Category" };
		} else if (clazz == Fact.class) {
			params = new Object[] { -1, -1, 0, -1 };
		} else if (clazz == Ratio.class) {
			params = new Object[] { -1, "New Ratio", 0 };
		}  else if (clazz == RatioCategory.class) {
			params = new Object[] { -1, "New Category" };
		} else if (clazz == ReferenceObject.class) {
			params = new Object[] { -1, 0, "New Reference Object" };
		} else if (clazz == Unit.class) {
			params = new Object[] { -1, "New Unit", "" };
		}
		
		DataObject newObj = null;
		try {
			if (clazz == DimensionHierarchy.class || clazz == ReferenceObjectHierarchy.class) {
				newObj = (DataObject)clazz.newInstance();
			} else {
				newObj = (DataObject)constructors[0].newInstance(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newObj;
	}
	
	public void deleteObject(TreeItem<DataObject> tiObjToDelete) {
		editingViewController.deleteObject(tiObjToDelete);
	}
	
	private void postStatus(String status) {
		statusBarController.postStatus(status);
	}
	
	private void clearStatus() {
		statusBarController.clearStatus();
	}
	
	private void serveCloseRequest() {
		if (editingViewController.getHasUnsavedChanges().get()) {
			saveOrDiscardOnLoadDialogController.createWindow();
		} else {
			close();
		}
	}
	
	public void close() {
		stage.close();
	}
}
