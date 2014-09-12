package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.DataObject;
import javafx.event.EventHandler;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class SidebarRightClickHandler implements EventHandler<MouseEvent> {
	
	private TreeView<DataObject> sidebar;
	
	public SidebarRightClickHandler(TreeView<DataObject> sidebar) {
		this.sidebar = sidebar;
	}
	
	@Override
	public void handle(MouseEvent event) {
		if (event.getButton() == MouseButton.SECONDARY && event.getSource() instanceof DataObjectTreeCell) {
			DataObjectTreeCell treeCell = (DataObjectTreeCell)event.getSource();
			boolean wasExpanded = treeCell.getTreeItem().isExpanded();
			sidebar.getSelectionModel().select(treeCell.getTreeItem());
			treeCell.getTreeItem().setExpanded(wasExpanded);
		}
	}
}
