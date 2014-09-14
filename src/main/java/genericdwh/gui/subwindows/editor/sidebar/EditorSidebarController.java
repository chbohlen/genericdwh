package genericdwh.gui.subwindows.editor.sidebar;

import javafx.fxml.FXML;
import javafx.scene.control.TreeView;
import genericdwh.dataobjects.DataObject;
import genericdwh.gui.general.sidebar.SidebarHeader;

public class EditorSidebarController {
	
	@FXML private TreeView<DataObject> sidebar;
	
	public void createSidebar() {
		SidebarHeader tiRoot = new SidebarHeader("", false);
		sidebar.setRoot(tiRoot);
		sidebar.setShowRoot(false);
		
		
		SidebarHeader tiDims = new SidebarHeader("Dimensions", true);
		tiRoot.addChild(tiDims);
		
		SidebarHeader tiDimsByCat = new SidebarHeader("By Category", true);
		tiDims.addChild(tiDimsByCat);
		
		
		SidebarHeader tiRefObjs = new SidebarHeader("Reference Objects", true);
		tiRoot.addChild(tiRefObjs);
		
		SidebarHeader tiRefObjsByDim = new SidebarHeader("By Dimension", true);
		tiRefObjs.addChild(tiRefObjsByDim);
		
		SidebarHeader tiRefObjsByCat = new SidebarHeader("By Category", true);
		tiRefObjs.addChild(tiRefObjsByCat);
		
		
		SidebarHeader tiRatios = new SidebarHeader("Ratios", true);
		tiRoot.addChild(tiRatios);
		
		SidebarHeader tiRatiosByCat = new SidebarHeader("By Category", true);
		tiRatios.addChild(tiRatiosByCat);
		
		
		SidebarHeader tiDimCats = new SidebarHeader("Dimension Categories", true);
		tiRoot.addChild(tiDimCats);
		
		SidebarHeader tiRefObjCats = new SidebarHeader("Reference Object Categories", true);
		tiRoot.addChild(tiRefObjCats);
		
		SidebarHeader tiUnits = new SidebarHeader ("Units", true);
		tiRoot.addChild(tiUnits);
	}	
}
