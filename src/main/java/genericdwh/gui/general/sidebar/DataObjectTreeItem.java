package genericdwh.gui.general.sidebar;

import genericdwh.dataobjects.DataObject;
import javafx.scene.control.TreeItem;

public class DataObjectTreeItem extends TreeItem<DataObject> {
	
	public DataObjectTreeItem(DataObject obj) {
		super(obj);
	}
	
	public void addChild(DataObjectTreeItem newChild) {
		getChildren().add(newChild);
	}
	
	public boolean hasChildren() {
		return !getChildren().isEmpty();
	}
}
