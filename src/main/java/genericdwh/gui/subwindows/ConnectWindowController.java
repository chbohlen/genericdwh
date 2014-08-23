package genericdwh.gui.subwindows;

import genericdwh.dataobjects.DimensionManager;
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
	@FXML private TextField tfDatabase;
	@FXML private TextField tfUser;
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
	}
	
	private void connect() {
		boolean connected  = Main.getContext().getBean(DatabaseController.class).connect("localhost", "3306", "genericdwh" , "root", "root");
		//boolean connected  = Main.getContext().getBean(DatabaseController.class).connect(tfIp.getText(), tfPort.getText(), tfDatabase.getText() , tfUser.getText(), tfPassword.getText());
		((Stage)btnConnect.getScene().getWindow()).close();
		
		if (connected) {
			DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
			dimManager.loadDimensions();
			
			MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
			mainWindowController.buildSidebar(dimManager.getDimensions(), dimManager.getHierachies());
			mainWindowController.showSidebar();
			mainWindowController.showQueryPane();
		}
	}
	
	private void cancel() {
		((Stage)btnCancel.getScene().getWindow()).close();
	}
	
	@FXML public void buttonConnectOnClickHandler() {		
		connect();
	}

	@FXML public void buttonCancelOnClickHandler() {
		cancel();
	}

	@FXML public void onKeyPressedHandler(KeyEvent event) {
		KeyCode pressedKeyCode = event.getCode();
		
		if (pressedKeyCode == KeyCode.ESCAPE) {
			cancel();
		}
		else if (pressedKeyCode == KeyCode.ENTER) {
			connect();
		}
		
		event.consume();
	}
}
