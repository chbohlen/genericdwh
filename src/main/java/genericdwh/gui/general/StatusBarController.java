package genericdwh.gui.general;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class StatusBarController implements Initializable {
	
	@FXML private Label lStatus;

	public StatusBarController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lStatus.getStyleClass().add("statusbar");
	}
	
	public void postStatus(String status) {
		lStatus.setText(status);
	}
	
	public void clearStatus() {
		lStatus.setText("");
	}
	
}
