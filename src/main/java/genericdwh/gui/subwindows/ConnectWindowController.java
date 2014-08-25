package genericdwh.gui.subwindows;

import java.util.Properties;

import genericdwh.configfiles.ConfigFileReader;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.RatioManager;
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
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class ConnectWindowController {
	
	@FXML private TextField tfIp;
	@FXML private TextField tfPort;
	@FXML private TextField tfDbName;
	@FXML private TextField tfUserName;
	@FXML private PasswordField tfPassword;
	
	@FXML private Button btnConnect;
	@FXML private Button btnCancel;

	public ConnectWindowController() {
	}
	
	public void createWindow() {
		try {
			Parent root = SpringFXMLLoader.load(getClass().getResource("ConnectWindow.fxml"));
			Scene scene = new Scene(root, 305, 240);
			Stage stage = new Stage();
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
		//boolean connected  = Main.getContext().getBean(DatabaseController.class).connect(tfIp.getText(), tfPort.getText(), tfDbName.getText() , tfUserName.getText(), tfPassword.getText());
		
		if (connected) {
			storeDbCredentials();
			
			DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
			dimManager.loadCategories();
			dimManager.loadDimensions();
			
			RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
			ratioManager.loadCategories();
			ratioManager.loadRatios();
			
			MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
			mainWindowController.buildSidebars(dimManager.getCategories(), dimManager.getHierarchies(), dimManager.getDimensions(),
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
		Properties dbCredentials = Main.getContext().getBean(ConfigFileReader.class).load("dbCredentials.properties");
		boolean loaded = dbCredentials.size() > 0;
		
		if (loaded) {
			tfIp.setText(dbCredentials.getProperty("ip"));
			tfPort.setText(dbCredentials.getProperty("port"));
			tfDbName.setText(dbCredentials.getProperty("dbName"));
			tfUserName.setText(dbCredentials.getProperty("userName"));
		}
		
		return loaded;
	}

	private void closeWindow() {
		((Stage)btnCancel.getScene().getWindow()).close();
	}
	
	@FXML public void buttonConnectOnClickHandler() {		
		connectToDatabase();
	}

	@FXML public void buttonCancelOnClickHandler() {
		closeWindow();
	}

	@FXML public void onKeyPressedHandler(KeyEvent event) {
		KeyCode pressedKeyCode = event.getCode();
		
		if (pressedKeyCode == KeyCode.ESCAPE) {
			closeWindow();
		}
		else if (pressedKeyCode == KeyCode.ENTER) {
			connectToDatabase();
		}
		
		event.consume();
	}
}
