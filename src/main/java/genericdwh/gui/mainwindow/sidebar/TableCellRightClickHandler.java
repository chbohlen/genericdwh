package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.DataObject;
import genericdwh.gui.mainwindow.querypane.DataObjectTableCell;
import javafx.event.EventHandler;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TableCellRightClickHandler implements EventHandler<MouseEvent> {
	
	private TableView<DataObject> tableView;
	
	public TableCellRightClickHandler(TableView<DataObject> tableView) {
		this.tableView = tableView;
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {
			DataObjectTableCell tableCell = (DataObjectTableCell)event.getSource();
			tableView.getSelectionModel().select(tableCell.getDataObj());
		}
	}
}

