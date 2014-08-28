package genericdwh.gui.mainwindow.querypane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.jooq.Record;
import org.jooq.Result;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGrid;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class QueryPaneController implements Initializable {
	
	private enum QueryType {
		MIXED,
		DIMENSIONS_ONLY,
		REFERENCE_OBJECTS_ONLY;
	}
		
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
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseController.class).getDbReader();


		List<DataObject> ratios = tvRatio.getItems();
		List<DataObject> rowDims = tvRowDims.getItems();
		List<DataObject> colDims = tvColDims.getItems();
		
		if (ratios.isEmpty()) {
			// TODO
			return;
		} else {
			ArrayList<DataObject> combinedDims = new ArrayList<>();
			combinedDims.addAll(rowDims);
			combinedDims.addAll(colDims);
			
			if (combinedDims.isEmpty()) {
				// TODO
				return;
			}
				
			QueryType type = getQueryTypeForQueryDimensions(combinedDims);
			
			if (type == QueryType.DIMENSIONS_ONLY) {
				long dimCombinationId = dbReader.findDimCombinationId(getDimensionCombination(combinedDims));
				Map<Long, Result<Record>> loadFactsForDimension = dbReader.loadFactsForDim(((Ratio)ratios.get(0)).getId(), dimCombinationId);
				 
//				if (loadFactsForDimension.isEmpty()) {
//					mainWindowController.postStatus("No data for the given input.");
//					return;
//				}
//				
//				System.out.println(loadFactsForDimension);
				
				ResultGrid resultGrid = new ResultGrid(rowDims, colDims);
				queryPane.getChildren().add(resultGrid);
				resultGrid.setLayoutY(130);
				
			} else if (type == QueryType.REFERENCE_OBJECTS_ONLY) {
				long refObjCombinationId = dbReader.findRefObjCombinationId(getReferenceObjectCombination(combinedDims));
				Double loadFactForRefObj = dbReader.loadFactForRefObj(((Ratio)ratios.get(0)).getId(), refObjCombinationId);
				
				if (loadFactForRefObj == -1) {
					mainWindowController.postStatus("No data for the given input.");
					return;
				}
				
			} else {
				long dimCombinationId = dbReader.findDimCombinationId(getDimensionCombination(combinedDims));
				System.out.println(dimCombinationId);
			}
		}
	}

	private long[] getReferenceObjectCombination(ArrayList<DataObject> combinedDims) {
		long[] combination = new long[combinedDims.size()];
		
		for (int i = 0; i < combination.length; i++) {
			combination[i] = ((ReferenceObject)combinedDims.get(i)).getId();
		}
		
		return combination;
	}

	private long[] getDimensionCombination(ArrayList<DataObject> combinedDims) {
		long[] combination = new long[combinedDims.size()];
		
		for (int i = 0; i < combination.length; i++) {
			DataObject currObj = combinedDims.get(i);
			if (currObj instanceof DimensionHierarchy) {
				combination[i] = ((DimensionHierarchy)currObj).getTop().getId();
			} else if (currObj instanceof ReferenceObject) {
				combination[i] = ((ReferenceObject)currObj).getDimensionId()    ;
			} else {
				combination[i] = ((Dimension)currObj).getId();
			}
		}
		
		return combination;
	}

	private QueryType getQueryTypeForQueryDimensions(ArrayList<DataObject> dims) {
		Class<?> type = dims.get(0).getClass();
		
		if (type == DimensionHierarchy.class) {
			type = Dimension.class;
		}
		
		for (DataObject obj : dims) {
			Class<?> currType = obj.getClass();
			if (currType == DimensionHierarchy.class) {
				currType = Dimension.class;
			}
			
			if (type != currType) {
				return QueryType.MIXED;
			}
		}
		
		if (type == Dimension.class) {
			return QueryType.DIMENSIONS_ONLY;
		} else if (type == ReferenceObject.class) {
			return QueryType.REFERENCE_OBJECTS_ONLY;
		}
		
		return null;
	}

	@FXML public void buttonClearOnClickHandler() {
		tvRatio.getItems().clear();
		tvRowDims.getItems().clear();
		tvColDims.getItems().clear();
	}
}
