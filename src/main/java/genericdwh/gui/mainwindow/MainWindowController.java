package genericdwh.gui.mainwindow;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.db.DatabaseController;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.sidebar.MainWindowSidebarController;
import genericdwh.gui.subwindows.connectdialog.ConnectDialogController;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class MainWindowController implements Initializable{
		
	@FXML private Label statusBar;
	@FXML private MenuItem menuBarDisconnect;
	@FXML private MenuItem menuBarOpenEditor;
	
	@Getter private Stage stage;
	
	private MainWindowSidebarController sidebarController;
	private QueryPaneController queryPaneController;
	
	private ConnectDialogController connectDialogController;
	private EditorController editorController;
	
	@ Getter @Setter private DataObject draggedDataObject;
	
	public MainWindowController(MainWindowSidebarController sidebarController, QueryPaneController queryPaneController,
			ConnectDialogController connectDialogController, EditorController editorController) {
		
		this.sidebarController = sidebarController;
		this.queryPaneController = queryPaneController;
		
		this.connectDialogController = connectDialogController;
		this.editorController = editorController;
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		statusBar.getStyleClass().add("statusbar");
		
		DatabaseController dbController = Main.getContext().getBean(DatabaseController.class);
		
		menuBarDisconnect.disableProperty().bind(Bindings
				.when(dbController.getIsConnected())
				.then(false)
				.otherwise(true));
		
		menuBarOpenEditor.disableProperty().bind(Bindings
				.when(dbController.getIsConnected())
				.then(false)
				.otherwise(true));
	}

	
	public void createWindow(Stage stage) {
		try {
			this.stage = stage;
			
			SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource("/fxml/mainwindow/MainWindow.fxml"), Main.getContext().getBean(MainWindowController.class));
			Parent root = loader.load();
			Scene scene = new Scene(root, 1030, 750);
			scene.getStylesheets().add("/css/StatusBarStyle.css");

			scene.widthProperty().addListener(new ChangeListener<Number>() {
			    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
			    	updateLayouts();
			    }
			});
			scene.heightProperty().addListener(new ChangeListener<Number>() {
			    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
			    	updateLayouts();
			    }
			});
			
			stage.setScene(scene);
			stage.setTitle("Generic DWH");
			stage.show();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void createSidebars(TreeMap<Long, DimensionCategory> dimensionCategories, List<DimensionHierarchy> hierarchies, TreeMap<Long, Dimension> dimensions, 
			TreeMap<Long, RatioCategory> ratioCategories, TreeMap<Long, Ratio> ratios) {
		
		sidebarController.createSidebars(dimensionCategories, hierarchies, dimensions, ratioCategories, ratios);
	}
	
	
	public void showSidebars() {
		sidebarController.showSidebars();
	}
	
	public void hideSidebars() {
		sidebarController.hideSidebars();
	}
	
	
	public void showQueryPane() {
		queryPaneController.showQueryPane();
	}
	
	public void hideQueryPane() {
		queryPaneController.hideQueryPane();
	}

	
	public void postStatus(String status) {
		statusBar.setText(status);
	}
	
	public void clearStatus() {
		statusBar.setText("");
	}


	@FXML private void menuBarConnectOnClickHandler() {
		connectDialogController.createWindow();
	}

	@FXML private void menuBarDisconnectOnClickHandler() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		
		hideSidebars();
		hideQueryPane();
	}
	
	@FXML private void menuBarExitOnClickHandler() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		stage.close();
    }

	@FXML private void menuBarOpenEditor() {
		editorController.createWindow();
	}
	
	
	private void updateLayouts() {
		double width = stage.getScene().getWidth();
		double height = stage.getScene().getHeight();
		sidebarController.updateLayout(width, height);
	}

	
	public void addColDimension(DataObject dim) {
		queryPaneController.addColDimension(dim);
	}

	public void addRowDimension(DataObject dim) {
		queryPaneController.addRowDimension(dim);
	}
	
	public void addFilter(DataObject dim) {
		queryPaneController.addFilter(dim);
	}
	
	public void addRatio(DataObject dim) {
		queryPaneController.addRatio(dim);
	}
}
