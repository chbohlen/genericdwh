package genericdwh.gui.mainwindow;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.connectwindow.ConnectWindowController;
import genericdwh.gui.editor.EditorController;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.StatusMessages;
import genericdwh.gui.general.statusbar.StatusBarController;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.sidebar.MainWindowSidebarController;
import genericdwh.main.Main;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

public class MainWindowController implements Initializable{
		
	@FXML private MenuItem menuBarDisconnect;
	@FXML private MenuItem menuBarOpenEditor;
	@FXML private MenuItem menuBarToggleSQLQueries;
	
	@Getter private Stage stage;
	
	private StatusBarController statusBarController;
	
	private MainWindowSidebarController sidebarController;
	private QueryPaneController queryPaneController;
	
	private ConnectWindowController connectWindowController;
	private EditorController editorController;
	
	@FXML private TextArea taPlaintextQueries;
	
	@Getter @Setter private DataObject draggedDataObject;
	
	public MainWindowController(StatusBarController statusBarController,
			MainWindowSidebarController sidebarController, QueryPaneController queryPaneController,
			ConnectWindowController connectWindowController, EditorController editorController) {
		
		this.statusBarController = statusBarController;
		
		this.sidebarController = sidebarController;
		this.queryPaneController = queryPaneController;
		
		this.connectWindowController = connectWindowController;
		this.editorController = editorController;
	}
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		DatabaseController dbController = Main.getContext().getBean(DatabaseController.class);
		
		menuBarDisconnect.disableProperty().bind(Bindings
				.when(dbController.getIsConnected())
				.then(false)
				.otherwise(true));
		
		menuBarOpenEditor.disableProperty().bind(Bindings
				.when(dbController.getIsConnected())
				.then(false)
				.otherwise(true));
		
		
		menuBarToggleSQLQueries.disableProperty().bind(Bindings
				.when(queryPaneController.getExecutedQuery())
				.then(false)
				.otherwise(true));
		
		menuBarToggleSQLQueries.textProperty().bind(Bindings
				.when(taPlaintextQueries.visibleProperty())
				.then("Hide")
				.otherwise("Show"));
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
			stage.getIcons().add(Icons.MAIN_WINDOW);
			stage.show();
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void createSidebars() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		dimManager.loadCategories();
		dimManager.loadDimensions();
		dimManager.loadHierarchies();
		
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
		ratioManager.loadCategories();
		ratioManager.loadRatios();
		
		Main.getContext().getBean(UnitManager.class).loadUnits();
					
		sidebarController.createSidebars(dimManager.getCategories(), dimManager.getHierarchies(),  dimManager.getDimensions(),
										ratioManager.getCategories(), ratioManager.getRatios());
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
	
	private void clearQueryPane() {
		queryPaneController.clear();
	}
	
	public void postStatus(String status, Image icon) {
		statusBarController.postStatus(status, icon);
	}
	
	public void clearStatus() {
		statusBarController.clearStatus();
	}


	@FXML private void menuBarConnectOnClickHandler() {
		connectWindowController.createWindow();
	}

	@FXML private void menuBarDisconnectOnClickHandler() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		
		clearQueryPane();
		
		hideSidebars();
		hideQueryPane();
		
		postStatus(StatusMessages.DISCONNECTED, Icons.NOTIFICATION);
	}
	
	@FXML private void menuBarExitOnClickHandler() {
		Main.getContext().getBean(DatabaseController.class).disconnect();
		stage.close();
    }

	@FXML private void menuBarOpenEditor() {
		editorController.createWindow();
	}
	
	@FXML public void menuBarToggleSQLQueries() {
		if (!taPlaintextQueries.isVisible()) {
			if (taPlaintextQueries.getText().equals("")) {
				List<String> lastQueries = Main.getContext().getBean(DatabaseReader.class).getLastQueries();
				String queries = "";
				for (String query : lastQueries) {
					queries += query;
					if (!lastQueries.get(lastQueries.size() - 1).equals(query)) {
						queries += "\n\n";
					}
				}
				taPlaintextQueries.setText(queries);	
			}
			taPlaintextQueries.setVisible(true);
			taPlaintextQueries.toFront();
		} else {
			hideSQLQueries();
		}
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

	public void refresh() {
		createSidebars();
		clearQueryPane();
	}
	
	public void enableLastSQLQueryButton() {
		menuBarToggleSQLQueries.setDisable(false);
	}
	
	public void disableLastSQLQueryButton() {
		menuBarToggleSQLQueries.setDisable(true);
	}
	
	public void hideSQLQueries() {
		taPlaintextQueries.setVisible(false);
	}
}
