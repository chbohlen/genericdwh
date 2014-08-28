package genericdwh.gui.mainwindow.querypane.resultgrid;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

public class ResultGridCell extends BorderPane {
	
	private Label label;
	
	public ResultGridCell(String val) {
		getStylesheets().add("/css/ResultGridVisibleBorders.css");
		getStyleClass().add("border");
		
		setPadding(new Insets(1));
		
		label = new Label(val);
		setCenter(label);
	}
	
	public ResultGridCell() {		
		this("");
	}
	
	public void setValue(String val) {
		label.setText(val);
	}
}
