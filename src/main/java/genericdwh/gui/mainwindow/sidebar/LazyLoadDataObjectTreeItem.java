package genericdwh.gui.mainwindow.sidebar;

import javafx.scene.Node;
import genericdwh.dataobjects.DataObject;
import genericdwh.gui.general.sidebar.DataObjectTreeItem;
import lombok.Getter;
import lombok.Setter;

public class LazyLoadDataObjectTreeItem extends DataObjectTreeItem {
	
	@Getter @Setter private boolean loaded;
	
	public LazyLoadDataObjectTreeItem(DataObject obj) {
		this(obj, null);
	}
	
	public LazyLoadDataObjectTreeItem(DataObject obj, Node icon) {
		super(obj, icon);
		
		this.loaded = false;
		
		expandedProperty().addListener(new LazyLoadOnExpandListener());
	}
	
	public LazyLoadDataObjectTreeItem getFirstChild() {
		if (getChildren().size() > 0) {
			return (LazyLoadDataObjectTreeItem)getChildren().get(0);
		} else {
			return null;
		}
	}
}
