package genericdwh.gui.mainwindow.sidebar;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
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
            	
            	treeCell.setOnMouseClicked(new TreeCellRightClickHandler(dimensionSidebar));
            	treeCell.contextMenuProperty().bind(Bindings
        				.when(treeCell.title.isEqualTo("Dimensions"))
        				.then((ContextMenu)null)
        				.otherwise(createDimensionContextMenu(treeCell))); 
            	
                return treeCell;
            }
        });
		
		ratioSidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
            	DataObjectTreeCell treeCell = new DataObjectTreeCell();
            	
            	treeCell.setOnMouseClicked(new TreeCellRightClickHandler(ratioSidebar));
            	treeCell.contextMenuProperty().bind(Bindings
        				.when(treeCell.title.isEqualTo("Ratios"))
        				.then((ContextMenu)null)
        				.otherwise(createRatioContextMenu(treeCell))); 
            	
                return treeCell;
            }
        });
	}

	
	private ContextMenu createDimensionContextMenu(DataObjectTreeCell treeCell) {		
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		
		ContextMenu contextMenu = new ContextMenu();
				
		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				treeCell.getTreeItem().setExpanded(true);
            }
        });
		
		MenuItem expandAll = new MenuItem("Expand All");
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
		
		MenuItem collapseAll = new MenuItem("Collapse All");
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
		
		SeparatorMenuItem separator = new SeparatorMenuItem();
		
		contextMenu.getItems().addAll(addColDimension, addRowDimension, addFilter, expand, expandAll, collapse, collapseAll);
		
		contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (treeCell.getItem().getName() != "Dimensions") {
					if (treeCell.getItem() instanceof SidebarHeader || treeCell.getItem() instanceof DimensionCategory) {
						addColDimension.setVisible(false);
						addRowDimension.setVisible(false);
						addFilter.setVisible(false);
						contextMenu.getItems().remove(separator);
					} else {
						if (!contextMenu.getItems().contains(separator)) {
							int index = contextMenu.getItems().indexOf(addFilter);
							if (index != -1) {
								contextMenu.getItems().add(index + 1, separator);
							}
						}
					}
					
					if (treeCell.getTreeItem().getChildren().size() == 0) {
						contextMenu.getItems().remove(separator);
						expand.setVisible(false);
						expandAll.setVisible(false);
						collapse.setVisible(false);
						collapseAll.setVisible(false);
					} else {					
						if (treeCell.getTreeItem().isExpanded()) {
							expand.setVisible(false);
							expandAll.setVisible(false);
							collapse.setVisible(true);
							collapseAll.setVisible(true);
						} else {
							expand.setVisible(true);
							expandAll.setVisible(true);
							collapse.setVisible(false);
							collapseAll.setVisible(false);
						}
					}
				}
			}
		});
		
		return contextMenu;
	}
	
	private ContextMenu createRatioContextMenu(DataObjectTreeCell treeCell) {
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);

		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				treeCell.getTreeItem().setExpanded(true);
            }
        });
		
		MenuItem expandAll = new MenuItem("Expand All");
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
		
		MenuItem collapseAll = new MenuItem("Collapse All");
		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				collapseAll(treeCell.getTreeItem());
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
		
		contextMenu.getItems().addAll(addRatio, expand, expandAll, collapse, collapseAll);
		
		contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (treeCell.getItem() instanceof SidebarHeader || treeCell.getItem() instanceof RatioCategory) {
					addRatio.setVisible(false);
					contextMenu.getItems().remove(separator);
				} else {
					if (!contextMenu.getItems().contains(separator)) {
						int index = contextMenu.getItems().indexOf(addRatio);
						if (index != -1) {
							contextMenu.getItems().add(index + 1, separator);
						}
					}
				}
				
				if (treeCell.getTreeItem().getChildren().size() == 0) {
					contextMenu.getItems().remove(separator);
					expand.setVisible(false);
					expandAll.setVisible(false);
					collapse.setVisible(false);
					collapseAll.setVisible(false);
				} else {					
					if (treeCell.getTreeItem().isExpanded()) {
						expand.setVisible(false);
						expandAll.setVisible(false);
						collapse.setVisible(true);
						collapseAll.setVisible(true);
					} else {
						expand.setVisible(true);
						expandAll.setVisible(true);
						collapse.setVisible(false);
						collapseAll.setVisible(false);
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
