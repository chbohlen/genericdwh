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
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.gui.general.sidebar.TreeCellRightClickHandler;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.WindowEvent;
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
        				.otherwise(createContextMenu(treeCell))); 
            	
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
        				.otherwise(createContextMenu(treeCell))); 
            	
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
		
		HeaderItem tiRoot = new HeaderItem("Dimensions", 0, true, false);
				
		TreeMap<Long, LazyLoadDataObjectTreeItem> categoryTreeItemMap = new TreeMap<>();
		for (DimensionCategory currCat : dimensionCategories.values()) {
			LazyLoadDataObjectTreeItem tiNewCategory = new LazyLoadDataObjectTreeItem(currCat);
			tiRoot.addChild(tiNewCategory);
			
			categoryTreeItemMap.put(currCat.getId(), tiNewCategory);
		}
				
		DimensionCategory noCat = new DimensionCategory(-1, "Uncategorized");
		LazyLoadDataObjectTreeItem tiNoCat = new LazyLoadDataObjectTreeItem(noCat);
		
		for (DimensionHierarchy hierarchy : hierarchies) {
			LazyLoadDataObjectTreeItem tiNewHierarchy = new LazyLoadDataObjectTreeItem(hierarchy);
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
		
		DimensionCategory combinations = new DimensionCategory(-1, "Combinations");
		LazyLoadDataObjectTreeItem tiCombinations = new LazyLoadDataObjectTreeItem(combinations);
		
		for (Dimension currDim : dimensions.values()) {
			LazyLoadDataObjectTreeItem tiNewDim = new LazyLoadDataObjectTreeItem(currDim);
			LazyLoadDataObjectTreeItem tiCat = categoryTreeItemMap.get(currDim.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewDim);
			} else if (currDim.isCombination()) {
				tiCombinations.addChild(tiNewDim);
			} else {
				tiNoCat.addChild(tiNewDim);
			}
			
			if (refObjManager.dimensionHasRecords(currDim)) {
				LazyLoadDataObjectTreeItem tiPlaceholder = new LazyLoadDataObjectTreeItem(currDim);
				tiNewDim.addChild(tiPlaceholder);
			}
		}
		
		if (tiCombinations.hasChildren()) {
			tiRoot.addChild(tiCombinations);
		}
		if (tiNoCat.hasChildren()) {
			tiRoot.addChild(tiNoCat);
		}
		
		dimensionSidebar.setRoot(tiRoot);
	}
	
	private void createRatioSidebar(TreeMap<Long, RatioCategory> ratioCategories, TreeMap<Long, Ratio> ratios) {
		HeaderItem tiRoot = new HeaderItem("Ratios", 1, true, false);
		tiRoot.setExpanded(true);
		
		TreeMap<Long, LazyLoadDataObjectTreeItem> categoryTreeItemMap = new TreeMap<>();
		for (RatioCategory currCat : ratioCategories.values()) {
			LazyLoadDataObjectTreeItem tiNewCategory = new LazyLoadDataObjectTreeItem(currCat);
			tiRoot.addChild(tiNewCategory);
			
			categoryTreeItemMap.put(currCat.getId(), tiNewCategory);
		}
		
		for (Ratio currRatio : ratios.values()) {
			LazyLoadDataObjectTreeItem tiNewRatio = new LazyLoadDataObjectTreeItem(currRatio);
			LazyLoadDataObjectTreeItem tiCat = categoryTreeItemMap.get(currRatio.getCategoryId());
			if (tiCat != null) {
				tiCat.addChild(tiNewRatio);
			}
			
			if (currRatio.isRelation()) {
				for (Ratio dependency : currRatio.getDependencies()) {
					LazyLoadDataObjectTreeItem tiNewDependency = new LazyLoadDataObjectTreeItem(dependency);
					tiNewRatio.addChild(tiNewDependency);
				}
			}
		}
		
		ratioSidebar.setRoot(tiRoot);
	}

	
	private ContextMenu createContextMenu(DraggableDataObjectTreeCell treeCell) {		
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		
		ContextMenu contextMenu = new ContextMenu();
				
		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				treeCell.getTreeItem().setExpanded(true);
            }
        });
		
		MenuItem expandAll = new MenuItem("Expand all");
		expandAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				expandAll(treeCell.getTreeItem());
            }
        });
		
		MenuItem collapse = new MenuItem("Collapse");
		collapse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				treeCell.getTreeItem().setExpanded(false);
            }
        });
		
		MenuItem collapseAll = new MenuItem("Collapse all");
		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				collapseAll(treeCell.getTreeItem());
            }
        });
		
		MenuItem addColDimension = new MenuItem("Add to Column Dimensions");
		addColDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addColDimension(treeCell.getItem());
            }
        });
		
		MenuItem addRowDimension = new MenuItem("Add to Row Dimensions");
		addRowDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addRowDimension(treeCell.getItem());
            }
        });
		
		MenuItem addFilter = new MenuItem("Add to Filters");
		addFilter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addFilter(treeCell.getItem());
            }
        });
		
		MenuItem addRatio = new MenuItem("Add to Ratios");
		addRatio.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addRatio(treeCell.getItem());
            }
        });
		
		SeparatorMenuItem separator = new SeparatorMenuItem();
		
		contextMenu.getItems().addAll(addColDimension, addRowDimension, addFilter, addRatio, expand, collapse, expandAll, collapseAll);
		
		contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (treeCell.getItem().getName() != "Dimensions" && treeCell.getItem().getName() != "Ratios") {
					addColDimension.setVisible(false);
					addRowDimension.setVisible(false);
					addFilter.setVisible(false);
					addRatio.setVisible(false);
					contextMenu.getItems().remove(separator);
					expand.setVisible(false);
					collapse.setVisible(false);
					expandAll.setVisible(true);
					collapseAll.setVisible(true);
					
					if (treeCell.getItem() instanceof Dimension
							|| treeCell.getItem() instanceof DimensionHierarchy
							|| treeCell.getItem() instanceof ReferenceObject) {
						
						addColDimension.setVisible(true);
						addRowDimension.setVisible(true);
						addFilter.setVisible(true);
						if (!contextMenu.getItems().contains(separator)) {
							int index = contextMenu.getItems().indexOf(addFilter);
							if (index != -1) {
								contextMenu.getItems().add(index + 1, separator);
							}
						}
					} else if (treeCell.getItem() instanceof Ratio) {
						addRatio.setVisible(true);
						if (!contextMenu.getItems().contains(separator)) {
							int index = contextMenu.getItems().indexOf(addRatio);
							if (index != -1) {
								contextMenu.getItems().add(index + 1, separator);
							}
						}
					}

					if (treeCell.getTreeItem().getChildren().size() == 0) {
						if (treeCell.getTreeItem().isExpanded()) {
							collapse.setVisible(true);
						} else {
							expand.setVisible(true);
						}
					}
				}
			}
		});
		
		return contextMenu;
	}
 	
	private void expandAll(TreeItem<DataObject> root) {
		expandCollapseAll(root, true);
	}
	
	private void collapseAll(TreeItem<DataObject> root) {
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
