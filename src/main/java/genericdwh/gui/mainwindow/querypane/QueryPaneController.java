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
import genericdwh.db.DatabaseReader;
import genericdwh.db.ResultObject;
import genericdwh.gui.general.ExecutionMessages;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.StatusMessages;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.querypane.dialogpopups.ExecutionFailurePopupDialogController;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGrid;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGridController;
import genericdwh.gui.mainwindow.sidebar.TableCellRightClickHandler;
import genericdwh.main.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ToggleGroup;
import javafx.util.Callback;
import lombok.Getter;

public class QueryPaneController implements Initializable {
		
	@FXML private Pane queryPane;
	
	@FXML private VBox resultGridContainer;
	
	@Getter @FXML private TableView<DataObject> tvColDims;
	@Getter @FXML private TableView<DataObject> tvRowDims;
	@Getter @FXML private TableView<DataObject> tvFilter;
	@Getter @FXML private TableView<DataObject> tvRatios;

	@FXML private TableColumn<DataObject, DataObject> tcColDims;
	@FXML private TableColumn<DataObject, DataObject> tcRowDims;
	@FXML private TableColumn<DataObject, DataObject> tcFilters;
	@FXML private TableColumn<DataObject, DataObject> tcRatios;

	@FXML private Button btnExecQuery;
	@FXML private Button btnClear;
	
	@FXML private RadioButton rbTotals;
	@FXML private RadioButton rbMeans;
	@FXML private ToggleGroup tgAggregation;
	
	@Getter private BooleanProperty executedQuery = new SimpleBooleanProperty(false);
	public void setExecutedQuery(boolean value) { executedQuery.set(value); };
	
	private ResultGridController resultGridController;
	
	private ExecutionFailurePopupDialogController executionFailurePopupDialogController;
	
	public enum QueryType {
		MIXED,
		MIXED_W_HIERARCHY,
		SINGLE_DIMENSION,
		SINGLE_DIMENSION_W_HIERARCHY,
		DIMENSION_COMBINATION,
		DIMENSION_COMBINATION_W_HIERARCHY,
		SINGLE_REFERENCE_OBJECT,
		REFERENCE_OBJECT_COMBINATION;
	}
	
	public enum AggregationType {
		TOTALS("Totals"),
		MEANS("Means");
		
		@Getter private String name;
		private AggregationType(String name) {
			this.name = name;
		}
	}

	
	public QueryPaneController(ResultGridController resultGridController, ExecutionFailurePopupDialogController executionFailurePopupDialogController) {
		this.resultGridController = resultGridController;
		this.executionFailurePopupDialogController = executionFailurePopupDialogController;
	}
	
