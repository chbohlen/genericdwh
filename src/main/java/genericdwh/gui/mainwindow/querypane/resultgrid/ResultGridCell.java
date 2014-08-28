package genericdwh.gui.mainwindow.querypane.resultgrid;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class ResultGridCell extends Pane {
	
	public ResultGridCell(String placeholder) {		
		Label label = new Label(placeholder);
		getChildren().add(label);
	}
}
