package genericdwh.gui.general.sidebar;

import genericdwh.dataobjects.DataObject;
import genericdwh.gui.mainwindow.sidebar.DataObjectTreeItem;

public class SidebarHeader extends DataObjectTreeItem {
	
	public SidebarHeader(String name, boolean canCollapse) {
		super(new DataObject(name) {});
		
		if (!canCollapse) {
			expandedProperty().addListener(new SidebarHeaderOnCollapseListener());
		}
	}
}
