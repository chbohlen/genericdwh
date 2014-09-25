package genericdwh.gui.general.statusbar;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class StatusBarController implements Initializable {
	
	@FXML private Label lStatus;
	@FXML private ImageView ivIcon;

	public StatusBarController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		lStatus.getStyleClass().add("statusbar");
	}
	
	public void postStatus(String status, Image icon) {
		lStatus.setText(status);
		ivIcon.setImage(icon);
	}
	
	public void clearStatus() {
		lStatus.setText("");
		ivIcon.setImage(null);
	}
	
}
