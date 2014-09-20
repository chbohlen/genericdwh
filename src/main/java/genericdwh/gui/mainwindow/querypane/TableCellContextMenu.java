package genericdwh.gui.mainwindow.querypane;

import genericdwh.dataobjects.DataObject;
import genericdwh.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableView;

public class TableCellContextMenu extends ContextMenu {

	public TableCellContextMenu(TableView<DataObject> tableView, DataObjectTableCell tableCell) {
		super();
		
		QueryPaneController queryPaneController = Main.getContext().getBean(QueryPaneController.class);
		
		if (tableView != queryPaneController.getTvRatios()) {
			if (tableView != queryPaneController.getTvColDims()) {
				MenuItem changeToColDimension = new MenuItem("Change to Column Dimension");
				changeToColDimension.setOnAction(new EventHandler<ActionEvent>() {
					@Override
		            public void handle(ActionEvent event) {
						DataObject obj = tableCell.getDataObj();
						tableView.getItems().remove(obj);
						queryPaneController.getTvColDims().getItems().add(obj);
		            }
		        });
				
				getItems().add(changeToColDimension);
			}
			
			if (tableView != queryPaneController.getTvRowDims()) {
				MenuItem changeToRowDimension = new MenuItem("Change to Row Dimension");
				changeToRowDimension.setOnAction(new EventHandler<ActionEvent>() {
					@Override
		            public void handle(ActionEvent event) {
						DataObject obj = tableCell.getDataObj();
						tableView.getItems().remove(obj);
						queryPaneController.getTvRowDims().getItems().add(obj);

		            }
		        });
				
				getItems().add(changeToRowDimension);
			}
			
			if (tableView != queryPaneController.getTvFilters()) {
				MenuItem changeToFilter = new MenuItem("Change to Filter");
				changeToFilter.setOnAction(new EventHandler<ActionEvent>() {
					@Override
		            public void handle(ActionEvent event) {
						DataObject obj = tableCell.getDataObj();
						tableView.getItems().remove(obj);
						queryPaneController.getTvFilters().getItems().add(obj);

		            }
		        });
				
				getItems().add(changeToFilter);
			}
			
			getItems().add(new SeparatorMenuItem());
		}
		
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
		
		getItems().addAll(moveUp, moveDown, new SeparatorMenuItem(), remove, removeAll);	
	}
}
