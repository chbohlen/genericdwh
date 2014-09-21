package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.WindowEvent;

public class DataObjectTreeCellContextMenu extends ContextMenu {

	public DataObjectTreeCellContextMenu(DraggableDataObjectTreeCell treeCell) {
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		MainWindowSidebarController mainWindowSidebarController = Main.getContext().getBean(MainWindowSidebarController.class);
		
		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				treeCell.getTreeItem().setExpanded(true);
            }
        });
		
		MenuItem expandAll = new MenuItem("Expand all underlying");
		expandAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowSidebarController.expandAll(treeCell.getTreeView().getSelectionModel().getSelectedItem());
            }
        });
		
		MenuItem collapse = new MenuItem("Collapse");
		collapse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				treeCell.getTreeItem().setExpanded(false);
            }
        });
		
		MenuItem collapseAll = new MenuItem("Collapse all underlying");
		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowSidebarController.collapseAll(treeCell.getTreeView().getSelectionModel().getSelectedItem());
            }
        });
		
		MenuItem addColDimension = new MenuItem("Add to Column Dimensions");
		addColDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addColDimension(treeCell.getTreeView().getSelectionModel().getSelectedItem().getValue());
            }
        });
		
		MenuItem addRowDimension = new MenuItem("Add to Row Dimensions");
		addRowDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addRowDimension(treeCell.getTreeView().getSelectionModel().getSelectedItem().getValue());
            }
        });
		
		MenuItem addFilter = new MenuItem("Add to Filters");
		addFilter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addFilter(treeCell.getTreeView().getSelectionModel().getSelectedItem().getValue());
            }
        });
		
		MenuItem addRatio = new MenuItem("Add to Ratios");
		addRatio.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				mainWindowController.addRatio(treeCell.getTreeView().getSelectionModel().getSelectedItem().getValue());
            }
        });
		
		SeparatorMenuItem separator = new SeparatorMenuItem();
		
		getItems().addAll(addColDimension, addRowDimension, addFilter, addRatio, expand, collapse, expandAll, collapseAll);
		
		setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (treeCell.getItem().getName() != "Dimensions" && treeCell.getItem().getName() != "Ratios") {
					addColDimension.setVisible(false);
					addRowDimension.setVisible(false);
					addFilter.setVisible(false);
					addRatio.setVisible(false);
					getItems().remove(separator);
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
						if (!getItems().contains(separator)) {
							int index = getItems().indexOf(addFilter);
							if (index != -1) {
								getItems().add(index + 1, separator);
							}
						}
					} else if (treeCell.getItem() instanceof Ratio) {
						addRatio.setVisible(true);
						if (!getItems().contains(separator)) {
							int index = getItems().indexOf(addRatio);
							if (index != -1) {
								getItems().add(index + 1, separator);
							}
						}
					}

					if (!treeCell.getTreeItem().getChildren().isEmpty()) {
						if (treeCell.getTreeItem().isExpanded()) {
							collapse.setVisible(true);
						} else {
							expand.setVisible(true);
						}
					}
				}
			}
		});
	}
}
