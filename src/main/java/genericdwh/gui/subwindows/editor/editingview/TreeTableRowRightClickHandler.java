package genericdwh.gui.subwindows.editor.editingview;

import genericdwh.dataobjects.DataObject;
import javafx.event.EventHandler;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class TreeTableRowRightClickHandler implements EventHandler<MouseEvent> {
	
	private TreeTableView<DataObject> treeTableView;
	
	public TreeTableRowRightClickHandler(TreeTableView<DataObject> treeTableView) {
		this.treeTableView = treeTableView;
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY) {
			@SuppressWarnings("unchecked")
			TreeTableRow<DataObject> treeTableRow = (TreeTableRow<DataObject>)event.getSource();
			treeTableView.getSelectionModel().select(treeTableRow.getTreeItem());
		}
	}
}

