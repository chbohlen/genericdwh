package genericdwh.gui;

import genericdwh.db.DatabaseController;
import genericdwh.main.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ConnectWindowController {
	
	@FXML private TextField ip;
	@FXML private TextField port;
	@FXML private TextField database;
	@FXML private TextField user;
	@FXML private PasswordField password;
	
	@FXML private Button btnConnect;
	@FXML private Button btnCancel;

	@FXML public void handleButtonConnect() {
		MainApp.getContext().getBean(DatabaseController.class).connect(ip.getText(), port.getText(), database.getText() , user.getText(), password.getText());
		((Stage)btnConnect.getScene().getWindow()).close();
	}

	@FXML public void handleButtonCancel() {
		((Stage)btnCancel.getScene().getWindow()).close();
	}
}
