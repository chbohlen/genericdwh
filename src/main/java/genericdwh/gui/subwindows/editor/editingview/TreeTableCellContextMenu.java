package genericdwh.gui.subwindows.editor.editingview;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectCombination;
import genericdwh.dataobjects.DataObjectHierarchy;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController.EditingViewType;
import genericdwh.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.stage.WindowEvent;

public class TreeTableCellContextMenu extends ContextMenu {

	public TreeTableCellContextMenu(TreeTableRow<DataObject> treeTableRow) {
		super();
		
		EditingViewController editingViewController = Main.getContext().getBean(EditingViewController.class);
		
		TreeTableView<DataObject> editingView = editingViewController.getEditingView();
		
		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingView.getTreeItem(treeTableRow.getIndex()).setExpanded(true);
            }
        });
		
		MenuItem expandAll = new MenuItem("Expand All");
		expandAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.expandAll(editingView.getRoot());
            }
        });
		
		MenuItem collapse = new MenuItem("Collapse");
		collapse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingView.getTreeItem(treeTableRow.getIndex()).setExpanded(false);
            }
        });
		
		MenuItem collapseAll = new MenuItem("Collapse All");
		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.collapseAll(editingView.getRoot());
            }
        });
				
		MenuItem createObject = new MenuItem("Create Object");
		createObject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.createObject();
            }
        });
		
		MenuItem deleteObject = new MenuItem("Delete Object");
		deleteObject.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				Main.getContext().getBean(EditorController.class).confirmDeletion(treeTableRow.getTreeItem());
            }
        });
		
		MenuItem createHierarchy = new MenuItem("Create Hierarchy");
		createHierarchy.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.createObject();
            }
        });
		
		MenuItem deleteHierarchy = new MenuItem("Delete Hierarchy");
		deleteHierarchy.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				Main.getContext().getBean(EditorController.class).confirmDeletion(treeTableRow.getTreeItem());
            }
        });
		
		MenuItem createCombination = new MenuItem("Create Combination");
		createCombination.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.createObject();
            }
        });
		
		MenuItem deleteCombination = new MenuItem("Delete Combination");
		deleteCombination.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				Main.getContext().getBean(EditorController.class).confirmDeletion(treeTableRow.getTreeItem());
            }
        });
		
		MenuItem addChild = new MenuItem("Add Child");
		addChild.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.addChild(editingView.getTreeItem(treeTableRow.getIndex()));
            }
        });
		
		MenuItem removeChild = new MenuItem("Remove Child");
		removeChild.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.removeChild(editingView.getTreeItem(treeTableRow.getIndex()));
            }
        });
		
		MenuItem addComponent = new MenuItem("Add Component");
		addComponent.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.addComponent(editingView.getTreeItem(treeTableRow.getIndex()));
            }
        });
		
		MenuItem removeComponent = new MenuItem("Remove Component");
		removeComponent.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingViewController.removeComponent(editingView.getTreeItem(treeTableRow.getIndex()));
            }
        });
		
		SeparatorMenuItem separator1 = new SeparatorMenuItem();
		SeparatorMenuItem separator2 = new SeparatorMenuItem();
		
		getItems().addAll(createObject, deleteObject,
									createHierarchy, deleteHierarchy, addChild, removeChild, 
									createCombination, deleteCombination, addComponent, removeComponent, 
									expand, collapse, expandAll, collapseAll);
		
		setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				EditingViewType currEditingViewType = editingViewController.getCurrEditingViewType();
				
				createObject.setVisible(true);
				deleteObject.setVisible(false);
				createHierarchy.setVisible(false);
				deleteHierarchy.setVisible(false);
				createCombination.setVisible(false);
				deleteCombination.setVisible(false);
				getItems().remove(separator2);
				addChild.setVisible(false);
				removeChild.setVisible(false);
				addComponent.setVisible(false);
				removeComponent.setVisible(false);
				getItems().remove(separator1);
				expand.setVisible(false);
				collapse.setVisible(false);
				expandAll.setVisible(false);
				collapseAll.setVisible(false);
				
				if (!treeTableRow.isEmpty() && !(editingView.getTreeItem(treeTableRow.getIndex()) instanceof HeaderItem)) {
					deleteObject.setVisible(true);
				}
				
				if (currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES
						|| currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES_BY_CATEGORY
						|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES
						|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES_BY_CATEGORY) {
					
					createObject.setVisible(false);
					deleteObject.setVisible(false);
					createHierarchy.setVisible(true);
					deleteHierarchy.setVisible(true);
					if (!treeTableRow.isEmpty()) {
						getItems().add(getItems().indexOf(deleteHierarchy) + 1, separator2);
						addChild.setVisible(true);
						if (!(treeTableRow.getTreeItem().getValue() instanceof DataObjectHierarchy)) {
							removeChild.setVisible(true);
						}
					}
				} else if (currEditingViewType == EditingViewType.DIMENSION_COMBINATIONS
							|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS
							|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS_BY_DIMENSION) {
					
					createObject.setVisible(false);
					deleteObject.setVisible(false);
					createCombination.setVisible(true);
					deleteCombination.setVisible(true);
					if (!treeTableRow.isEmpty()) {
						getItems().add(getItems().indexOf(deleteCombination) + 1, separator2);
						addComponent.setVisible(true);
						if (!(treeTableRow.getTreeItem().getValue() instanceof DataObjectCombination)) {
							removeComponent.setVisible(true);
						}
					}
				}
				
				if (editingView.getTreeItem(treeTableRow.getIndex()) instanceof HeaderItem
						|| (!treeTableRow.isEmpty() && (editingView.getTreeItem(treeTableRow.getIndex()).getValue() instanceof DataObjectHierarchy
														|| editingView.getTreeItem(treeTableRow.getIndex()).getValue() instanceof DataObjectCombination))) {
					
					if (editingView.getTreeItem(treeTableRow.getIndex()).isExpanded()) {
						collapse.setVisible(true);
					} else {
						expand.setVisible(true);
					}
				}
				
				if (currEditingViewType == EditingViewType.DIMENSIONS_BY_CATEGORY
						|| currEditingViewType == EditingViewType.REFERENCE_OBJECTS_BY_DIMENSION
						|| currEditingViewType == EditingViewType.RATIOS_BY_CATEGORY
						|| currEditingViewType == EditingViewType.FACTS_BY_RATIO
						|| currEditingViewType == EditingViewType.FACTS_BY_REFERENCE_OBJECT
						|| currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES
						|| currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES_BY_CATEGORY
						|| currEditingViewType == EditingViewType.DIMENSION_COMBINATIONS
						|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES
						|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES_BY_CATEGORY
						|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS
						|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS_BY_DIMENSION) {
					
					if (!getItems().contains(separator1)) {
						int index;
						if (currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES
								|| currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES_BY_CATEGORY
								|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES
								|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES_BY_CATEGORY) {
							
							index = getItems().indexOf(removeChild);
						} else if (currEditingViewType == EditingViewType.DIMENSION_COMBINATIONS
								|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS
								|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS_BY_DIMENSION) {
							
							index = getItems().indexOf(removeComponent);
						} else {
							index = getItems().indexOf(deleteObject);
						}
						if (index != -1) {
							getItems().add(index + 1, separator1);
						}
					}
					expandAll.setVisible(true);
					collapseAll.setVisible(true);
				}
			}
		});
	}
}
