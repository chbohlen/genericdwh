package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.DataObject;
import genericdwh.gui.general.sidebar.DataObjectTreeItem;
import lombok.Getter;
import lombok.Setter;

public class LazyLoadDataObjectTreeItem extends DataObjectTreeItem {
	
	@Getter @Setter private boolean loaded;
	
	public LazyLoadDataObjectTreeItem(DataObject obj) {
		super(obj);
		
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
