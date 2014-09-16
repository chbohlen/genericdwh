package genericdwh.gui.subwindows.connectdialog;

import java.util.Properties;

import genericdwh.configfiles.ConfigFileReader;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.db.DatabaseController;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ConnectDialogController {
	
	@FXML private TextField tfIp;
	@FXML private TextField tfPort;
	@FXML private TextField tfDbName;
	@FXML private TextField tfUserName;
	@FXML private PasswordField tfPassword;
	
	@FXML private Button btnConnect;
	@FXML private Button btnCancel;
	
	private String lastIp = new String();
	private String lastPort = new String();
	private String lastDbName = new String();
	private String lastUserName = new String();
	
	private Stage stage;

	public ConnectDialogController() {
	}
	
	public void createWindow() {
		try {
			SpringFXMLLoader loader = new SpringFXMLLoader(getClass().getResource("/fxml/subwindows/connectdialog/ConnectDialog.fxml"), Main.getContext().getBean(ConnectDialogController.class));
			Parent root = loader.load();
			Scene scene = new Scene(root, 305, 240);
			stage = new Stage();
			stage.setScene(scene);
			stage.setTitle("Connect to database");
			stage.setResizable(false);
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if (loadDbCredentials()) {
			tfPassword.requestFocus();
		}
	}
	
	private void connectToDatabase() {
		boolean connected  = Main.getContext().getBean(DatabaseController.class).connect("localhost", "3306", "genericdwh" , "root", "root");
//		boolean connected  = Main.getContext().getBean(DatabaseController.class).connect(tfIp.getText(), tfPort.getText(), tfDbName.getText() , tfUserName.getText(), tfPassword.getText());
		
		if (connected) {
			storeDbCredentials();
			
			DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
			dimManager.loadCategories();
			dimManager.loadDimensions();
			
			RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
			ratioManager.loadCategories();
			ratioManager.loadRatios();
			
			Main.getContext().getBean(UnitManager.class).loadUnits();
			
			MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
			
			mainWindowController.createSidebars(dimManager.getCategories(), dimManager.getHierarchies(), dimManager.getDimensions(),
												ratioManager.getCategories(), ratioManager.getRatios());
			mainWindowController.showSidebars();
			mainWindowController.showQueryPane();
		}		
		
		closeWindow();
	}
	
	private void storeDbCredentials() {
		Properties dbCredentials = new Properties();
		dbCredentials.setProperty("ip", tfIp.getText());
		dbCredentials.setProperty("port", tfPort.getText());
		dbCredentials.setProperty("dbName", tfDbName.getText());
		dbCredentials.setProperty("userName", tfUserName.getText());
		
		Main.getContext().getBean(ConfigFileReader.class).store(dbCredentials, "dbCredentials.properties", "last successfully used database credentials");
	}
	
	private boolean loadDbCredentials() {
		boolean loaded = false;
		if (lastIp.isEmpty() && lastPort.isEmpty() && lastDbName.isEmpty() && lastDbName.isEmpty()) {
			Properties dbCredentials = Main.getContext().getBean(ConfigFileReader.class).load("dbCredentials.properties");
			loaded = dbCredentials.size() > 0;
			
			if (loaded) {
				lastIp = dbCredentials.getProperty("ip");
				lastPort = dbCredentials.getProperty("port");
				lastDbName = dbCredentials.getProperty("dbName");
				lastUserName = dbCredentials.getProperty("userName");
			}
		} else {
			loaded = true;
		}
		
		tfIp.setText(lastIp);
		tfPort.setText(lastPort);
		tfDbName.setText(lastDbName);
		tfUserName.setText(lastUserName);
		
		return loaded;
	}

	private void closeWindow() {
		stage.close();
	}
	
	@FXML public void buttonConnectOnClickHandler() {		
		connectToDatabase();
	}

	@FXML public void buttonCancelOnClickHandler() {
		closeWindow();
	}
}
