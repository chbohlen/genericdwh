package genericdwh.gui.general.sidebar;

import genericdwh.dataobjects.DataObject;

public class HeaderItem extends DataObjectTreeItem {

	public HeaderItem(String name, long id, boolean defaultExpanded, boolean canCollapse) {
		super(new DataObject(id, name) {@Override public DataObject clone() { return null; }});
		
		setExpanded(defaultExpanded);
		
		if (!canCollapse) {
			expandedProperty().addListener(new HeaderItemOnCollapseListener());
		}
	}
}
