package genericdwh.gui.mainwindow.querypane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.db.DatabaseController;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class QueryPaneController implements Initializable {
	
	@FXML private Pane queryPane;
	
	@FXML private TableView<DataObject> tvRatio;
	@FXML private TableView<DataObject> tvRowDims;
	@FXML private TableView<DataObject> tvColDims;
	
	@FXML TableColumn<DataObject, DataObject> tcRatio;
	@FXML TableColumn<DataObject, DataObject> tcRowDims;
	@FXML TableColumn<DataObject, DataObject> tcColDims;

	@FXML Button btnExecQuery;
	@FXML Button btnClear;
	
	public QueryPaneController() {
	}
	
	public void initialize(URL location, ResourceBundle resources) {		
		hideQueryPane();
		
		tcRatio.setCellFactory(new Callback<TableColumn<DataObject, DataObject>, TableCell<DataObject, DataObject>>() {
            public TableCell<DataObject, DataObject> call(TableColumn<DataObject, DataObject> param) {
                return new DataObjectTableCell();
            }
        });
		tcRowDims.setCellFactory(new Callback<TableColumn<DataObject, DataObject>, TableCell<DataObject, DataObject>>() {
            public TableCell<DataObject, DataObject> call(TableColumn<DataObject, DataObject> param) {
                return new DataObjectTableCell();
            }
        });
		tcColDims.setCellFactory(new Callback<TableColumn<DataObject, DataObject>, TableCell<DataObject, DataObject>>() {
            public TableCell<DataObject, DataObject> call(TableColumn<DataObject, DataObject> param) {
                return new DataObjectTableCell();
            }
        });
	}
	

	public void showQueryPane() {
		queryPane.setVisible(true);
	}
	
	public void hideQueryPane() {
		queryPane.setVisible(false);
	}
	
	@FXML public void onDragDroppedHandler(DragEvent event) {
		boolean completed = false;
		
		DataObject draggedDataObject = Main.getContext().getBean(MainWindowController.class).getDraggedDataObject();
		if (draggedDataObject != null) {
			Object source = event.getGestureSource();			
			if (source instanceof TableView) {
				@SuppressWarnings("unchecked")
				TableView<DataObject> tvSource = (TableView<DataObject>)source;
				tvSource.getItems().remove(draggedDataObject);
			}
			
			@SuppressWarnings("unchecked")
			TableView<DataObject> tvTarget= (TableView<DataObject>)event.getGestureTarget();
			tvTarget.getItems().add(draggedDataObject);

			completed = true;
		}
		
		Main.getContext().getBean(MainWindowController.class).setDraggedDataObject(null);
		
		event.setDropCompleted(completed);
		event.consume();
	}
	
	@FXML public void onDragOverHandler(DragEvent event) {
		DataObject draggedDataObject = Main.getContext().getBean(MainWindowController.class).getDraggedDataObject();
		if (draggedDataObject != null) {
			@SuppressWarnings("unchecked")
			TableView<DataObject> tvSource = (TableView<DataObject>)event.getSource();

			if ((draggedDataObject instanceof Ratio && tvSource != tvRatio) || (!(draggedDataObject instanceof Ratio) && tvSource == tvRatio)) {
				event.consume();
				return;
			}
			
			if (!(event.getGestureSource() instanceof TableView)
					&& (tvRatio.getItems().contains(draggedDataObject)
						|| tvRowDims.getItems().contains(draggedDataObject)
						|| tvColDims.getItems().contains(draggedDataObject))) {
				event.consume();
				return;
			}
			
			
			event.acceptTransferModes(TransferMode.ANY);
		}
		
		event.consume();
	}

	@FXML public void buttonExecQueryOnClickHandler() {
		List<DataObject> ratios = tvRatio.getItems();
		List<DataObject> rowDims = tvRowDims.getItems();
		List<DataObject> colDims = tvColDims.getItems();
		
		ArrayList<Dimension> combination = new ArrayList<Dimension>();
		
		for (DataObject obj : rowDims) {
			combination.add((Dimension)obj);
		}
		
		for (DataObject obj : colDims) {
			combination.add((Dimension)obj);
		}
		
		
		System.out.println(Main.getContext().getBean(DatabaseController.class).getDbReader().findDimensionCombination(combination));
	}

	@FXML public void buttonClearOnClickHandler() {
		tvRatio.getItems().clear();
		tvRowDims.getItems().clear();
		tvColDims.getItems().clear();
	}
}