	public void initialize(URL location, ResourceBundle resources) {				
		queryPane.getStylesheets().add("/css/TableViewNoHorizontalScrollBar.css");
		
		tvColDims.getStyleClass().add("no-horizontal-scrollbar");
		tvRowDims.getStyleClass().add("no-horizontal-scrollbar");
		tvFilter.getStyleClass().add("no-horizontal-scrollbar");
		tvRatios.getStyleClass().add("no-horizontal-scrollbar");
		
		tvColDims.setPlaceholder(new Text(""));
		tvRowDims.setPlaceholder(new Text(""));
		tvFilter.setPlaceholder(new Text(""));
		tvRatios.setPlaceholder(new Text(""));
		
		tcColDims.setCellFactory(new Callback<TableColumn<DataObject, DataObject>, TableCell<DataObject, DataObject>>() {
            public TableCell<DataObject, DataObject> call(TableColumn<DataObject, DataObject> param) {
            	DataObjectTableCell tableCell =  new DataObjectTableCell();
            	
            	tableCell.setOnMouseClicked(new TableCellRightClickHandler(tvColDims));
            	tableCell.setContextMenu(new TableCellContextMenu(tvColDims, tableCell));
            	return tableCell;            
            }
        });
		
		tcRowDims.setCellFactory(new Callback<TableColumn<DataObject, DataObject>, TableCell<DataObject, DataObject>>() {
            public TableCell<DataObject, DataObject> call(TableColumn<DataObject, DataObject> param) {
            	DataObjectTableCell tableCell =  new DataObjectTableCell();
            	
            	tableCell.setOnMouseClicked(new TableCellRightClickHandler(tvRowDims));
            	tableCell.setContextMenu(new TableCellContextMenu(tvRowDims, tableCell));

            	return tableCell;
            }
        });
		
		tcFilters.setCellFactory(new Callback<TableColumn<DataObject, DataObject>, TableCell<DataObject, DataObject>>() {
            public TableCell<DataObject, DataObject> call(TableColumn<DataObject, DataObject> param) {
            	DataObjectTableCell tableCell =  new DataObjectTableCell();
            	
            	tableCell.setOnMouseClicked(new TableCellRightClickHandler(tvFilter));
            	tableCell.setContextMenu(new TableCellContextMenu(tvFilter, tableCell));

            	return tableCell;            
            }
        });
		
		tcRatios.setCellFactory(new Callback<TableColumn<DataObject, DataObject>, TableCell<DataObject, DataObject>>() {
            public TableCell<DataObject, DataObject> call(TableColumn<DataObject, DataObject> param) {
            	DataObjectTableCell tableCell =  new DataObjectTableCell();
            	
            	tableCell.setOnMouseClicked(new TableCellRightClickHandler(tvRatios));
            	tableCell.setContextMenu(new TableCellContextMenu(tvRatios, tableCell));

            	return tableCell;
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
			Object gestureSource = event.getGestureSource();			
			if (gestureSource instanceof TableView) {
				@SuppressWarnings("unchecked")
				TableView<DataObject> tvSource = (TableView<DataObject>)gestureSource;
				tvSource.getItems().remove(draggedDataObject);
			}
			
			Object gestureTarget = event.getGestureTarget();
			if (gestureTarget instanceof TableView) {
				@SuppressWarnings("unchecked")
				TableView<DataObject> tvTarget = (TableView<DataObject>)gestureTarget;
				tvTarget.getItems().add(draggedDataObject);
				
				queryPane.requestFocus();
			}

			completed = true;
		}
		
		Main.getContext().getBean(MainWindowController.class).setDraggedDataObject(null);
		
		event.setDropCompleted(completed);
		event.consume();
	}
	
	@FXML public void onDragOverHandler(DragEvent event) {
		DataObject draggedDataObject = Main.getContext().getBean(MainWindowController.class).getDraggedDataObject();
		if (draggedDataObject != null) {
			Object source = event.getSource();
			Object gestureSource = event.getGestureSource();
			if ((source != tvRatios && draggedDataObject instanceof Ratio)
				|| (source == tvRatios && !(draggedDataObject instanceof Ratio))
				|| (source == tvFilter && !(draggedDataObject instanceof ReferenceObject))) {
				event.consume();
				return;
			}
			
			if (!(gestureSource instanceof TableView)
					&& (tvRatios.getItems().contains(draggedDataObject)
						|| tvRowDims.getItems().contains(draggedDataObject)
						|| tvColDims.getItems().contains(draggedDataObject)
						|| tvFilter.getItems().contains(draggedDataObject))) {
				
				event.consume();
				return;
			}
			
			if (source == gestureSource) {
				event.consume();
				return;
			}
			
			event.acceptTransferModes(TransferMode.ANY);
		}
		
		event.consume();
	}	
	
	@FXML public void buttonExecuteQueryOnClickHandler() {	
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		
		mainWindowController.postStatus(StatusMessages.QUERYING, Icons.NOTIFICATION);
		Main.getLogger().info(StatusMessages.QUERYING);
		
		setExecutedQuery(false);
		
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		
		resetResultGrids();
				
		List<DataObject> ratios = tvRatios.getItems();
		List<DataObject> rowDims = tvRowDims.getItems();
		List<DataObject> colDims = tvColDims.getItems();
		List<DataObject> filter = tvFilter.getItems();
		
		Main.getLogger().info("Query parameters " 
				+ "- Column Dimensions: " + colDims.toString()
				+ ", Row Dimensions: " + rowDims.toString()
				+ ", Filtered Reference Objects: " + filter.toString()
				+ ", Ratios: " + ratios.toString());
		
		if (ratios.isEmpty()) {
			if (rowDims.isEmpty() && colDims.isEmpty()) {
				mainWindowController.postStatus(StatusMessages.QUERY_NO_DATA, Icons.WARNING);
				Main.getLogger().info(StatusMessages.QUERY_NO_DATA);
				return;
			}
			
			ratios = new ArrayList<>(Main.getContext().getBean(RatioManager.class).getRatios().values());
		}
		
		boolean hasResults = false;
		if (rowDims.isEmpty() && colDims.isEmpty()) {
			hasResults = handleNoReferenceObjectsOrDimensions(ratios, filter);
		} else {	
			List<DataObject> combinedDims = new ArrayList<>();
			combinedDims.addAll(rowDims);
			combinedDims.addAll(colDims);
			
			List<TreeMap<Long, ReferenceObject>> rowRefObjs = refObjManager.loadRefObjs(rowDims, filter);
			List<TreeMap<Long, ReferenceObject>> colRefObjs = refObjManager.loadRefObjs(colDims, filter);
			if (rowRefObjs.isEmpty() && colRefObjs.isEmpty()) {
				showExecutionFailure(ExecutionMessages.NO_REFERENCE_OBJECTS);
				Main.getLogger().error("Query execution failed: " + ExecutionMessages.NO_REFERENCE_OBJECTS);
				return;
			}
			
			AggregationType aggregationType = determineAggregationType((RadioButton)tgAggregation.getSelectedToggle());
			
			long refObjId = refObjManager.findRefObjAggregateId(combinedDims);
			long dimId = dimManager.findDimAggregateId(combinedDims);
			Long[] filterRefObjIds = refObjManager.readRefObjIds(filter);
			
			switch (determineQueryType(combinedDims)) {
				case SINGLE_REFERENCE_OBJECT: {
					hasResults = handleSingleReferenceObject(ratios, rowRefObjs, colRefObjs, refObjId);
					break;
				}
				case REFERENCE_OBJECT_COMBINATION: {
					hasResults = handleReferenceObjectCombination(ratios, rowRefObjs, colRefObjs, refObjId);
					break;
				}
				case SINGLE_DIMENSION: {
					hasResults = handleSingleDimension(ratios, rowRefObjs, colRefObjs, dimId, filterRefObjIds, aggregationType);
					break;
				}
				case SINGLE_DIMENSION_W_HIERARCHY: {
					hasResults = handleSingleDimensionWHierarchy(ratios, rowDims, colDims, dimId, filterRefObjIds, combinedDims, filter, aggregationType);
					if (!hasResults) {
						mainWindowController.postStatus(StatusMessages.QUERY_NO_DATA_ON_CURRENT_LEVELS, Icons.WARNING);
						Main.getLogger().info(StatusMessages.QUERY_NO_DATA_ON_CURRENT_LEVELS);
						setExecutedQuery(true);
						return;
					}
					break;
				}
				case DIMENSION_COMBINATION: {
					hasResults = handleDimensionCombination(ratios, rowRefObjs, colRefObjs, dimId, filterRefObjIds, aggregationType);
					break;
				}
				case DIMENSION_COMBINATION_W_HIERARCHY: {
					hasResults = handleDimensionCombinationWHierarchy(ratios, rowDims, colDims, dimId, filterRefObjIds, combinedDims, filter, aggregationType);
					if (!hasResults) {
						mainWindowController.postStatus(StatusMessages.QUERY_NO_DATA_ON_CURRENT_LEVELS, Icons.WARNING);
						Main.getLogger().info(StatusMessages.QUERY_NO_DATA_ON_CURRENT_LEVELS);
						setExecutedQuery(true);
						return;
					}
					break;
				}
				case MIXED: {
					hasResults = handleMixed(ratios, rowRefObjs, colRefObjs, dimId, filterRefObjIds, combinedDims, aggregationType);
					break;
				}
				case MIXED_W_HIERARCHY: {
					hasResults = handleMixedWHierarchy(ratios, rowDims, colDims, dimId, filterRefObjIds, combinedDims, filter, aggregationType);
					if (!hasResults) {
						mainWindowController.postStatus(StatusMessages.QUERY_NO_DATA_ON_CURRENT_LEVELS, Icons.WARNING);
						Main.getLogger().info(StatusMessages.QUERY_NO_DATA_ON_CURRENT_LEVELS);
						setExecutedQuery(true);
						return;
					}
					break;
				}
			}
		}
		
		if (!hasResults) {
			mainWindowController.postStatus(StatusMessages.QUERY_NO_DATA, Icons.WARNING);
			Main.getLogger().info(StatusMessages.QUERY_NO_DATA);
			return;
		}
		
		mainWindowController.postStatus(StatusMessages.QUERY_OK, Icons.NOTIFICATION);
		Main.getLogger().info("Query executed successfully.");
		setExecutedQuery(true);
	}	
	
	@FXML public void buttonClearOnClickHandler() {
		clear();
	}
	
	public void clear() {
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		mainWindowController.clearStatus();
		mainWindowController.hideSQLQueries();

		tvRatios.getItems().clear();                    
		tvRowDims.getItems().clear();
		tvColDims.getItems().clear();
		tvFilter.getItems().clear();
		
		resetResultGrids();
		
		setExecutedQuery(false);
	}

	
	private void resetResultGrids() {
		resultGridContainer.getChildren().clear();
		resultGridController.reset();
	}
	
	
	private QueryType determineQueryType(List<DataObject> dims) {
		boolean mixed = false;
		boolean hasHierarchy = false;
		
		Class<?> type = dims.get(0).getClass();
				
		if (type == DimensionHierarchy.class) {
			hasHierarchy = true;
			type = Dimension.class;
		}
		
		for (DataObject obj : dims) {
			Class<?> currType = obj.getClass();
			if (currType == DimensionHierarchy.class) {
				hasHierarchy = true;
				currType = Dimension.class;
			}
			
			if (type != currType) {
				mixed = true;
			}
		}
		
		if (mixed) {
			if (hasHierarchy) {
				return QueryType.MIXED_W_HIERARCHY;
			}
			return QueryType.MIXED;
		} else if (type == Dimension.class) {
			if (dims.size() == 1) {
				if (hasHierarchy) {
					return QueryType.SINGLE_DIMENSION_W_HIERARCHY;
				}
				return QueryType.SINGLE_DIMENSION;
			} else {
				if (hasHierarchy) {
					return QueryType.DIMENSION_COMBINATION_W_HIERARCHY;
				}
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
	
	private AggregationType determineAggregationType(RadioButton selectedRadioButton) {
		if (selectedRadioButton == rbTotals) {
			return AggregationType.TOTALS;
		} else if (selectedRadioButton == rbMeans) {
			return AggregationType.MEANS;
		}
		
		return null;
	}
	
	private boolean handleNoReferenceObjectsOrDimensions(List<DataObject> ratios, List<DataObject> filter) {
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		dbReader.clearLastQueries();
		
		Long[] filterRefObjIds = refObjManager.readRefObjIds(filter);
		
		boolean hasResults = false;
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			List<ResultObject> facts = dbReader.loadAllFactsForRatio(ratio.getId(), filterRefObjIds);
			if (facts != null) {
				hasResults = true;
				
				TreeMap<Long, ReferenceObject> refObjs = new TreeMap<>();
				for (ResultObject resObj : facts) {
					ReferenceObject refObj = refObjManager.loadRefObj(resObj.getId());
					refObjs.put(refObj.getId(), refObj);
				}
				List<TreeMap<Long, ReferenceObject>> rows = new ArrayList<>();
				rows.add(refObjs);

				resultGridController.initializeGrid((Ratio)ratio, rows, new ArrayList<>());
				resultGridController.fillRatio(facts);
			}
		}
		return hasResults;
	}

	private boolean handleSingleReferenceObject(List<DataObject> ratios, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs,
			long refObjId) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		dbReader.clearLastQueries();
		
		boolean hasResults = false;
						
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			ResultObject fact = dbReader.loadFactForSingleRefObj(ratio.getId(), refObjId);
			if (fact != null) {
				hasResults = true;
				
				resultGridController.initializeGrid((Ratio)ratio, rowRefObjs, colRefObjs);
				resultGridController.fillReferenceObject(fact);
			}
		}
		return hasResults;
	}
	
	private boolean handleReferenceObjectCombination(List<DataObject> ratios, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs,
			long refObjId) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		dbReader.clearLastQueries();
				
		boolean hasResults = false;
								
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
						
			ResultObject facts = dbReader.loadFactForRefObjCombination(ratio.getId(), refObjId);
			if (facts != null) {
				hasResults = true;
				
				resultGridController.initializeGrid((Ratio)ratio, rowRefObjs, colRefObjs);
				resultGridController.fillReferenceObject(facts);
			}
		}
		return hasResults;
	}
		
