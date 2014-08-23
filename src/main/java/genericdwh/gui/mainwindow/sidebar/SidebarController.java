package genericdwh.gui.mainwindow.sidebar;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.Dimension;
import genericdwh.dataobjects.DimensionHierarchy;
import genericdwh.dataobjects.ReferenceObjectManager;
import genericdwh.main.Main;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

public class SidebarController implements Initializable {
	
	@FXML private TreeView<DataObject> sidebar;
	
	public SidebarController() {
	}
	
	public void initialize(URL location, ResourceBundle resources) {		
		hideSidebar();
		
		sidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
                return new DataObjectTreeCell();
            }
        });
	}
	
	
	public void buildSidebar(ArrayList<DimensionHierarchy> hierarchies, TreeMap<Long, Dimension> dimensions) {
		DataObjectTreeItem tiRoot = new DataObjectTreeItem(new SidebarHeader("Dimensions"));
		tiRoot.setExpanded(true);
		
		for (DimensionHierarchy hierarchy : hierarchies) {
			DataObjectTreeItem tiNewHierarchy = new DataObjectTreeItem(hierarchy);
			tiRoot.addChild(tiNewHierarchy);
			DataObjectTreeItem tiTmp = tiNewHierarchy;
			
			for (Dimension lvl : hierarchy.getLevels()) {
				if (!Main.getContext().getBean(ReferenceObjectManager.class).dimensionHasRecords(lvl)) {
					break;
				}
				
				DataObjectTreeItem tiNewLevel = new DataObjectTreeItem(lvl);
				tiTmp.addChild(tiNewLevel);
				tiTmp = tiNewLevel;
			}
		}
		
		sidebar.setRoot(tiRoot);
	}

	public void showSidebar() {
		sidebar.setVisible(true);
	}
	
	public void hideSidebar() {
		sidebar.setVisible(false);
	}
	
	@FXML public void contextMenuNewDimensionOnClickHandler() {
		
	}
}
