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
import genericdwh.gui.general.StatusMessages;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.statusbar.StatusBarController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.DeletePopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.DiscardPopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.SavePopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.SaveOrDiscardPopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.ValidationFailurePopupDialogController;
import genericdwh.main.Main;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.Getter;

public class EditorController implements Initializable{
	
	private ChangeManager changeManager;
	
	private StatusBarController statusBarController;
	
	private EditorSidebarController sidebarController;
	private EditingViewController editingViewController;
	
	private SavePopupDialogController saveChangesDialogPopupController;
	private ValidationFailurePopupDialogController validationFailurePopupDialogController;
	private DiscardPopupDialogController discardChangesDialogPopupController;
	private SaveOrDiscardPopupDialogController saveOrDiscardOnLoadDialogPopupController;
	private DeletePopupDialogController deleteObjectDialogPopupController;
	
	@Getter private Stage stage;
	
	@FXML MenuItem miSave;
	@FXML MenuItem miDiscard;
		
	public EditorController(ChangeManager changeManager,
			StatusBarController statusBarController,
			EditorSidebarController sidebarController, EditingViewController resultViewController,
			SavePopupDialogController saveChangesDialogPopupController,
			ValidationFailurePopupDialogController validationFailurePopupDialogController,
			DiscardPopupDialogController discardChangesDialogPopupController,
			SaveOrDiscardPopupDialogController saveOrDiscardOnLoadDialogPopupController,
			DeletePopupDialogController deleteObjectDialogPopupController) {
		
		this.changeManager = changeManager;
		
		this.statusBarController = statusBarController;
		
		this.sidebarController = sidebarController;
		this.editingViewController = resultViewController;
		
		this.saveChangesDialogPopupController = saveChangesDialogPopupController;
		this.validationFailurePopupDialogController = validationFailurePopupDialogController;
		this.discardChangesDialogPopupController = discardChangesDialogPopupController;
		this.saveOrDiscardOnLoadDialogPopupController = saveOrDiscardOnLoadDialogPopupController;
		this.deleteObjectDialogPopupController = deleteObjectDialogPopupController;
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
			saveOrDiscardOnLoadDialogPopupController.createWindow(id);
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
		saveChangesDialogPopupController.createWindow();
	}
	
	@FXML public void menuBarDiscardOnClickHandler() {
		discardChangesDialogPopupController.createWindow();
	}
	
	@FXML public void menuBarExitOnClickHandler() {
		serveCloseRequest();
	}
	
	public void saveChanges() {
		saveChanges(editingViewController.getCurrEditingViewType().ordinal());
	}
	
	public void saveChanges(int id) {
		String validationResult = changeManager.validateChanges();
		if (validationResult != null) {
			if (id == -1) {
				editingViewController.setHasUnsavedChanges(false);
				validationFailurePopupDialogController.createWindow(validationResult, true);
				MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
				mainWindowController.refresh();
				mainWindowController.postStatus(StatusMessages.VALIDATION_FAILED, Icons.WARNING);
			} else {
				validationFailurePopupDialogController.createWindow(validationResult, false);
				postStatus(StatusMessages.VALIDATION_FAILED, Icons.WARNING);
			}
			return;
		}
		
		changeManager.saveChanges();
		postStatus(StatusMessages.CHANGES_SAVED, Icons.NOTIFICATION);
		if (id == -1) {
			editingViewController.setHasUnsavedChanges(false);
			Main.getContext().getBean(MainWindowController.class).postStatus(StatusMessages.CHANGES_SAVED, Icons.NOTIFICATION);
			close();
		} else {
			postStatus(StatusMessages.CHANGES_SAVED, Icons.NOTIFICATION);
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
			Main.getContext().getBean(MainWindowController.class).postStatus(StatusMessages.CHANGES_DISCARDED, Icons.NOTIFICATION);
		} else {
			postStatus(StatusMessages.CHANGES_DISCARDED, Icons.NOTIFICATION);
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
		deleteObjectDialogPopupController.createWindow(tiObjToDelete);
	}

	public DataObject createObject(Class<? extends DataObject> clazz) {
		Constructor<?>[] constructors = clazz.getConstructors();
		
		Object[] params = null;
		if (clazz == Dimension.class) {
			params = new Object[] { -1, "New Dimension", 0 };
		} else if (clazz == ReferenceObject.class) {
			params = new Object[] { -1, -1, "New Reference Object" };
		} else if (clazz == DimensionCategory.class) {
			params = new Object[] { -1, "New Category" };
		} else if (clazz == Fact.class) {
			params = new Object[] { -1, -1, 0, 0 };
		} else if (clazz == Ratio.class) {
			params = new Object[] { -1, "New Ratio", 0 };
		}  else if (clazz == RatioCategory.class) {
			params = new Object[] { -1, "New Category" };
		} else if (clazz == Unit.class) {
			params = new Object[] { -1, "New Unit", "" };
		}
		
		DataObject newObj = null;
		try {
			if (clazz == DimensionHierarchy.class || clazz == ReferenceObjectHierarchy.class) {
				newObj = (DataObject)clazz.newInstance();
				newObj.setName("New Hierarchy");
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
	
	private void postStatus(String status, Image icon) {
		statusBarController.postStatus(status, icon);
	}
	
	private void clearStatus() {
		statusBarController.clearStatus();
	}
	
	private void serveCloseRequest() {
		if (editingViewController.getHasUnsavedChanges().get()) {
			saveOrDiscardOnLoadDialogPopupController.createWindow();
		} else {
			close();
		}
	}
	
	public void close() {
		stage.close();
	}
}