	private boolean handleSingleDimension(List<DataObject> ratios, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs,
			long dimId, Long[] filterRefObjIds,
			AggregationType aggregationType) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		dbReader.clearLastQueries();
		
		boolean hasResults = false;
				
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
						
			List<ResultObject> facts = dbReader.loadFactsForSingleDim(ratio.getId(), dimId, filterRefObjIds);
			if (facts != null) {
				hasResults = true;
				
				resultGridController.initializeGridWAggregations((Ratio)ratio, rowRefObjs, colRefObjs, aggregationType);
				resultGridController.fillDimension(facts);
			}
		}
		return hasResults;
	}	
		
	private boolean handleSingleDimensionWHierarchy(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims,
			long dimId, Long[] filterRefObjIds,
			List<DataObject> combinedDims, List<DataObject> filter,
			AggregationType aggregationType) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		dbReader.clearLastQueries();
				
		boolean hasResults = false;
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			newResultGrid.setGridId(resultGridController.addResultGrid(newResultGrid));
			
			resultGridController.initializeGridWHierarchiesWAggregations((Ratio)ratio, new ArrayList<>(rowDims), new ArrayList<>(colDims), new ArrayList<>(combinedDims),
					filter, getHierarchies(combinedDims), QueryType.SINGLE_DIMENSION_W_HIERARCHY, aggregationType);
						
			List<ResultObject> facts = dbReader.loadFactsForSingleDim(ratio.getId(), dimId, filterRefObjIds);	
			resultGridController.fillDimension(facts);
			
			if (facts != null) {
				hasResults = true;
			}
		}
		return hasResults;		
	}
		
	private boolean handleDimensionCombination(List<DataObject> ratios, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs,
			long dimId, Long[] filterRefObjIds,
			AggregationType aggregationType) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		dbReader.clearLastQueries();

		boolean hasResults = false;
				
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
						
			List<ResultObject> facts = dbReader.loadFactsForDimCombination(ratio.getId(), dimId, filterRefObjIds);
			if (facts != null) {
				hasResults = true;
				
				resultGridController.initializeGridWAggregations((Ratio)ratio, rowRefObjs, colRefObjs, aggregationType);
				resultGridController.fillDimension(facts);
			}
		}
		return hasResults;
	}
		
	private boolean handleDimensionCombinationWHierarchy(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims,
			long dimId, Long[] filterRefObjIds,
			List<DataObject> combinedDims, List<DataObject> filter,
			AggregationType aggregationType) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		dbReader.clearLastQueries();
					
		boolean hasResults = false;
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			newResultGrid.setGridId(resultGridController.addResultGrid(newResultGrid));
			
			resultGridController.initializeGridWHierarchiesWAggregations((Ratio)ratio, new ArrayList<>(rowDims), new ArrayList<>(colDims), new ArrayList<>(combinedDims),
					filter,  getHierarchies(combinedDims), QueryType.DIMENSION_COMBINATION_W_HIERARCHY, aggregationType);
			
			List<ResultObject> facts = dbReader.loadFactsForDimCombination(ratio.getId(), dimId, filterRefObjIds);
			resultGridController.fillDimension(facts);
			
			if (facts != null) {
				hasResults = true;
			}
		}
		return hasResults;
	}
		
	private boolean handleMixed(List<DataObject> ratios, List<TreeMap<Long, ReferenceObject>> rowRefObjs, List<TreeMap<Long, ReferenceObject>> colRefObjs,
			long dimId, Long[] filterRefObjIds,
			List<DataObject> combinedDims,
			AggregationType aggregationType) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		dbReader.clearLastQueries();
						
		boolean hasResults = false;
		
		Long[] refObjIds = refObjManager.readRefObjIds(combinedDims);
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			resultGridController.addResultGrid(newResultGrid);
			
			List<ResultObject> facts = dbReader.loadFactsForDimCombinationAndRefObjs(ratio.getId(), dimId, refObjIds, filterRefObjIds);
			
			if (facts != null) {
				hasResults = true;
				
				resultGridController.initializeGridWAggregations((Ratio)ratio, rowRefObjs, colRefObjs, aggregationType);
				resultGridController.fillDimension(facts);
			}
		}
		return hasResults;
	}
			
	private boolean handleMixedWHierarchy(List<DataObject> ratios, List<DataObject> rowDims, List<DataObject> colDims,
			long dimId, Long[] filterRefObjIds,
			List<DataObject> combinedDims, List<DataObject> filter,
			AggregationType aggregationType) {
		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		dbReader.clearLastQueries();
		
		Long[] refObjIds = refObjManager.readRefObjIds(combinedDims);
		
		boolean hasResults = false;
		
		for (DataObject ratio : ratios) {
			ResultGrid newResultGrid = new ResultGrid();
			resultGridContainer.getChildren().add(newResultGrid);
			newResultGrid.setGridId(resultGridController.addResultGrid(newResultGrid));
			
			resultGridController.initializeGridWHierarchiesWAggregations((Ratio)ratio, new ArrayList<>(rowDims), new ArrayList<>(colDims),
					new ArrayList<>(combinedDims), filter, getHierarchies(combinedDims), QueryType.MIXED_W_HIERARCHY, aggregationType);
			
			List<ResultObject> facts = dbReader.loadFactsForDimCombinationAndRefObjs(ratio.getId(), dimId, refObjIds, filterRefObjIds);
			resultGridController.fillDimension(facts);

			if (facts != null) {
				hasResults = true;
			}
		}
		return hasResults;
	}
	

	private List<DimensionHierarchy> getHierarchies(List<DataObject> combinedDims) {
		List<DimensionHierarchy> hierarchies = new ArrayList<>();
		for (DataObject obj : combinedDims) {
			if (obj instanceof DimensionHierarchy) {
				hierarchies.add((DimensionHierarchy)obj);
			}
		}
		return hierarchies;
	}
	
	public void showExecutionFailure(String message) {
		executionFailurePopupDialogController.createWindow(message);
	}
	
	public void addColDimension(DataObject dim) {
		if (!tvColDims.getItems().contains(dim)) {
			tvColDims.getItems().add(dim);
		}
		queryPane.requestFocus();
	}
	
	public void addRowDimension(DataObject dim) {
		if (!tvRowDims.getItems().contains(dim)) {
			tvRowDims.getItems().add(dim);
		}
		queryPane.requestFocus();
	}
	
	public void addFilter(DataObject dim) {
		if (!tvFilter.getItems().contains(dim)) {
			tvFilter.getItems().add(dim);
		}
		queryPane.requestFocus();
	}
	
	public void addRatio(DataObject dim) {
		if (!tvRatios.getItems().contains(dim)) {
			tvRatios.getItems().add(dim);
		}
		queryPane.requestFocus();
	}
}
