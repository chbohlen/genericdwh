package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.DataObject;
import javafx.scene.control.TreeItem;
import lombok.Getter;
import lombok.Setter;

public class DataObjectTreeItem extends TreeItem<DataObject> {
	
	private DataObject obj;
	
	@Getter @Setter private boolean loaded;
	
	public DataObjectTreeItem(DataObject obj) {
		super(obj);
		
		this.obj = obj;
		this.loaded = false;
		
		if (obj instanceof SidebarHeader) {
			expandedProperty().addListener(new DontCollapseOnCollapseListener());
		} else {
			expandedProperty().addListener(new LazyLoadOnExpandListener());
		}
	}
	
	public void addChild(DataObjectTreeItem newChild) {
		getChildren().add(newChild);
	}
	
	public DataObjectTreeItem getFirstChild() {
		if (getChildren().size() > 0) {
			return (DataObjectTreeItem)getChildren().get(0);
		} else {
			return null;
		}
	}
	
	public Class<?> getDataObjectType() {
		return obj.getClass();
	}
}
