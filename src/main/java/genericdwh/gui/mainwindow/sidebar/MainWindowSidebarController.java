package genericdwh.gui.mainwindow.sidebar;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.gui.general.sidebar.TreeCellRightClickHandler;
import genericdwh.main.Main;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;

public class MainWindowSidebarController implements Initializable {
	
	@FXML private AnchorPane sidebars;
	@FXML private TreeView<DataObject> dimensionSidebar;
	@FXML private TreeView<DataObject> ratioSidebar;
	

	
	public MainWindowSidebarController() {
	}

	public void initialize(URL location, ResourceBundle resources) {		
		hideSidebars();
		
		dimensionSidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
            	DraggableDataObjectTreeCell treeCell = new DraggableDataObjectTreeCell();
            	
            	treeCell.setOnMouseClicked(new TreeCellRightClickHandler(dimensionSidebar));
            	treeCell.contextMenuProperty().bind(Bindings
        				.when(treeCell.getTitle().isEqualTo("Dimensions"))
        				.then((ContextMenu)null)
        				.otherwise(new DataObjectTreeCellContextMenu(treeCell))); 
            	
                return treeCell;
            }
        });
		
		ratioSidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
            	DraggableDataObjectTreeCell treeCell = new DraggableDataObjectTreeCell();
            	
            	treeCell.setOnMouseClicked(new TreeCellRightClickHandler(ratioSidebar));
            	treeCell.contextMenuProperty().bind(Bindings
        				.when(treeCell.getTitle().isEqualTo("Ratios"))
        				.then((ContextMenu)null)
        				.otherwise(new DataObjectTreeCellContextMenu(treeCell))); 
            	
                return treeCell;
            }
        });
	}

	
	public void createSidebars(TreeMap<Long, DimensionCategory> dimensionCategories, List<DimensionHierarchy> hierarchies, TreeMap<Long, Dimension> dimensions, 
			TreeMap<Long, RatioCategory> ratioCategories, TreeMap<Long, Ratio> ratios) {
		
		createDimensionSidebar(dimensionCategories, hierarchies, dimensions);
		createRatioSidebar(ratioCategories, ratios);
	}
	
	private void createDimensionSidebar(TreeMap<Long, DimensionCategory> dimensionCategories, List<DimensionHierarchy> hierarchies, TreeMap<Long, Dimension> dimensions) {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
	    
		HeaderItem tiRoot = new HeaderItem("Dimensions", 0, true, false, new ImageView(Icons.FOLDER));
				
		TreeMap<Long, LazyLoadDataObjectTreeItem> categoryTreeItemMap = new TreeMap<>();
		for (DimensionCategory currCat : dimensionCategories.values()) {
			LazyLoadDataObjectTreeItem tiNewCategory = new LazyLoadDataObjectTreeItem(currCat, new ImageView(Icons.FOLDER));
			tiRoot.addChild(tiNewCategory);
			
			categoryTreeItemMap.put(currCat.getId(), tiNewCategory);
		}
				
		DimensionCategory noCat = new DimensionCategory(-1, "Uncategorized");
		LazyLoadDataObjectTreeItem tiNoCat = new LazyLoadDataObjectTreeItem(noCat, new ImageView(Icons.FOLDER));
		
		for (DimensionHierarchy hierarchy : hierarchies) {
			LazyLoadDataObjectTreeItem tiNewHierarchy = new LazyLoadDataObjectTreeItem(hierarchy, new ImageView(Icons.DIMENSION_HIERARCHY));
			LazyLoadDataObjectTreeItem tiCat = categoryTreeItemMap.get(hierarchy.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewHierarchy);
			} else {
				tiNoCat.addChild(tiNewHierarchy);
			}

			LazyLoadDataObjectTreeItem tiTmp = tiNewHierarchy;
			
			for (Dimension lvl : hierarchy.getLevels()) {
				if (!refObjManager.dimensionHasRecords(lvl)) {
					break;
				}
				
				LazyLoadDataObjectTreeItem tiNewLevel = new LazyLoadDataObjectTreeItem(lvl);
				tiTmp.addChild(tiNewLevel);
				tiTmp = tiNewLevel;
			}
		}
		
		for (Dimension currDim : dimensions.values()) {
			LazyLoadDataObjectTreeItem tiNewDim = new LazyLoadDataObjectTreeItem(currDim, new ImageView(Icons.DIMENSION));
			LazyLoadDataObjectTreeItem tiCat = categoryTreeItemMap.get(currDim.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewDim);
			}  else {
				tiNoCat.addChild(tiNewDim);
			}
			
			if (refObjManager.dimensionHasRecords(currDim)) {
				LazyLoadDataObjectTreeItem tiPlaceholder = new LazyLoadDataObjectTreeItem(currDim);
				tiNewDim.addChild(tiPlaceholder);
			}
		}

		if (tiNoCat.hasChildren()) {
			tiRoot.addChild(tiNoCat);
		}
		
		dimensionSidebar.setRoot(tiRoot);
	}
	
	private void createRatioSidebar(TreeMap<Long, RatioCategory> ratioCategories, TreeMap<Long, Ratio> ratios) {
		HeaderItem tiRoot = new HeaderItem("Ratios", 1, true, false, new ImageView(Icons.FOLDER));
		tiRoot.setExpanded(true);
		
		TreeMap<Long, LazyLoadDataObjectTreeItem> categoryTreeItemMap = new TreeMap<>();
		for (RatioCategory currCat : ratioCategories.values()) {
			LazyLoadDataObjectTreeItem tiNewCategory = new LazyLoadDataObjectTreeItem(currCat, new ImageView(Icons.FOLDER));
			tiRoot.addChild(tiNewCategory);
			
			categoryTreeItemMap.put(currCat.getId(), tiNewCategory);
		}
		
		for (Ratio currRatio : ratios.values()) {
			LazyLoadDataObjectTreeItem tiNewRatio = new LazyLoadDataObjectTreeItem(currRatio, new ImageView(Icons.RATIO));
			LazyLoadDataObjectTreeItem tiCat = categoryTreeItemMap.get(currRatio.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewRatio);
			}
			
			if (currRatio.isRelation()) {
				for (Ratio dependency : currRatio.getDependencies()) {
					LazyLoadDataObjectTreeItem tiNewDependency = new LazyLoadDataObjectTreeItem(dependency, new ImageView(Icons.RATIO));
					tiNewRatio.addChild(tiNewDependency);
				}
			}
		}
		
		ratioSidebar.setRoot(tiRoot);
	}

 	
	public void expandAll(TreeItem<DataObject> root) {
		expandCollapseAll(root, true);
	}
	
	public void collapseAll(TreeItem<DataObject> root) {
		expandCollapseAll(root, false);
	}
	
	private void expandCollapseAll(TreeItem<DataObject> root, boolean expand) {
		LinkedList<TreeItem<DataObject>> queue = new LinkedList<>();
		queue.push(root);
		while (!queue.isEmpty()) {
			TreeItem<DataObject> currNode = queue.pop();
			currNode.setExpanded(expand);
			queue.addAll(currNode.getChildren());
		}
	}


	public void showSidebars() {
		sidebars.setVisible(true);
	}
	
	public void hideSidebars() {
		sidebars.setVisible(false);
	}

	
	public void updateLayout(double width, double height) {
		AnchorPane.setBottomAnchor(dimensionSidebar , (height - 50) * 0.40);
		AnchorPane.setTopAnchor(ratioSidebar , (height - 50) * 0.60);
	}
}
