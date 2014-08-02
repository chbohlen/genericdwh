package genericdwh.gui;

import genericdwh.db.DatabaseController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
	
	@FXML public void onClickButtonConnect() {
		boolean connected  = Main.getContext().getBean(DatabaseController.class).connect("localhost", "3306", "genericdwh" , "root", "root");
		//boolean connected  = Main.getContext().getBean(DatabaseController.class).connect(tfIp.getText(), tfPort.getText(), tfDatabase.getText() , tfUser.getText(), tfPassword.getText());
		((Stage)btnConnect.getScene().getWindow()).close();
		
		if (connected) {
			Main.getContext().getBean(MainWindowController.class).showTreeView();
		}
	}

	@FXML public void onClickButtonCancel() {
		((Stage)btnCancel.getScene().getWindow()).close();
	}
}
