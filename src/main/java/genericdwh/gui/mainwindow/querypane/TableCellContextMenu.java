package genericdwh.gui.mainwindow.querypane;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;
import javafx.stage.WindowEvent;

public class TableCellContextMenu extends ContextMenu {

	public TableCellContextMenu(TableView<DataObject> tableView, DataObjectTableCell tableCell) {
		super();
		
		QueryPaneController queryPaneController = Main.getContext().getBean(QueryPaneController.class);
		
	
		MenuItem changeToColDimension = new MenuItem("Change to Column Dimension");
		changeToColDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				DataObject obj = tableCell.getDataObj();
				tableView.getItems().remove(obj);
				queryPaneController.getTvColDims().getItems().add(obj);
            }
        });
		
		MenuItem changeToRowDimension = new MenuItem("Change to Row Dimension");
		changeToRowDimension.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				DataObject obj = tableCell.getDataObj();
				tableView.getItems().remove(obj);
				queryPaneController.getTvRowDims().getItems().add(obj);

            }
        });
		
		MenuItem changeToFilter = new MenuItem("Change to Filter");
		changeToFilter.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				DataObject obj = tableCell.getDataObj();
				tableView.getItems().remove(obj);
				queryPaneController.getTvFilters().getItems().add(obj);

            }
        });
		
		SeparatorMenuItem separator1 = new SeparatorMenuItem();
				
		MenuItem moveUp = new MenuItem("Move up");
		moveUp.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				if (tableView.getItems().size() == 1) {
					return;
				}
				
				DataObject obj = tableCell.getDataObj();
				int index = tableView.getItems().indexOf(obj);
				tableView.getItems().remove(obj);
				tableView.getItems().add(index - 1, obj);
				
				tableView.getSelectionModel().select(obj);
            }
        });

		MenuItem moveDown = new MenuItem("Move down");
		moveDown.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				if (tableView.getItems().size() == 1) {
					return;
				}
				
				DataObject obj = tableCell.getDataObj();
				int index = tableView.getItems().indexOf(obj);
				tableView.getItems().remove(obj);
				tableView.getItems().add(index + 1, obj);
				
				tableView.getSelectionModel().select(obj);
            }
        });
		
		SeparatorMenuItem separator2 = new SeparatorMenuItem();
		
		MenuItem remove = new MenuItem("Remove");
		remove.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				tableView.getItems().remove(tableCell.getDataObj());
            }
        });
		
		MenuItem removeAll = new MenuItem("Remove all");
		removeAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				tableView.getItems().clear();
            }
        });
		
		getItems().addAll(changeToColDimension, changeToRowDimension, changeToFilter, separator1, 
				moveUp, moveDown, separator2,
				remove, removeAll);
		
		setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				changeToColDimension.setVisible(false);
				changeToRowDimension.setVisible(false);
				changeToFilter.setVisible(false);
				getItems().remove(separator1);
				moveUp.setVisible(false);
				moveDown.setVisible(false);
				getItems().remove(separator2);
				remove.setVisible(false);
				removeAll.setVisible(true);
				
				if (!tableView.getSelectionModel().isEmpty()) {
					if (tableView != queryPaneController.getTvRatios()) {
						if (tableView != queryPaneController.getTvColDims()) {
							changeToColDimension.setVisible(true);
						}
						if (tableView != queryPaneController.getTvRowDims()) {
							changeToRowDimension.setVisible(true);
						}
						if (tableView != queryPaneController.getTvFilters() 
								&& tableView.getSelectionModel().getSelectedItem() instanceof ReferenceObject) {			
							changeToFilter.setVisible(true);
						}
						getItems().add(getItems().indexOf(changeToFilter) + 1, separator1);
					}
					
					if (tableView != queryPaneController.getTvFilters() && tableView.getItems().size() > 1) {
						moveUp.setVisible(true);
						moveDown.setVisible(true);
						getItems().add(getItems().indexOf(moveDown) + 1, separator2);
					}
					
					remove.setVisible(true);
				}
			};
		});
	}
}
