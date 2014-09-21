package genericdwh.gui.subwindows.connectdialog;

import java.util.Properties;

import genericdwh.configfiles.ConfigFileReader;
import genericdwh.db.DatabaseController;
import genericdwh.gui.SpringFXMLLoader;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.StatusMessage;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Modality;
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
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(Main.getContext().getBean(MainWindowController.class).getStage().getScene().getWindow());
			stage.setScene(scene);
			stage.setTitle("Connect to Database");
			stage.getIcons().add(Icons.CONNECT_DIALOG);
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
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);

		mainWindowController.postStatus(StatusMessage.CONNECTING);
		boolean connected  = Main.getContext().getBean(DatabaseController.class).connect("localhost", "3306", "genericdwh" , "root", "root");
//		boolean connected  = Main.getContext().getBean(DatabaseController.class).connect(tfIp.getText(), tfPort.getText(), tfDbName.getText() , tfUserName.getText(), tfPassword.getText());
		
		if (connected) {
			storeDbCredentials();
			mainWindowController.createSidebars();
			mainWindowController.showSidebars();
			mainWindowController.showQueryPane();
			mainWindowController.postStatus(StatusMessage.CONNECTION_OK);
		} else {
			mainWindowController.postStatus(StatusMessage.CONNECTION_FAILED);
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
