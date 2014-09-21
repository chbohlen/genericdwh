package genericdwh.gui.mainwindow.querypane.resultgrid;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;

public class ResultGridCellContextMenu extends ContextMenu {

	public ResultGridCellContextMenu(ResultGridCell cell) {
		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cell.expand();
			}
		});
		
		MenuItem collapse = new MenuItem("Collapse");
		collapse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				cell.collapse();
			}
		});
		
		getItems().addAll(expand, collapse);
		
		setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (cell.isCollapsed()) {
					expand.setVisible(true);
					collapse.setVisible(false);
				} else {
					expand.setVisible(false);
					collapse.setVisible(true);
				}
			}
		});
	}
}
