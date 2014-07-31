package genericdwh.gui;

import genericdwh.db.DatabaseController;
import genericdwh.main.Main;import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
			Parent root = FXMLLoader.load(getClass().getResource("ConnectWindow.fxml"));
			Scene scene = new Scene(root, 305, 240);
			Stage stage = new Stage();
			stage.setScene(scene);
			stage.show();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@FXML public void handleButtonConnect() {
		Main.getContext().getBean(DatabaseController.class).connect(tfIp.getText(), tfPort.getText(), tfDatabase.getText() , tfUser.getText(), tfPassword.getText());
		((Stage)btnConnect.getScene().getWindow()).close();
	}

	@FXML public void handleButtonCancel() {
		((Stage)btnCancel.getScene().getWindow()).close();
	}
}
