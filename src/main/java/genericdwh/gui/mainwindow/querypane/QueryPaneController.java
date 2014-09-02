package genericdwh.gui.mainwindow.querypane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.db.DatabaseReader;
import genericdwh.db.ResultObject;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.StatusMessage;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGrid;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGridController;
import genericdwh.main.Main;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class QueryPaneController implements Initializable {
		
	@FXML private Pane queryPane;
	
	@FXML private VBox resultGridContainer;
	
	@FXML private TableView<DataObject> tvRatio;
	@FXML private TableView<DataObject> tvRowDims;
	@FXML private TableView<DataObject> tvColDims;
	
	@FXML private TableColumn<DataObject, DataObject> tcRatio;
	@FXML private TableColumn<DataObject, DataObject> tcRowDims;
	@FXML private TableColumn<DataObject, DataObject> tcColDims;

	@FXML private Button btnExecQuery;
	@FXML private Button btnClear;
	
	ResultGridController resultGridController;
	
	private enum QueryType {
		MIXED,
		SINGLE_DIMENSION,
		DIMENSION_COMBINATION,
		SINGLE_REFERENCE_OBJECT,
		REFERENCE_OBJECT_COMBINATION;
	}
	
	public QueryPaneController(ResultGridController resultGridController) {
		this.resultGridController = resultGridController;
	}
	
	public void initialize(URL location, ResourceBundle resources) {				
		queryPane.getStylesheets().add("/css/TableViewNoHorizontalScrollBar.css");
		
		tvRatio.getStyleClass().add("no-horizontal-scrollbar");
		tvRowDims.getStyleClass().add("no-horizontal-scrollbar");
		tvColDims.getStyleClass().add("no-horizontal-scrollbar");
		
		tvRatio.setPlaceholder(new Text(""));
		tvRowDims.setPlaceholder(new Text(""));
		tvColDims.setPlaceholder(new Text(""));
		
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

			if ((draggedDataObject instanceof Ratio && tvSource != tvRatio)
				|| (!(draggedDataObject instanceof Ratio) && tvSource == tvRatio)) {
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
		resetResultGrids();
				
		List<DataObject> ratios = tvRatio.getItems();
		List<DataObject> rowDims = tvRowDims.getItems();
		List<DataObject> colDims = tvColDims.getItems();
		
		if (ratios.isEmpty()) {
			ratios = new ArrayList<>(Main.getContext().getBean(RatioManager.class).getRatios().values());
		}
				
		boolean queryHadResults = false;
		if (rowDims.isEmpty() && colDims.isEmpty()) {
			queryHadResults = handleNoReferenceObjectsOrDimensions(ratios);
		} else {	
			ArrayList<DataObject> combinedDims = new ArrayList<>();
			combinedDims.addAll(rowDims);
			combinedDims.addAll(colDims);
			
			switch (determineQueryType(combinedDims)) {
				case SINGLE_REFERENCE_OBJECT: {
					queryHadResults = handleSingleReferenceObject(ratios, rowDims, colDims, combinedDims);
					break;
				}
				case REFERENCE_OBJECT_COMBINATION: {
					queryHadResults = handleReferenceObjectCombination(ratios, rowDims, colDims, combinedDims);
					break;
				}
				case SINGLE_DIMENSION: {
					queryHadResults = handleSingleDimension(ratios, rowDims, colDims, combinedDims);
					break;
				}
				case DIMENSION_COMBINATION: {
					queryHadResults = handleDimensionCombination(ratios, rowDims, colDims, combinedDims);
					break;
				}
				case MIXED: {
					handleMixed(ratios, rowDims, colDims, combinedDims);
					break;
				}
			}
		}
		
		if (!queryHadResults) {
			Main.getContext().getBean(MainWindowController.class).postStatus(StatusMessage.NO_DATA_FOR_INPUT);
		}
	}
	
	@FXML public void buttonClearOnClickHandler() {
		tvRatio.getItems().clear();                    
		tvRowDims.getItems().clear();
		tvColDims.getItems().clear();
		
		resetResultGrids();
	}

	
	private void resetResultGrids() {
		resultGridContainer.getChildren().clear();
		resultGridController.reset();
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
	
	private boolean handleNoReferenceObjectsOrDimensions(List<DataObject> ratios) {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		
		boolean queryHadResults = false;
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			ArrayList<ResultObject> allFactsForRatio = dbReader.loadAllFactsForRatio(ratio.getId());
			if (allFactsForRatio != null) {
				queryHadResults = true;
				
				TreeMap<Long, ReferenceObject> refObjs = new TreeMap<>();
				for (ResultObject resObj : allFactsForRatio) {
					ReferenceObject refObj = refObjManager.loadRefObj(resObj.getId());
					refObjs.put(refObj.getId(), refObj);
				}
				ArrayList<TreeMap<Long, ReferenceObject>> rows = new ArrayList<>();
				rows.add(refObjs);
									
				String unitSymbol = unitManager.getUnit(allFactsForRatio.get(0).getUnitId()).getSymbol();
				resultGridController.initializeGrid(rows, new ArrayList<>(), ratio.getName(), unitSymbol);
				resultGridController.fillRatio(allFactsForRatio);
			} else if (queryHadResults){					
				resultGridController.initializeGridNoData(ratio.getName());
			}
		}
		
		return queryHadResults;
	}
	
	private boolean handleSingleReferenceObject(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims, ArrayList<DataObject> combinedDims) {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		
		boolean queryHadResults = false;
		
		long refObjId = combinedDims.get(0).getId();
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			ResultObject factForRefObj = dbReader.loadFactForSingleRefObj(ratio.getId(), refObjId);
			if (factForRefObj != null) {
				queryHadResults = true;

				String unitSymbol = unitManager.getUnit(factForRefObj.getUnitId()).getSymbol();
				resultGridController.initializeGrid(refObjManager.loadRefObjs(rowDims), refObjManager.loadRefObjs(colDims), ratio.getName(), unitSymbol);
				resultGridController.fillReferenceObject(factForRefObj);
			} else if (queryHadResults){					
				resultGridController.initializeGridNoData(ratio.getName());
			}
		}
		
		return queryHadResults;
	}
	
	private boolean handleReferenceObjectCombination(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims, ArrayList<DataObject> combinedDims) {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		
		boolean queryHadResults = false;
		
		long refObjId = refObjManager.findRefObjAggregateId(combinedDims);
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			ResultObject factForRefObj = dbReader.loadFactForRefObjCombination(ratio.getId(), refObjId);
			if (factForRefObj != null) {
				queryHadResults = true;
				
				String unitSymbol = unitManager.getUnit(factForRefObj.getUnitId()).getSymbol();
				resultGridController.initializeGrid(refObjManager.loadRefObjs(rowDims), refObjManager.loadRefObjs(colDims), ratio.getName(), unitSymbol);
				resultGridController.fillReferenceObject(factForRefObj);
			} else if (queryHadResults){					
				resultGridController.initializeGridNoData(ratio.getName());
			}
		}

		return queryHadResults;
	}

	
	private boolean handleSingleDimension(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims, ArrayList<DataObject> combinedDims) {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		
		boolean queryHadResults = false;

		long dimId = combinedDims.get(0).getId();
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			ArrayList<ResultObject> factsForDimension = dbReader.loadFactsForSingleDim(ratio.getId(), dimId);
			if (factsForDimension != null) {
				queryHadResults = true;
											
				String unitSymbol = unitManager.getUnit(factsForDimension.get(0).getUnitId()).getSymbol();
				resultGridController.initializeGridWithTotals(refObjManager.loadRefObjs(rowDims), refObjManager.loadRefObjs(colDims), ratio.getName(), unitSymbol);
				resultGridController.fillDimension(factsForDimension);
			} else if (queryHadResults){					
				resultGridController.initializeGridNoData(ratio.getName());
			}
		}
		
		return queryHadResults;
	}

	private boolean handleDimensionCombination(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims, ArrayList<DataObject> combinedDims) {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		
		boolean queryHadResults = false;

		long dimId = dimManager.findDimAggregateId(combinedDims);
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			ArrayList<ResultObject> factsForDimension = dbReader.loadFactsForDimCombination(ratio.getId(), dimId);
			if (factsForDimension != null) {
				queryHadResults = true;

				String unitSymbol = unitManager.getUnit(factsForDimension.get(0).getUnitId()).getSymbol();
				resultGridController.initializeGridWithTotals(refObjManager.loadRefObjs(rowDims), refObjManager.loadRefObjs(colDims), ratio.getName(), unitSymbol);
				resultGridController.fillDimension(factsForDimension);
			} else if (queryHadResults){					
				resultGridController.initializeGridNoData(ratio.getName());
			}
		}
		
		return queryHadResults;
	}

	private boolean handleMixed(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims, ArrayList<DataObject> combinedDims) {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		
		boolean queryHadResults = false;

		long dimId = dimManager.findDimAggregateId(combinedDims);
		Long[] refObjIds = refObjManager.readRefObjComponentIds(combinedDims);
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			ArrayList<ResultObject> factsForDimensionAndRefObjs = dbReader.loadFactsForDimCombinationAndRefObjs(ratio.getId(), dimId, refObjIds);
			if (factsForDimensionAndRefObjs != null) {
				queryHadResults = true;
				
				String unitSymbol = unitManager.getUnit(factsForDimensionAndRefObjs.get(0).getUnitId()).getSymbol();
				resultGridController.initializeGridWithTotals(refObjManager.loadRefObjs(rowDims), refObjManager.loadRefObjs(colDims), ratio.getName(), unitSymbol);
				resultGridController.fillDimension(factsForDimensionAndRefObjs);
			} else if (queryHadResults){					
				resultGridController.initializeGridNoData(ratio.getName());
			}
		}
		
		return queryHadResults;
	}

}
