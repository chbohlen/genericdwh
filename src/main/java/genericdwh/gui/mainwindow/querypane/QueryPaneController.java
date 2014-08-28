package genericdwh.gui.mainwindow.querypane;

import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.StatusMessage;
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
		
	@FXML private Pane queryPane;
	
	@FXML private TableView<DataObject> tvRatio;
	@FXML private TableView<DataObject> tvRowDims;
	@FXML private TableView<DataObject> tvColDims;
	
	@FXML private TableColumn<DataObject, DataObject> tcRatio;
	@FXML private TableColumn<DataObject, DataObject> tcRowDims;
	@FXML private TableColumn<DataObject, DataObject> tcColDims;

	@FXML private Button btnExecQuery;
	@FXML private Button btnClear;
	
	private enum QueryType {
		MIXED,
		DIMENSIONS_ONLY,
		REFERENCE_OBJECTS_ONLY;
	}
	
	private ResultGrid resultGrid;
	
	public QueryPaneController(ResultGrid resultGrid) {
		this.resultGrid = resultGrid;
	}
	
	public void initialize(URL location, ResourceBundle resources) {				
		queryPane.getStylesheets().add("/css/TableViewNoHorizontalScrollBar.css");
		
		tvRatio.getStyleClass().add("no-horizontal-scrollbar");
		tvRowDims.getStyleClass().add("no-horizontal-scrollbar");
		tvColDims.getStyleClass().add("no-horizontal-scrollbar");
		
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
		
		queryPane.getChildren().add(resultGrid);
		
		hideQueryPane();
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
			
			if (event.getGestureSource() == event.getSource()) {
				event.consume();
				return;
			}
			
			event.acceptTransferModes(TransferMode.ANY);
		}
		
		event.consume();
	}

	@FXML public void buttonExecQueryOnClickHandler() {
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseController.class).getDbReader();
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		
		List<DataObject> ratios = tvRatio.getItems();
		List<DataObject> rowDims = tvRowDims.getItems();
		List<DataObject> colDims = tvColDims.getItems();
		
		ArrayList<DataObject> combinedDims = new ArrayList<>();
		combinedDims.addAll(rowDims);
		combinedDims.addAll(colDims);
		
		
		
		if (ratios.isEmpty()) {
			// TODO
			return;
		} else if (combinedDims.isEmpty()) {
			// TODO
			return;
		} else {
			resultGrid.create(loadRefObjs(rowDims), loadRefObjs(colDims));
			
			QueryType type = determineQueryType(combinedDims);
			if (type == QueryType.REFERENCE_OBJECTS_ONLY) {
				long refObjCombinationId = dbReader.findRefObjCombinationId(determineRefObjCombinationComponentIds(combinedDims));
				SimpleEntry<Double, Long[]> factForRefObj = dbReader.loadFactForRefObj(((Ratio)ratios.get(0)).getId(), refObjCombinationId);
				
				if (factForRefObj == null) {
					mainWindowController.postStatus(StatusMessage.NO_DATA_FOR_INPUT);
					return;
				}
				
				resultGrid.fillWith(factForRefObj);
			} else if (type == QueryType.DIMENSIONS_ONLY) {
				long dimCombinationId = dbReader.findDimCombinationId(determineDimCombinationComponentIds(combinedDims));
				TreeMap<Long, SimpleEntry<Double, Long[]>> factsForDimensions = dbReader.loadFactsForDim(((Ratio)ratios.get(0)).getId(), dimCombinationId);
				 
				if (factsForDimensions == null) {
					mainWindowController.postStatus(StatusMessage.NO_DATA_FOR_INPUT);
					return;
				}
				
				resultGrid.fillWith(factsForDimensions);
			} else {
				long dimCombinationId = dbReader.findDimCombinationId(determineDimCombinationComponentIds(combinedDims));
			}
		}
	}

	private long[] determineRefObjCombinationComponentIds(ArrayList<DataObject> combinedDims) {
		long[] combination = new long[combinedDims.size()];
		for (int i = 0; i < combination.length; i++) {
			combination[i] = ((ReferenceObject)combinedDims.get(i)).getId();
		}

		return combination;
	}

	private long[] determineDimCombinationComponentIds(ArrayList<DataObject> combinedDims) {
		long[] combination = new long[combinedDims.size()];
		
		for (int i = 0; i < combination.length; i++) {
			DataObject currObj = combinedDims.get(i);
			if (currObj instanceof DimensionHierarchy) {
				combination[i] = ((DimensionHierarchy)currObj).getTopLevel().getId();
			} else if (currObj instanceof ReferenceObject) {
				combination[i] = ((ReferenceObject)currObj).getDimensionId();
			} else {
				combination[i] = ((Dimension)currObj).getId();
			}
		}
		
		return combination;
	}
	
	private ArrayList<TreeMap<Long, ReferenceObject>> loadRefObjs(List<DataObject> dims) {
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseController.class).getDbReader();
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);

		
		ArrayList<TreeMap<Long, ReferenceObject>> result = new ArrayList<TreeMap<Long, ReferenceObject>>();
		for (DataObject dim : dims) {
			if (dim instanceof ReferenceObject) {
				ReferenceObject refObj = refObjManager.getReferenceObject(((ReferenceObject)dim).getId());
				TreeMap<Long, ReferenceObject> refObjInTreeMap = new TreeMap<Long, ReferenceObject>();
				refObjInTreeMap.put(refObj.getId(), refObj);
				result.add(refObjInTreeMap);
			} else {
				if (dim instanceof DimensionHierarchy) {
					dim = ((DimensionHierarchy)dim).getTopLevel();
				}
				result.add(dbReader.loadRefObjsForDim(((Dimension)dim).getId()));
			}
		}
		
		return result;	
	}

	private QueryType determineQueryType(ArrayList<DataObject> dims) {
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
		
		resultGrid.reset();
	}
}
