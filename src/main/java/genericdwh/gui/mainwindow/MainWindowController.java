package genericdwh.gui.mainwindow;

import java.util.ArrayList;
import java.util.TreeMap;

import genericdwh.main.Main;
import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.ratio.RatioRelation;
import genericdwh.db.DatabaseController;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.sidebar.SidebarController;
import genericdwh.gui.subwindows.ConnectWindowController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindowController {
	
	private Stage stage;
	
	private ConnectWindowController connectWindowController;
	private SidebarController sidebarController;
	private QueryPaneController queryPaneController;
	
	private DataObject draggedDataObject;
	
	public MainWindowController(ConnectWindowController connectWindowController, SidebarController sidebarController, QueryPaneController queryPaneController) {
		this.connectWindowController = connectWindowController;
		this.sidebarController = sidebarController;
		this.queryPaneController = queryPaneController;
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
	
	public void buildSidebars(TreeMap<Long, DimensionCategory> dimensionCategories, ArrayList<DimensionHierarchy> hierarchies, TreeMap<Long, Dimension> dimensions, 
			TreeMap<Long, RatioCategory> ratioCategories, TreeMap<Long, Ratio> ratios) {
		
		sidebarController.buildSidebars(dimensionCategories, hierarchies, dimensions, ratioCategories, ratios);
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
	
	public void setDraggedDataObject(DataObject draggedTreeItem) {
		this.draggedDataObject = draggedTreeItem;
	}
	
	public DataObject getDraggedDataObject() {
		return draggedDataObject;
	}
		
	@FXML protected void menuBarExitOnClickHandler() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		stage.close();
    }

	@FXML public void menuBarConnectOnClickHandler() {
		connectWindowController.createWindow();
	}

	@FXML public void menuBarDisconnectOnClickHandler() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		hideSidebars();
		hideQueryPane();
	}
}
