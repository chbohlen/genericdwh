package genericdwh.gui.mainwindow.querypane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
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
		SINGLE_DIMENSION,
		DIMENSION_COMBINATION,
		SINGLE_REFERENCE_OBJECT,
		REFERENCE_OBJECT_COMBINATION;
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
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		
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
			resultGrid.create(refObjManager.loadRefObjs(rowDims), refObjManager.loadRefObjs(colDims));
			
			switch (determineQueryType(combinedDims)) {
				case SINGLE_REFERENCE_OBJECT: {
					long refObjId = ((ReferenceObject)combinedDims.get(0)).getId();
					Entry<Long, Double> factForRefObj = dbReader.loadFactForSingleRefObj(((Ratio)ratios.get(0)).getId(), refObjId);
					
					if (factForRefObj == null) {
						mainWindowController.postStatus(StatusMessage.NO_DATA_FOR_INPUT);
						return;
					}
					
					resultGrid.fillSingleRefObj(factForRefObj);
					break;
				}
				case REFERENCE_OBJECT_COMBINATION: {
					long refObjId = refObjManager.findRefObjAggregateId(combinedDims);					
					Entry<Long, Entry<Long[], Double>> factForRefObj = dbReader.loadFactForRefObjCombination(((Ratio)ratios.get(0)).getId(), refObjId);
					
					if (factForRefObj == null) {
						mainWindowController.postStatus(StatusMessage.NO_DATA_FOR_INPUT);
						return;
					}
					
					resultGrid.fillRefObjCombination(factForRefObj);
					break;
				}
				case SINGLE_DIMENSION: {
					long dimId = ((Dimension)combinedDims.get(0)).getId();
					TreeMap<Long, Double> factsForDimension = dbReader.loadFactsForSingleDim(((Ratio)ratios.get(0)).getId(), dimId);
					 
					if (factsForDimension == null) {
						mainWindowController.postStatus(StatusMessage.NO_DATA_FOR_INPUT);
						return;
					}
					
					resultGrid.fillSingleDim(factsForDimension);
					break;
				}
				case DIMENSION_COMBINATION: {
					long dimId = dimManager.findDimAggregateId(combinedDims); 
					TreeMap<Long, Entry<Long[], Double>> factsForDimension = dbReader.loadFactsForDimCombination(((Ratio)ratios.get(0)).getId(), dimId);
					 
					if (factsForDimension == null) {
						mainWindowController.postStatus(StatusMessage.NO_DATA_FOR_INPUT);
						return;
					}
					
					resultGrid.fillDimCombination(factsForDimension);
					break;
				}
				case MIXED: {
					break;
				}
			}
		}
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
			if (dims.size() == 1) {
				return QueryType.SINGLE_DIMENSION;
			} else {
				return QueryType.DIMENSION_COMBINATION;
			}
		} else if (type == ReferenceObject.class) {
			if (dims.size() == 1) {
				return QueryType.SINGLE_REFERENCE_OBJECT;
			} else {
				return QueryType.REFERENCE_OBJECT_COMBINATION;
			}
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
