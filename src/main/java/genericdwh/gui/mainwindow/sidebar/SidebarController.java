package genericdwh.gui.mainwindow.sidebar;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class SidebarController implements Initializable {
	
	@FXML private Pane sidebars;
	@FXML private TreeView<DataObject> dimensionSidebar;
	@FXML private TreeView<DataObject> ratioSidebar;
	
	public SidebarController() {
	}
	
	public void initialize(URL location, ResourceBundle resources) {		
		hideSidebars();
		
		dimensionSidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
                return new DataObjectTreeCell();
            }
        });
		
		ratioSidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
                return new DataObjectTreeCell();
            }
        });
	}
	
	public void createSidebars(TreeMap<Long, DimensionCategory> dimensionCategories, ArrayList<DimensionHierarchy> hierarchies, TreeMap<Long, Dimension> dimensions, 
			TreeMap<Long, RatioCategory> ratioCategories, TreeMap<Long, Ratio> ratios) {
		
		createDimensionSidebar(dimensionCategories, hierarchies, dimensions);
		createRatioSidebar(ratioCategories, ratios);
	}
	
	private void createDimensionSidebar(TreeMap<Long, DimensionCategory> dimensionCategories, ArrayList<DimensionHierarchy> hierarchies, TreeMap<Long, Dimension> dimensions) {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		DataObjectTreeItem tiRoot = new DataObjectTreeItem(new SidebarHeader("Dimensions"));
		tiRoot.setExpanded(true);
				
		TreeMap<Long, DataObjectTreeItem> categoryTreeItemMap = new TreeMap<>();
		for (DimensionCategory currCat : dimensionCategories.values()) {
			DataObjectTreeItem tiNewCategory = new DataObjectTreeItem(currCat);
			tiRoot.addChild(tiNewCategory);
			
			categoryTreeItemMap.put(currCat.getId(), tiNewCategory);
		}
		
		DimensionCategory noCat = new DimensionCategory(-1, "Uncategorized");
		DataObjectTreeItem tiNoCat = new DataObjectTreeItem(noCat);
		
		for (DimensionHierarchy hierarchy : hierarchies) {
			DataObjectTreeItem tiNewHierarchy = new DataObjectTreeItem(hierarchy);
			DataObjectTreeItem tiCat = categoryTreeItemMap.get(hierarchy.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewHierarchy);
			} else {
				tiNoCat.addChild(tiNewHierarchy);
			}

			DataObjectTreeItem tiTmp = tiNewHierarchy;
			
			for (Dimension lvl : hierarchy.getLevels()) {
				if (!refObjManager.dimensionHasRecords(lvl)) {
					break;
				}
				
				DataObjectTreeItem tiNewLevel = new DataObjectTreeItem(lvl);
				tiTmp.addChild(tiNewLevel);
				tiTmp = tiNewLevel;
			}
		}
		
		for (Dimension currDim : dimensions.values()) {
			DataObjectTreeItem tiNewDim = new DataObjectTreeItem(currDim);
			DataObjectTreeItem tiCat = categoryTreeItemMap.get(currDim.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewDim);
			} else {
				tiNoCat.addChild(tiNewDim);
			}
			
			if (refObjManager.dimensionHasRecords(currDim)) {
				DataObjectTreeItem tiPlaceholder = new DataObjectTreeItem(currDim);
				tiNewDim.addChild(tiPlaceholder);
			}
		}
		
		if (!tiNoCat.getChildren().isEmpty()) {
			tiRoot.addChild(tiNoCat);
		}
		
		dimensionSidebar.setRoot(tiRoot);
	}
	
	private void createRatioSidebar(TreeMap<Long, RatioCategory> ratioCategories, TreeMap<Long, Ratio> ratios) {
		DataObjectTreeItem tiRoot = new DataObjectTreeItem(new SidebarHeader("Ratios"));
		tiRoot.setExpanded(true);
		
		TreeMap<Long, DataObjectTreeItem> categoryTreeItemMap = new TreeMap<>();
		for (RatioCategory currCat : ratioCategories.values()) {
			DataObjectTreeItem tiNewCategory = new DataObjectTreeItem(currCat);
			tiRoot.addChild(tiNewCategory);
			
			categoryTreeItemMap.put(currCat.getId(), tiNewCategory);
		}
		
		for (Ratio currRatio : ratios.values()) {
			DataObjectTreeItem tiNewRatio = new DataObjectTreeItem(currRatio);
			DataObjectTreeItem tiCat = categoryTreeItemMap.get(currRatio.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewRatio);
			}
			
			if (currRatio.isRelation()) {
				for (Ratio dependency : currRatio.getDependencies()) {
					DataObjectTreeItem tiNewDependency = new DataObjectTreeItem(dependency);
					tiNewRatio.addChild(tiNewDependency);
				}
			}
		}
		
		ratioSidebar.setRoot(tiRoot);
	}

	public void showSidebars() {
		sidebars.setVisible(true);
	}
	
	public void hideSidebars() {
		sidebars.setVisible(false);
	}
	
	@FXML public void contextMenuNewDimensionOnClickHandler() {
		
	}

	@FXML public void contextMenuNewRatioOnClickHandler() {
		
	}
}
