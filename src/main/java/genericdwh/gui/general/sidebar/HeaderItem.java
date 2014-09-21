package genericdwh.gui.general.sidebar;

import javafx.scene.Node;
import genericdwh.dataobjects.DataObject;

public class HeaderItem extends DataObjectTreeItem {

	public HeaderItem(String name, long id, boolean defaultExpanded, boolean canCollapse) {
		this(name, id, defaultExpanded, canCollapse, null);
	}
	
	public HeaderItem(String name, long id, boolean defaultExpanded, boolean canCollapse, Node icon) {
		super(new DataObject(id, name) {@Override public DataObject clone() { return null; }}, icon);
		getValue().initProperties();
		
		setExpanded(defaultExpanded);
		
		if (!canCollapse) {
			expandedProperty().addListener(new HeaderItemOnCollapseListener());
		}
	}
}
