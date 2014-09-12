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
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
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
            	DataObjectTreeCell treeCell = new DataObjectTreeCell();
            	
            	treeCell.setOnMouseClicked(new SidebarRightClickHandler(dimensionSidebar));
            	treeCell.setContextMenu(createDimensionContextMenu(treeCell));
            	
                return treeCell;
            }
        });
		
		ratioSidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
            	DataObjectTreeCell treeCell = new DataObjectTreeCell();
            	
            	treeCell.setOnMouseClicked(new SidebarRightClickHandler(ratioSidebar));
            	treeCell.setContextMenu(createRatioContextMenu(treeCell));
            	
                return treeCell;
            }
        });
	}

	
	private ContextMenu createDimensionContextMenu(DataObjectTreeCell treeCell) {		
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		
		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem addColDimension = new MenuItem("Add to Column Dimension");
		addColDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addColDimension(treeCell.getDataObj());
            }
        });
		
		MenuItem addRowDimension = new MenuItem("Add to Row Dimension");
		addRowDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addRowDimension(treeCell.itemProperty().get());
            }
        });
		
		MenuItem addFilter = new MenuItem("Add to Filters");
		addFilter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addFilter(treeCell.itemProperty().get());
            }
        });
		
		contextMenu.getItems().addAll(addColDimension, addRowDimension, addFilter);
						
		contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {				
				if (treeCell.getItem() instanceof SidebarHeader || treeCell.getItem() instanceof DimensionCategory) {
					addColDimension.setVisible(false);
					addRowDimension.setVisible(false);
					addFilter.setVisible(false);
				}
			}
		});
		
		return contextMenu;
	}
	
	private ContextMenu createRatioContextMenu(DataObjectTreeCell treeCell) {
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);

		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem addRatio = new MenuItem("Add to Ratios");
		addRatio.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addRatio(treeCell.getItem());
            }
        });	
		
		contextMenu.getItems().addAll(addRatio);
		
		contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {				
				if (treeCell.getItem() instanceof SidebarHeader || treeCell.getItem() instanceof RatioCategory) {
					addRatio.setVisible(false);
				}
			}
		});
		
		return contextMenu;
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
		
		DimensionCategory combinations = new DimensionCategory(-1, "Combinations");
		DataObjectTreeItem tiCombinations = new DataObjectTreeItem(combinations);
		
		for (Dimension currDim : dimensions.values()) {
			DataObjectTreeItem tiNewDim = new DataObjectTreeItem(currDim);
			DataObjectTreeItem tiCat = categoryTreeItemMap.get(currDim.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewDim);
			} else if (currDim.isCombination()) {
				tiCombinations.addChild(tiNewDim);
			} else {
				tiNoCat.addChild(tiNewDim);
			}
			
			if (refObjManager.dimensionHasRecords(currDim)) {
				DataObjectTreeItem tiPlaceholder = new DataObjectTreeItem(currDim);
				tiNewDim.addChild(tiPlaceholder);
			}
		}
		
		if (!tiCombinations.getChildren().isEmpty()) {
			tiRoot.addChild(tiCombinations);
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

	
	public void updateLayout(double width, double height) {
		AnchorPane.setBottomAnchor(dimensionSidebar , (height - 50) * 0.40);
		AnchorPane.setTopAnchor(ratioSidebar , (height - 50) * 0.60);
	}
}
