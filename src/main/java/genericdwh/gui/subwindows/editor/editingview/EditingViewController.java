package genericdwh.gui.subwindows.editor.editingview;

import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectCombination;
import genericdwh.dataobjects.DataObjectHierarchy;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionCombination;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.fact.FactManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectCombination;
import genericdwh.dataobjects.referenceobject.ReferenceObjectHierarchy;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.gui.general.sidebar.DataObjectTreeItem;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import lombok.Getter;

public class EditingViewController implements Initializable {
	
	private SearchBoxController searchBoxController;

	@Getter @FXML private TreeTableView<DataObject> editingView;
	
	@Getter private BooleanProperty hasUnsavedChanges = new SimpleBooleanProperty();
	public void setHasUnsavedChanges(boolean hasUnsavedChanges) { this.hasUnsavedChanges.set(hasUnsavedChanges); };
	
	private TreeMap<Long, DataObjectTreeItem> tiHeaderMap;
	
	@Getter private EditingViewType currEditingViewType = null;
		
	public enum EditingViewType {
		DIMENSIONS(Dimension.class),
		DIMENSIONS_BY_CATEGORY(Dimension.class),
		DIMENSION_HIERARCHIES(DimensionHierarchy.class),
		DIMENSION_HIERARCHIES_BY_CATEGORY(DimensionHierarchy.class),
		DIMENSION_COMBINATIONS(DimensionCombination.class),
		REFERENCE_OBJECTS(ReferenceObject.class),
		REFERENCE_OBJECTS_BY_DIMENSION(ReferenceObject.class),
		REFERENCE_OBJECT_HIERARCHIES(ReferenceObjectHierarchy.class),
		REFERENCE_OBJECT_HIERARCHIES_BY_CATEGORY(ReferenceObjectHierarchy.class),
		REFERENCE_OBJECT_COMBINATIONS(ReferenceObjectCombination.class),
		REFERENCE_OBJECT_COMBINATIONS_BY_DIMENSION(ReferenceObjectCombination.class),
		RATIOS(Ratio.class),
		RATIOS_BY_CATEGORY(Ratio.class),
		FACTS(Fact.class),
		FACTS_BY_RATIO(Fact.class),
		FACTS_BY_REFERENCE_OBJECT(Fact.class),
		DIMENSION_CATEGORIES(DimensionCategory.class),
		RATIO_CATEGORIES(RatioCategory.class),
		UNITS(Unit.class);
		
		private Class<? extends DataObject> objectClass;
		private EditingViewType(Class<? extends DataObject> clazz) {
			objectClass = clazz;
		}
	}

	
	public EditingViewController(SearchBoxController searchBoxController) {
		this.searchBoxController = searchBoxController;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		editingView.setRowFactory(new Callback<TreeTableView<DataObject>, TreeTableRow<DataObject>>() {
			@Override
			public TreeTableRow<DataObject> call(TreeTableView<DataObject> param) {
				TreeTableRow<DataObject> treeTableRow = new TreeTableRow<>();
				treeTableRow.setOnMouseClicked(new TreeTableRowRightClickHandler(editingView));
				treeTableRow.setContextMenu(new TreeTableCellContextMenu(treeTableRow));
				return treeTableRow;
			}
		});
	}	

	public void createEditorTreeTable(int id) {	
		resetEditingView();
				
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		FactManager factManager = Main.getContext().getBean(FactManager.class);
		
		HeaderItem tiRoot = new HeaderItem("", -1, true, false);
		editingView.setRoot(tiRoot);
				
		currEditingViewType = EditingViewType.values()[id];
		switch (currEditingViewType) {
			case DIMENSIONS: {
				setupDimensionsTable();
				
				dimManager.initDimensions();
				dimManager.initCategories();
				
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						DataObjectTreeItem tiDim = new DataObjectTreeItem(dim);
						tiRoot.addChild(tiDim);
					}
				}
								
				break;
			}
			case DIMENSIONS_BY_CATEGORY: {
				setupDimensionsTable();
				
				dimManager.initCategories();
				dimManager.initDimensions();
				
				tiHeaderMap = new TreeMap<>();
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), cat.getId(), true, true);
					tiHeaderMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", 0, true, true);
				tiHeaderMap.put((long)0, tiNoCat);

				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						DataObjectTreeItem tiDim = new DataObjectTreeItem(dim);
						tiHeaderMap.get(dim.getCategoryId()).addChild(tiDim);
					}
				}
				
				for (DataObjectTreeItem tiCat : tiHeaderMap.values()) {
					if (tiCat.hasChildren()) {
						tiRoot.addChild(tiCat);
					}
				}
								
				break;
			}
			case DIMENSION_HIERARCHIES: {
				setupDimensionHierarchiesTable();
				
				dimManager.initCategories();
				dimManager.initDimensions();
				dimManager.initHierarchies();
				
				for (DimensionHierarchy dimHierarchy : dimManager.getHierarchies()) {
					DataObjectTreeItem tiDimHierarchy = new DataObjectTreeItem(dimHierarchy);
					tiRoot.addChild(tiDimHierarchy);
					
					DataObjectTreeItem currLevel = tiDimHierarchy;
					for (Dimension level : dimHierarchy.getLevels()) {
						DataObjectTreeItem tiLevel = new DataObjectTreeItem(level);
						currLevel.addChild(tiLevel);
						currLevel = tiLevel;
					}
				}
							
				break;
			}
			case DIMENSION_HIERARCHIES_BY_CATEGORY: {
				setupDimensionHierarchiesTable();
				
				dimManager.initCategories();
				dimManager.initDimensions();
				dimManager.initHierarchies();
				
				tiHeaderMap = new TreeMap<>();
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), cat.getId(), true, true);
					tiHeaderMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", 0, true, true);
				tiHeaderMap.put((long)0, tiNoCat);
				
				for (DimensionHierarchy dimHierarchy : dimManager.getHierarchies()) {
					DataObjectTreeItem tiDimHierarchy = new DataObjectTreeItem(dimHierarchy);
					tiHeaderMap.get(dimHierarchy.getCategoryId()).addChild(tiDimHierarchy);
					
					DataObjectTreeItem currLevel = tiDimHierarchy;
					for (Dimension level : dimHierarchy.getLevels()) {
						DataObjectTreeItem tiLevel = new DataObjectTreeItem(level);
						currLevel.addChild(tiLevel);
						currLevel = tiLevel;
					}
				}
				
				for (DataObjectTreeItem tiCat : tiHeaderMap.values()) {
					if (tiCat.hasChildren()) {
						tiRoot.addChild(tiCat);
					}
				}
					
				break;
			}
			case DIMENSION_COMBINATIONS: {
				setupDimensionCombinationsTable();
				
				dimManager.loadCombinations();
				dimManager.initDimensions();
				dimManager.initCombinations();
				
				for (DimensionCombination dimCombination : dimManager.getCombinations()) {
					DataObjectTreeItem tiDimCombination = new DataObjectTreeItem(dimCombination);
					tiRoot.addChild(tiDimCombination);
					
					for (Dimension component : dimCombination.getComponents()) {
						DataObjectTreeItem tiComponent = new DataObjectTreeItem(component);
						tiDimCombination.addChild(tiComponent);
					}
				}
				
				break;
				
			}
			case REFERENCE_OBJECTS: {
				setupReferenceObjectsTable();
				
				dimManager.initDimensions();
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();

				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					if (refObj.getDimensionId() == 0 || !refObj.isCombination()) {
						DataObjectTreeItem tiRefObj = new DataObjectTreeItem(refObj);
						tiRoot.addChild(tiRefObj);
					}
				}
								
				break;
			}
			case REFERENCE_OBJECTS_BY_DIMENSION: {
				setupReferenceObjectsTable();
				
				dimManager.initDimensions();
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				
				tiHeaderMap = new TreeMap<>();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						HeaderItem tiDim = new HeaderItem(dim.getName(), dim.getId(), true, true);
						tiHeaderMap.put(dim.getId(), tiDim);
					}
				}
				HeaderItem tiNoDim = new HeaderItem("No Dimension", 0, true, true);
				tiHeaderMap.put((long)0, tiNoDim);
				
				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					DataObjectTreeItem tiRefObj = new DataObjectTreeItem(refObj);
					tiHeaderMap.get(refObj.getDimensionId()).addChild(tiRefObj);
				}
				
				for (DataObjectTreeItem tiDim : tiHeaderMap.values()) {
					if (tiDim.hasChildren()) {
						tiRoot.addChild(tiDim);
					}
				}
								
				break;
			}
			case REFERENCE_OBJECT_HIERARCHIES: {
				setupReferenceObjectHierarchiesTable();
				
				dimManager.initCategories();
				dimManager.initDimensions();
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				refObjManager.loadHierarchies();
				refObjManager.initHierarchies();
				
				for (ReferenceObjectHierarchy refObjHierarchy : refObjManager.getHierarchies()) {
					DataObjectTreeItem tiRefObjHierarchy = new DataObjectTreeItem(refObjHierarchy);
					tiRoot.addChild(tiRefObjHierarchy);
					
					DataObjectTreeItem currLevel = tiRefObjHierarchy;
					for (ReferenceObject level : refObjHierarchy.getLevels()) {
						DataObjectTreeItem tiLevel = new DataObjectTreeItem(level);
						currLevel.addChild(tiLevel);
						currLevel = tiLevel;
					}
				}
							
				break;
			}
			case REFERENCE_OBJECT_HIERARCHIES_BY_CATEGORY: {
				setupReferenceObjectHierarchiesTable();
				
				dimManager.initCategories();
				dimManager.initDimensions();
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				refObjManager.loadHierarchies();
				refObjManager.initHierarchies();
				
				tiHeaderMap = new TreeMap<>();
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), cat.getId(), true, true);
					tiHeaderMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", 0, true, true);
				tiHeaderMap.put((long)0, tiNoCat);
								
				for (ReferenceObjectHierarchy refObjHierarchy : refObjManager.getHierarchies()) {
					DataObjectTreeItem tiRefObjHierarchy = new DataObjectTreeItem(refObjHierarchy);
					tiHeaderMap.get(refObjHierarchy.getCategoryId()).addChild(tiRefObjHierarchy);
					
					DataObjectTreeItem currLevel = tiRefObjHierarchy;
					for (ReferenceObject level : refObjHierarchy.getLevels()) {
						DataObjectTreeItem tiLevel = new DataObjectTreeItem(level);
						currLevel.addChild(tiLevel);
						currLevel = tiLevel;
					}
				}
				
				for (DataObjectTreeItem tiCat : tiHeaderMap.values()) {
					if (tiCat.hasChildren()) {
						tiRoot.addChild(tiCat);
					}
				}
					
				break;
			}
			case REFERENCE_OBJECT_COMBINATIONS: {
				setupReferenceObjectCombinationsTable();
				
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				refObjManager.loadCombinations();
				refObjManager.initCombinations();
				
				for (ReferenceObjectCombination refObjCombination : refObjManager.getCombinations()) {
					DataObjectTreeItem tiRefObjCombination = new DataObjectTreeItem(refObjCombination);
					tiRoot.addChild(tiRefObjCombination);
					
					for (ReferenceObject component : refObjCombination.getComponents()) {
						DataObjectTreeItem tiComponent = new DataObjectTreeItem(component);
						tiRefObjCombination.addChild(tiComponent);
					}
				}
				
				break;
			}
			case REFERENCE_OBJECT_COMBINATIONS_BY_DIMENSION: {
				setupReferenceObjectCombinationsTable();
				
				dimManager.initDimensions();
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				refObjManager.loadCombinations();
				refObjManager.initCombinations();
				
				tiHeaderMap = new TreeMap<>();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (dim.isCombination()) {
						HeaderItem tiDim = new HeaderItem(dim.getName(), dim.getId(), true, true);
						tiHeaderMap.put(dim.getId(), tiDim);
					}
				}
				HeaderItem tiNoDim = new HeaderItem("No Dimension", 0, true, true);
				tiHeaderMap.put((long)0, tiNoDim);
				
				for (ReferenceObjectCombination refObjCombination : refObjManager.getCombinations()) {
					DataObjectTreeItem tiRefObjCombination = new DataObjectTreeItem(refObjCombination);
					tiHeaderMap.get(refObjCombination.getCombination().getDimensionId()).addChild(tiRefObjCombination);
					
					for (ReferenceObject component : refObjCombination.getComponents()) {
						DataObjectTreeItem tiComponent = new DataObjectTreeItem(component);
						tiRefObjCombination.addChild(tiComponent);
					}
				}
				
				for (DataObjectTreeItem tiDim : tiHeaderMap.values()) {
					if (tiDim.hasChildren()) {
						tiRoot.addChild(tiDim);
					}
				}
				
				break;
			}
			case RATIOS: {
				setupRatiosTable();
				
				ratioManager.initCategories();
				ratioManager.initRatios();
				
				for (Ratio ratio : ratioManager.getRatios().values()) {
					DataObjectTreeItem tiRatio = new DataObjectTreeItem(ratio);
					tiRoot.addChild(tiRatio);
				}
								
				break;
			}
			case RATIOS_BY_CATEGORY: {
				setupRatiosTable();
				
				ratioManager.initCategories();
				ratioManager.initRatios();
				
				tiHeaderMap = new TreeMap<>();
				for (RatioCategory cat : ratioManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), cat.getId(), true, true);
					tiHeaderMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", 0, true, true);
				tiHeaderMap.put((long)0, tiNoCat);


				for (Ratio ratio : ratioManager.getRatios().values()) {
					DataObjectTreeItem tiRatio = new DataObjectTreeItem(ratio);
					tiHeaderMap.get(ratio.getCategoryId()).addChild(tiRatio);
				}
				
				for (DataObjectTreeItem tiCat : tiHeaderMap.values()) {
					if (tiCat.hasChildren()) {
						tiRoot.addChild(tiCat);
					}
				}
				
				break;
			}
			case FACTS: {
				setupFactsTable();
				
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				ratioManager.initRatios();
				unitManager.initUnits();
				factManager.loadFacts();
				factManager.initFacts();
				
				for (Fact fact : factManager.getFacts().values()) {
					DataObjectTreeItem tiFact = new DataObjectTreeItem(fact);
					tiRoot.addChild(tiFact);
				}
				
				break;
			}
			case FACTS_BY_RATIO: {
				setupFactsTable();
				
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				ratioManager.initRatios();
				unitManager.initUnits();
				factManager.loadFacts();
				factManager.initFacts();

				tiHeaderMap = new TreeMap<>();
				for (Ratio ratio : ratioManager.getRatios().values()) {
					HeaderItem tiRatio = new HeaderItem(ratio.getName(), ratio.getId(), true, true);
					tiHeaderMap.put(ratio.getId(), tiRatio);
				}
				
				for (Fact fact : factManager.getFacts().values()) {
					DataObjectTreeItem tiFact = new DataObjectTreeItem(fact);
					tiHeaderMap.get(fact.getRatioId()).addChild(tiFact);
				}
				
				for (DataObjectTreeItem tiRatio : tiHeaderMap.values()) {
					if (tiRatio.hasChildren()) {
						tiRoot.addChild(tiRatio);
					}
				}
				
				break;				
			}
			case FACTS_BY_REFERENCE_OBJECT: {
				setupFactsTable();
				
				refObjManager.loadReferenceObjects();
				refObjManager.initReferenceObjects();
				ratioManager.initRatios();
				unitManager.initUnits();
				factManager.loadFacts();
				factManager.initFacts();
				
				tiHeaderMap = new TreeMap<>();
				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					HeaderItem tiRefObj = new HeaderItem(refObj.getName(), refObj.getId(), true, true);
					tiHeaderMap.put(refObj.getId(), tiRefObj);
				}
				
				for (Fact fact : factManager.getFacts().values()) {
					DataObjectTreeItem tiFact = new DataObjectTreeItem(fact);
					tiHeaderMap.get(fact.getReferenceObjectId()).addChild(tiFact);
				}
				
				for (DataObjectTreeItem tiRefObj : tiHeaderMap.values()) {
					if (tiRefObj.hasChildren()) {
						tiRoot.addChild(tiRefObj);
					}
				}
				
				break;				
			}
			case DIMENSION_CATEGORIES: {
				setupDimCategoriesTable();
				
				dimManager.initCategories();
				
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					DataObjectTreeItem tiDimCat = new DataObjectTreeItem(cat);
					tiRoot.addChild(tiDimCat);
				}
				
				break;
			}
			case RATIO_CATEGORIES: {
				setupRatioCategoriesTable();
				
				ratioManager.initCategories();
				
				for (RatioCategory cat : ratioManager.getCategories().values()) {
					DataObjectTreeItem tiRatioCat = new DataObjectTreeItem(cat);
					tiRoot.addChild(tiRatioCat);
				}
				
				break;
			}
			case UNITS: {
				setupUnitsTable();
				
				unitManager.initUnits();
				
				for (Unit unit : unitManager.getUnits().values()) {
					DataObjectTreeItem tiUnit = new DataObjectTreeItem(unit);
					tiRoot.addChild(tiUnit);
				}
				
				break;
			}
		}
	}
	
	
	private void setupDimensionsTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return param.getValue().getValue().getNameProperty();
			}
		});
		
		TreeTableColumn<DataObject, DimensionCategory> colCategory = createDimCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, DimensionCategory>, ObservableValue<DimensionCategory>>() {
			public ObservableValue<DimensionCategory> call(CellDataFeatures<DataObject, DimensionCategory> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<DimensionCategory>(null);
				}
				return ((Dimension)param.getValue().getValue()).getCategoryProperty();
		    }
		});
	}
	
	private void setupDimensionHierarchiesTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				if (param.getValue().getValue() instanceof DimensionHierarchy || param.getValue() instanceof HeaderItem) {
					return param.getValue().getValue().getNameProperty();
				} else {
					return new SimpleStringProperty("Hierarchy Level");
				}
			}
		});
		
		TreeTableColumn<DataObject, Dimension> colLevel = createDimLevelCol();
		colLevel.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Dimension>, ObservableValue<Dimension>>() {
			public ObservableValue<Dimension> call(CellDataFeatures<DataObject, Dimension> param) {
				if (param.getValue().getValue() instanceof DimensionHierarchy || param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<Dimension>(null);
				} else {
					return new SimpleObjectProperty<Dimension>((Dimension)param.getValue().getValue());
				}
			}
		});
		
		TreeTableColumn<DataObject, DimensionCategory> colCategory = createDimCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, DimensionCategory>, ObservableValue<DimensionCategory>>() {
			public ObservableValue<DimensionCategory> call(CellDataFeatures<DataObject, DimensionCategory> param) {
				if (param.getValue().getValue() instanceof DimensionHierarchy && !(param.getValue() instanceof HeaderItem)) {
					return ((DimensionHierarchy)param.getValue().getValue()).getCategoryProperty();
				} else {
					return new SimpleObjectProperty<DimensionCategory>(null);
				}
		    }
		});
	}

	private void setupDimensionCombinationsTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				if (param.getValue().getValue() instanceof DimensionCombination) {
					return param.getValue().getValue().getNameProperty();
				} else {
					return new SimpleStringProperty("Combination Component");
				}
			}
		});
		
		TreeTableColumn<DataObject, Dimension> colComponent = createDimComponentCol();
		colComponent.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Dimension>, ObservableValue<Dimension>>() {
			public ObservableValue<Dimension> call(CellDataFeatures<DataObject, Dimension> param) {
				if (param.getValue().getValue() instanceof DimensionCombination) {
					return new SimpleObjectProperty<Dimension>(null);
				} else {
					return new SimpleObjectProperty<Dimension>((Dimension)param.getValue().getValue());
				}
			}
		});
	}
	
	private void setupReferenceObjectsTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return param.getValue().getValue().getNameProperty();
		    }
		});
		
		TreeTableColumn<DataObject, Dimension> colDimension = createDimensionCol();
		colDimension.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Dimension>, ObservableValue<Dimension>>() {
			public ObservableValue<Dimension> call(CellDataFeatures<DataObject, Dimension> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<Dimension>(null);
				}
				return ((ReferenceObject)param.getValue().getValue()).getDimensionProperty();
		    }
		});
	}
	
	private void setupReferenceObjectHierarchiesTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				if (param.getValue().getValue() instanceof ReferenceObjectHierarchy || param.getValue() instanceof HeaderItem) {
					return param.getValue().getValue().getNameProperty();
				} else {
					return new SimpleStringProperty("Hierarchy Level");
				}
			}
		});
		
		TreeTableColumn<DataObject, ReferenceObject> colLevel = createRefObjLevelCol();
		colLevel.setCellValueFactory(new Callback<CellDataFeatures<DataObject, ReferenceObject>, ObservableValue<ReferenceObject>>() {
			public ObservableValue<ReferenceObject> call(CellDataFeatures<DataObject, ReferenceObject> param) {
				if (param.getValue().getValue() instanceof ReferenceObjectHierarchy || param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<ReferenceObject>(null);
				} else {
					return new SimpleObjectProperty<ReferenceObject>((ReferenceObject)param.getValue().getValue());
				}
			}
		});
		
		TreeTableColumn<DataObject, DimensionCategory> colCategory = createDimCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, DimensionCategory>, ObservableValue<DimensionCategory>>() {
			public ObservableValue<DimensionCategory> call(CellDataFeatures<DataObject, DimensionCategory> param) {
				if (param.getValue().getValue() instanceof ReferenceObjectHierarchy && !(param.getValue() instanceof HeaderItem)) {
					return ((ReferenceObjectHierarchy)param.getValue().getValue()).getCategoryProperty();
				} else {
					return new SimpleObjectProperty<DimensionCategory>(null);
				}
		    }
		});
	}
	
	private void setupReferenceObjectCombinationsTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				if (param.getValue() instanceof HeaderItem || param.getValue().getValue() instanceof ReferenceObjectCombination) {
					return param.getValue().getValue().getNameProperty();
				} else {
					return new SimpleStringProperty("Combination Component");
				}
			}
		});
		
		TreeTableColumn<DataObject, ReferenceObject> colComponent = createRefObjComponentCol();
		colComponent.setCellValueFactory(new Callback<CellDataFeatures<DataObject, ReferenceObject>, ObservableValue<ReferenceObject>>() {
			public ObservableValue<ReferenceObject> call(CellDataFeatures<DataObject, ReferenceObject> param) {
				if (param.getValue() instanceof HeaderItem || param.getValue().getValue() instanceof ReferenceObjectCombination) {
					return new SimpleObjectProperty<ReferenceObject>(null);
				} else {
					return new SimpleObjectProperty<ReferenceObject>((ReferenceObject)param.getValue().getValue());
				}
			}
		});
		
		TreeTableColumn<DataObject, Dimension> colDimension = createRefObjCombinationDimCol();
		colDimension.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Dimension>, ObservableValue<Dimension>>() {
			public ObservableValue<Dimension> call(CellDataFeatures<DataObject, Dimension> param) {
				if (param.getValue().getValue() instanceof ReferenceObjectCombination) {
					return ((ReferenceObjectCombination)param.getValue().getValue()).getCombination().getDimensionProperty();
				} else {
					return new SimpleObjectProperty<Dimension>(null);
				}
		    }
		});
	}
	
	private void setupRatiosTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return param.getValue().getValue().getNameProperty();
		    }
		});
		
		TreeTableColumn<DataObject, RatioCategory> colCategory = createRatioCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, RatioCategory>, ObservableValue<RatioCategory>>() {
			public ObservableValue<RatioCategory> call(CellDataFeatures<DataObject, RatioCategory> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<RatioCategory>(null);
				}
				return ((Ratio)param.getValue().getValue()).getCategoryProperty();
		    }
		});		
	}
	
	private void setupFactsTable() {
		TreeTableColumn<DataObject, Ratio> colRatio = createRatioCol();
		colRatio.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Ratio>, ObservableValue<Ratio>>() {
			public ObservableValue<Ratio> call(CellDataFeatures<DataObject, Ratio> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<Ratio>(new Ratio(-1, param.getValue().getValue().getName(), -1));
				}
				return ((Fact)param.getValue().getValue()).getRatioProperty();
		    }
		});		
		
		TreeTableColumn<DataObject, ReferenceObject> colRefObj = createReferenceObjectCol();
		colRefObj.setCellValueFactory(new Callback<CellDataFeatures<DataObject, ReferenceObject>, ObservableValue<ReferenceObject>>() {
			public ObservableValue<ReferenceObject> call(CellDataFeatures<DataObject, ReferenceObject> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<ReferenceObject>(null);
				}
				return ((Fact)param.getValue().getValue()).getReferenceObjectProperty();
		    }
		});	
		
		TreeTableColumn<DataObject, Double> colValue = createValueCol();
		colValue.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Double>, ObservableValue<Double>>() {
			public ObservableValue<Double> call(CellDataFeatures<DataObject, Double> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<Double>(null);
				}
				
				return new SimpleObjectProperty<Double>(((Fact)param.getValue().getValue()).getValue());
		    }
		});	
		
		TreeTableColumn<DataObject, Unit> colUnit = createUnitCol();
		colUnit.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Unit>, ObservableValue<Unit>>() {
			public ObservableValue<Unit> call(CellDataFeatures<DataObject, Unit> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<Unit>(null);
				}
				return ((Fact)param.getValue().getValue()).getUnitProperty();
		    }
		});	
	}
	
	private void setupDimCategoriesTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return param.getValue().getValue().getNameProperty();
		    }
		});
	}
	
	private void setupRatioCategoriesTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return param.getValue().getValue().getNameProperty();
		    }
		});
	}
	
	private void setupUnitsTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return param.getValue().getValue().getNameProperty();
		    }
		});
		
		TreeTableColumn<DataObject, String> colSymbol = createSymbolCol();
		colSymbol.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleStringProperty(null);
				}
				return ((Unit)param.getValue().getValue()).getSymbolProperty();
		    }
		});		
	}
	

	private TreeTableColumn<DataObject, String> createNameCol() {
		TreeTableColumn<DataObject, String> colName = new TreeTableColumn<>("Name");
		colName.setPrefWidth(235);
		
		colName.setCellFactory(new Callback<TreeTableColumn<DataObject, String>,TreeTableCell<DataObject, String>>() {
			@Override
			public TreeTableCell<DataObject, String> call(TreeTableColumn<DataObject, String> param) {
				return new DataObjectTFTreeTableCell(new DefaultStringConverter());
			}
		});
		
		colName.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, String>>() {
            @Override
            public void handle(CellEditEvent<DataObject, String> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	String newName = event.getNewValue();
                	
                	if (newName != obj.getNameProperty().get()) {
                    	obj.setNameProperty(newName);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(obj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(event.getRowValue());
                }
            }
        });
		
		editingView.getColumns().add(colName);
		
		return colName;
	}
	
	private TreeTableColumn<DataObject, DimensionCategory> createDimCategoryCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);

		TreeTableColumn<DataObject, DimensionCategory> colCategory = new TreeTableColumn<>("Category");
		colCategory.setPrefWidth(235);
		
		colCategory.setCellFactory(new Callback<TreeTableColumn<DataObject, DimensionCategory>,TreeTableCell<DataObject, DimensionCategory>>() {
			@Override
			public TreeTableCell<DataObject, DimensionCategory> call(TreeTableColumn<DataObject, DimensionCategory> param) {
				ObservableList<DimensionCategory> cats = FXCollections.observableArrayList(dimManager.getCategories().values());
				DimensionCategory noCat = DimensionCategory.NO_DIMENSION_CATEGORY;
				cats.add(noCat);
				
				return new DataObjectCBTreeTableCell<DimensionCategory>(cats);
			}
		});
		
		colCategory.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, DimensionCategory>>() {
            @Override
            public void handle(CellEditEvent<DataObject, DimensionCategory> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> ti = event.getRowValue();
                	Dimension dim = (Dimension)ti.getValue();
                	DimensionCategory newCategory = event.getNewValue();
                	
                	if (newCategory != dim.getCategoryProperty().get()) {
                    	dim.setCategoryProperty(newCategory);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(dim);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	if (currEditingViewType == EditingViewType.DIMENSIONS_BY_CATEGORY) {
                		changeParent(ti, newCategory.getId());
                	}
                	
                	
                	focus(ti);
                }
            }
        });
		
		editingView.getColumns().add(colCategory);
		
		return colCategory;
	}
	
	private TreeTableColumn<DataObject, Dimension> createDimLevelCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);

		TreeTableColumn<DataObject, Dimension> colLevel = new TreeTableColumn<>("Level Dimension");
		colLevel.setPrefWidth(235);
		
		colLevel.setCellFactory(new Callback<TreeTableColumn<DataObject, Dimension>,TreeTableCell<DataObject, Dimension>>() {
			@Override
			public TreeTableCell<DataObject, Dimension> call(TreeTableColumn<DataObject, Dimension> param) {
				ObservableList<Dimension> dims = FXCollections.observableArrayList();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						dims.add(dim);
					}
				}
				return new DataObjectCBTreeTableCell<Dimension>(dims);
			}
		});
		
		colLevel.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Dimension>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Dimension> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> tiDimHierarchy = getHierarchyTreeItem(event.getRowValue());
                	DimensionHierarchy dimHierarchy = (DimensionHierarchy)tiDimHierarchy.getValue();
                	Dimension newLevel = event.getNewValue();
                	Dimension oldLevel = event.getOldValue();
                	
                	if (!dimHierarchy.getLevelsProperty().get().contains(newLevel)) {
                    	if (dimHierarchy.getLevelsProperty().get().contains(oldLevel)) {
                    		int oldIndex = dimHierarchy.getLevelsProperty().get().indexOf(oldLevel);
                    		dimHierarchy.getLevelsProperty().get().add(oldIndex, newLevel);
                    		dimHierarchy.getLevelsProperty().get().remove(oldLevel);
                    	} else {
                    		dimHierarchy.getLevelsProperty().get().add(newLevel);
                    	}
                    	
                    	if (dimHierarchy.getLevelsProperty().get().indexOf(newLevel) == 0) {
                    		dimHierarchy.setCategoryProperty(newLevel.getCategoryProperty().get());
                    	}
                    	
                    	dimHierarchy.setNameProperty(dimHierarchy.generateName(dimHierarchy.getLevelsProperty().get()));
                    	
                    	event.getRowValue().setValue(newLevel);
                    	
                    	if (currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES_BY_CATEGORY && newLevel.getCategoryId() != oldLevel.getCategoryId()) {
                    		changeParent(tiDimHierarchy, newLevel.getCategoryId());
                    	}
                    	
                    	Main.getContext().getBean(EditorController.class).stageUpdate(dimHierarchy);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(event.getRowValue());
                }
            }
        });
		
		editingView.getColumns().add(colLevel);
		
		return colLevel;
	}
	
	private TreeTableColumn<DataObject, Dimension> createDimComponentCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);

		TreeTableColumn<DataObject, Dimension> colComponent = new TreeTableColumn<>("Component Dimension");
		colComponent.setPrefWidth(235);
		
		colComponent.setCellFactory(new Callback<TreeTableColumn<DataObject, Dimension>,TreeTableCell<DataObject, Dimension>>() {
			@Override
			public TreeTableCell<DataObject, Dimension> call(TreeTableColumn<DataObject, Dimension> param) {
				ObservableList<Dimension> dims = FXCollections.observableArrayList();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						dims.add(dim);
					}
				}
				return new DataObjectCBTreeTableCell<Dimension>(dims);
			}
		});
		
		colComponent.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Dimension>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Dimension> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> tiComponent = event.getRowValue();
                	TreeItem<DataObject> tiCombination = tiComponent.getParent();
                	DimensionCombination combination = (DimensionCombination)tiCombination.getValue();
                	Dimension newComponent = event.getNewValue();
                	Dimension oldComponent = event.getOldValue();
                	
                	if (!combination.getComponentsProperty().get().contains(newComponent)) {
                    	int oldIndex = combination.getComponentsProperty().get().indexOf(oldComponent);
                    	combination.getComponentsProperty().get().add(oldIndex, newComponent);
                    	combination.getComponentsProperty().get().remove(oldComponent);
                    	
                    	combination.setNameProperty(combination.generateName(combination.getComponentsProperty().get()));
                    	
                    	event.getRowValue().setValue(newComponent);
                    	
                    	Main.getContext().getBean(EditorController.class).stageUpdate(combination);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(tiComponent);
                }
            }
        });

		editingView.getColumns().add(colComponent);
		
		return colComponent;
	}
	
	private TreeTableColumn<DataObject, Dimension> createDimensionCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		
		TreeTableColumn<DataObject, Dimension> colDim = new TreeTableColumn<>("Dimension");
		colDim.setPrefWidth(235);
		
		colDim.setCellFactory(new Callback<TreeTableColumn<DataObject, Dimension>,TreeTableCell<DataObject, Dimension>>() {
			@Override
			public TreeTableCell<DataObject, Dimension> call(TreeTableColumn<DataObject, Dimension> param) {
				ObservableList<Dimension> dims = FXCollections.observableArrayList();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						dims.add(dim);
					}
				}
				Dimension noDim = Dimension.SELECT_DIMENSION;
				dims.add(noDim);
				return new DataObjectCBTreeTableCell<Dimension>(dims);
			}
		});
		
		colDim.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Dimension>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Dimension> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> ti = event.getRowValue();
                	ReferenceObject refObj = (ReferenceObject)ti.getValue();
                	Dimension newDimension = event.getNewValue();
                	
                	if (newDimension != refObj.getDimensionProperty().get()) {
                    	refObj.setDimensionProperty(newDimension);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(refObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	if (currEditingViewType == EditingViewType.REFERENCE_OBJECTS_BY_DIMENSION) {
                		changeParent(ti, newDimension.getId());
                	}
                	
                	focus(ti);
                }
            }
        });
		
		editingView.getColumns().add(colDim);
		
		return colDim;
	}
	
	private TreeTableColumn<DataObject, ReferenceObject> createRefObjLevelCol() {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);

		TreeTableColumn<DataObject, ReferenceObject> colLevel = new TreeTableColumn<>("Level Reference Object");
		colLevel.setPrefWidth(235);
		
		colLevel.setCellFactory(new Callback<TreeTableColumn<DataObject, ReferenceObject>,TreeTableCell<DataObject, ReferenceObject>>() {
			@Override
			public TreeTableCell<DataObject, ReferenceObject> call(TreeTableColumn<DataObject, ReferenceObject> param) {
				ObservableList<ReferenceObject> refObjs = FXCollections.observableArrayList();
				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					refObjs.add(refObj);
				}
				return new DataObjectCBTreeTableCell<ReferenceObject>(refObjs);
			}
		});
		
		colLevel.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, ReferenceObject>>() {
            @Override
            public void handle(CellEditEvent<DataObject, ReferenceObject> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> tiRefObjHierarchy = getHierarchyTreeItem(event.getRowValue());
                	ReferenceObjectHierarchy refObjHierarchy = (ReferenceObjectHierarchy)tiRefObjHierarchy.getValue();
                	ReferenceObject newLevel = event.getNewValue();
                	ReferenceObject oldLevel = event.getOldValue();
                	
                	if (!refObjHierarchy.getLevelsProperty().get().contains(newLevel)) {
                    	if (refObjHierarchy.getLevelsProperty().get().contains(oldLevel)) {
                    		int oldIndex = refObjHierarchy.getLevelsProperty().get().indexOf(oldLevel);
                    		refObjHierarchy.getLevelsProperty().get().add(oldIndex, newLevel);
                    		refObjHierarchy.getLevelsProperty().get().remove(oldLevel);
                		} else {
                    		refObjHierarchy.getLevelsProperty().get().add(newLevel);
                    	}
                    	
                    	refObjHierarchy.setNameProperty(refObjHierarchy.generateName(refObjHierarchy.getLevelsProperty().get()));
                    	
                    	if (refObjHierarchy.getLevelsProperty().get().indexOf(newLevel) == 0) {
                    		refObjHierarchy.setCategoryProperty(newLevel.getDimensionProperty().get().getCategoryProperty().get());
                    	}
                    	
                    	event.getRowValue().setValue(newLevel);
                    	
                    	if (currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES_BY_CATEGORY && newLevel.getDimensionId() != oldLevel.getDimensionId()) {
                    		changeParent(tiRefObjHierarchy, newLevel.getDimensionId());
                    	}
                    	
                    	Main.getContext().getBean(EditorController.class).stageUpdate(refObjHierarchy);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(event.getRowValue());
                }
            }
        });
		
		editingView.getColumns().add(colLevel);
		
		return colLevel;
	}
	
	private TreeTableColumn<DataObject, ReferenceObject> createRefObjComponentCol() {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);

		TreeTableColumn<DataObject, ReferenceObject> colComponent = new TreeTableColumn<>("Component Reference Object");
		colComponent.setPrefWidth(235);
		
		colComponent.setCellFactory(new Callback<TreeTableColumn<DataObject, ReferenceObject>,TreeTableCell<DataObject, ReferenceObject>>() {
			@Override
			public TreeTableCell<DataObject, ReferenceObject> call(TreeTableColumn<DataObject, ReferenceObject> param) {
				ObservableList<ReferenceObject> refObjs = FXCollections.observableArrayList();
				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					if (!refObj.isCombination()) {
						refObjs.add(refObj);
					}
				}
				return new DataObjectCBTreeTableCell<ReferenceObject>(refObjs);
			}
		});
		
		colComponent.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, ReferenceObject>>() {
            @Override
            public void handle(CellEditEvent<DataObject, ReferenceObject> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> tiComponent = event.getRowValue();
                	TreeItem<DataObject> tiCombination = tiComponent.getParent();
                	ReferenceObjectCombination combination = (ReferenceObjectCombination)tiCombination.getValue();
                	ReferenceObject newComponent = event.getNewValue();
                	ReferenceObject oldComponent = event.getOldValue();
                	
                	if (!combination.getComponentsProperty().get().contains(newComponent)) {
                    	int oldIndex = combination.getComponentsProperty().get().indexOf(oldComponent);
                    	combination.getComponentsProperty().get().add(oldIndex, newComponent);
                    	combination.getComponentsProperty().get().remove(oldComponent);
                    	
                    	combination.setNameProperty(combination.generateName(combination.getComponentsProperty().get()));
                    	
                    	event.getRowValue().setValue(newComponent);
                    	
                    	Main.getContext().getBean(EditorController.class).stageUpdate(combination);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(tiComponent);
                }
            }
        });

		editingView.getColumns().add(colComponent);
		
		return colComponent;
	}
	
	private TreeTableColumn<DataObject, Dimension> createRefObjCombinationDimCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		
		TreeTableColumn<DataObject, Dimension> colDim = new TreeTableColumn<>("Combination Dimensions");
		colDim.setPrefWidth(235);
		
		colDim.setCellFactory(new Callback<TreeTableColumn<DataObject, Dimension>,TreeTableCell<DataObject, Dimension>>() {
			@Override
			public TreeTableCell<DataObject, Dimension> call(TreeTableColumn<DataObject, Dimension> param) {
				ObservableList<Dimension> dims = FXCollections.observableArrayList();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (dim.isCombination()) {
						dims.add(dim);
					}
				}
				Dimension noDim = Dimension.SELECT_DIMENSION;
				dims.add(noDim);
				return new DataObjectCBTreeTableCell<Dimension>(dims);
			}
		});
		
		colDim.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Dimension>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Dimension> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> tiCombination = event.getRowValue();
                	ReferenceObjectCombination combination = (ReferenceObjectCombination)tiCombination.getValue();
                	Dimension newDimension = event.getNewValue();
                	
                	if (newDimension != combination.getCombination().getDimensionProperty().get()) {
                		combination.getCombination().setDimensionProperty(newDimension);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(combination);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	if (currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS_BY_DIMENSION) {
                		changeParent(tiCombination, newDimension.getId());
                	}
                	
                	focus(tiCombination);
                }
            }
        });
		
		editingView.getColumns().add(colDim);
		
		return colDim;
	}
	
	private TreeTableColumn<DataObject, RatioCategory> createRatioCategoryCol() {
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);

		TreeTableColumn<DataObject, RatioCategory> colCategory = new TreeTableColumn<>("Category");
		colCategory.setPrefWidth(235);
		
		colCategory.setCellFactory(new Callback<TreeTableColumn<DataObject, RatioCategory>,TreeTableCell<DataObject, RatioCategory>>() {
			@Override
			public TreeTableCell<DataObject, RatioCategory> call(TreeTableColumn<DataObject, RatioCategory> param) {
				ObservableList<RatioCategory> cats = FXCollections.observableArrayList(ratioManager.getCategories().values());
				RatioCategory noCat = RatioCategory.NO_RATIO_CATEGORY;
				cats.add(noCat);
				
				return new DataObjectCBTreeTableCell<RatioCategory>(cats);
			}
		});
		
		colCategory.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, RatioCategory>>() {
            @Override
            public void handle(CellEditEvent<DataObject, RatioCategory> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> ti = event.getRowValue();
                	Ratio ratio = (Ratio)ti.getValue();
                	RatioCategory newCategory = event.getNewValue();
                	
                	if (newCategory != ratio.getCategoryProperty().get()) {
                    	ratio.setCategoryProperty(newCategory); 
                    	Main.getContext().getBean(EditorController.class).stageUpdate(ratio);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	if (currEditingViewType == EditingViewType.RATIOS_BY_CATEGORY) {
                		changeParent(ti, newCategory.getId());
                	}
                	
                	focus(ti);
                }
            }
        });
		
		editingView.getColumns().add(colCategory);
		
		return colCategory;
	}
	
	private TreeTableColumn<DataObject, Ratio> createRatioCol() {
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);

		TreeTableColumn<DataObject, Ratio> colRatio = new TreeTableColumn<>("Ratio");
		colRatio.setPrefWidth(235);
		
		colRatio.setCellFactory(new Callback<TreeTableColumn<DataObject, Ratio>,TreeTableCell<DataObject, Ratio>>() {
			@Override
			public TreeTableCell<DataObject, Ratio> call(TreeTableColumn<DataObject, Ratio> param) {
				ObservableList<Ratio> ratios = FXCollections.observableArrayList(ratioManager.getRatios().values());
				return new DataObjectCBTreeTableCell<Ratio>(ratios);
			}
		});
		
		colRatio.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Ratio>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Ratio> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> ti = event.getRowValue();
                	Fact fact = (Fact)ti.getValue();
                	Ratio newRatio = event.getNewValue();
                	
                	if (newRatio != fact.getRatioProperty().get()) {
                    	fact.setRatioProperty(newRatio);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(fact);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	if (currEditingViewType == EditingViewType.FACTS_BY_RATIO) {
                		changeParent(ti, newRatio.getId());
                	}
                	
                	focus(ti);
                }
            }
        });
		
		editingView.getColumns().add(colRatio);
		
		return colRatio;
	}
	
	private TreeTableColumn<DataObject, ReferenceObject> createReferenceObjectCol() {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);

		TreeTableColumn<DataObject, ReferenceObject> colRefObj = new TreeTableColumn<>("Reference Object");
		colRefObj.setPrefWidth(235);
		
		colRefObj.setCellFactory(new Callback<TreeTableColumn<DataObject, ReferenceObject>,TreeTableCell<DataObject, ReferenceObject>>() {
			@Override
			public TreeTableCell<DataObject, ReferenceObject> call(TreeTableColumn<DataObject, ReferenceObject> param) {
				ObservableList<ReferenceObject> refObjs = FXCollections.observableArrayList(refObjManager.getReferenceObjects().values());
				return new DataObjectCBTreeTableCell<ReferenceObject>(refObjs);
			}
		});
		
		colRefObj.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, ReferenceObject>>() {
            @Override
            public void handle(CellEditEvent<DataObject, ReferenceObject> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	TreeItem<DataObject> ti = event.getRowValue();
                	Fact fact = (Fact)ti.getValue();
                	ReferenceObject newRefObj = event.getNewValue();
                	
                	if (newRefObj != fact.getReferenceObjectProperty().get()) {
                    	fact.setReferenceObjectProperty(newRefObj);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(fact);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	if (currEditingViewType == EditingViewType.FACTS_BY_REFERENCE_OBJECT) {
                		changeParent(ti, newRefObj.getId());
                	}
                	
                	focus(ti);
                }
            }
        });
		
		editingView.getColumns().add(colRefObj);
		
		return colRefObj;
	}
	
	private TreeTableColumn<DataObject, Double> createValueCol() {
		TreeTableColumn<DataObject, Double> colValue = new TreeTableColumn<>("Value");
		colValue.setPrefWidth(235);
		
		colValue.setCellFactory(new Callback<TreeTableColumn<DataObject, Double>,TreeTableCell<DataObject, Double>>() {
			@Override
			public TreeTableCell<DataObject, Double> call(TreeTableColumn<DataObject, Double> param) {
				return new TextFieldTreeTableCell<DataObject, Double>(new DoubleStringConverter());
			}
		});
		
		colValue.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Double>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Double> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	Fact fact = (Fact)event.getRowValue().getValue();
                	Double newValue = event.getNewValue();
                	
                	if (newValue != fact.getValueProperty().get()) {
                    	fact.setValueProperty(newValue);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(fact);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(event.getRowValue());
                }
            }
        });
		
		editingView.getColumns().add(colValue);
		
		return colValue;
	}
	
	private TreeTableColumn<DataObject, Unit> createUnitCol() {
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		
		TreeTableColumn<DataObject, Unit> colUnit = new TreeTableColumn<>("Unit");
		colUnit.setPrefWidth(235);
		
		colUnit.setCellFactory(new Callback<TreeTableColumn<DataObject, Unit>,TreeTableCell<DataObject, Unit>>() {
			@Override
			public TreeTableCell<DataObject, Unit> call(TreeTableColumn<DataObject, Unit> param) {
				ObservableList<Unit> units = FXCollections.observableArrayList(unitManager.getUnits().values());
				units.add(Unit.NO_UNIT);
				return new DataObjectCBTreeTableCell<Unit>(units);
			}
		});
		
		colUnit.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Unit>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Unit> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	Fact fact = (Fact)event.getRowValue().getValue();
                	Unit newUnit = event.getNewValue();
                	
                	if (newUnit != fact.getUnitProperty().get()) {
                    	fact.setUnitProperty(newUnit);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(fact);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(event.getRowValue());
                }
            }
        });
		
		editingView.getColumns().add(colUnit);
		
		return colUnit;
	}
	
	private TreeTableColumn<DataObject, String> createSymbolCol() {
		TreeTableColumn<DataObject, String> colSymbol = new TreeTableColumn<>("Symbol");
		colSymbol.setPrefWidth(235);
		
		colSymbol.setCellFactory(new Callback<TreeTableColumn<DataObject, String>,TreeTableCell<DataObject, String>>() {
			@Override
			public TreeTableCell<DataObject, String> call(TreeTableColumn<DataObject, String> param) {
				return new DataObjectTFTreeTableCell(new DefaultStringConverter());
			}
		});
		
		colSymbol.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, String>>() {
            @Override
            public void handle(CellEditEvent<DataObject, String> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	Unit unit = (Unit)event.getRowValue().getValue();
                	String newSymbol = event.getNewValue();
                	
                	if (newSymbol != unit.getSymbolProperty().get()) {
                    	unit.setSymbolProperty(newSymbol);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(unit);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                	
                	focus(event.getRowValue());
                }
            }
        });
		
		editingView.getColumns().add(colSymbol);
		
		return colSymbol;
	}
		
		
	public void createObject() {
		DataObject newObj = Main.getContext().getBean(EditorController.class).createObject(currEditingViewType.objectClass);
		newObj.initProperties();
		DataObjectTreeItem tiNewObj = new DataObjectTreeItem(newObj);
		editingView.getRoot().getChildren().add(tiNewObj);
		
		if (currEditingViewType == EditingViewType.DIMENSIONS_BY_CATEGORY) {
			changeParent(tiNewObj, ((Dimension)newObj).getCategoryId());
		} else if (currEditingViewType == EditingViewType.REFERENCE_OBJECTS_BY_DIMENSION) {
			changeParent(tiNewObj, ((ReferenceObject)newObj).getDimensionId());
		} else if (currEditingViewType == EditingViewType.RATIOS_BY_CATEGORY) {
			changeParent(tiNewObj, ((Ratio)newObj).getCategoryId());
		}
		
    	Main.getContext().getBean(EditorController.class).stageCreation(newObj);		
    	hasUnsavedChanges.set(true);
    	
    	focus(tiNewObj);
	}
 
	public void deleteObject(TreeItem<DataObject> tiObj) {
		removeFromParent(tiObj);
		
    	Main.getContext().getBean(EditorController.class).stageDeletion(tiObj.getValue());		
    	hasUnsavedChanges.set(true);
    	editingView.requestFocus();
	}
	
	
	public void addChild(TreeItem<DataObject> tiCurrChild) {
		DataObject newChild = null;
		if (currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES
				|| currEditingViewType == EditingViewType.DIMENSION_HIERARCHIES_BY_CATEGORY) {
			
			newChild = Main.getContext().getBean(EditorController.class).createObject(Dimension.class);
		} else if (currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES
				|| currEditingViewType == EditingViewType.REFERENCE_OBJECT_HIERARCHIES_BY_CATEGORY) {
			
			newChild = Main.getContext().getBean(EditorController.class).createObject(ReferenceObject.class);
		}
		newChild.initProperties();
		
		DataObjectTreeItem tiNewLevel = new DataObjectTreeItem(newChild);
		@SuppressWarnings("unchecked")
		DataObjectHierarchy<DataObject> hierarchy = (DataObjectHierarchy<DataObject>)getHierarchyTreeItem(tiCurrChild).getValue();
		if (!tiCurrChild.getChildren().isEmpty()) {
			TreeItem<DataObject> tiOldChild = tiCurrChild.getChildren().remove(0);
			tiNewLevel.getChildren().add(tiOldChild);
			tiNewLevel.setExpanded(true);
    		hierarchy.getLevelsProperty().get().add(hierarchy.getLevelsProperty().get().indexOf(tiOldChild.getValue()), newChild);
		} else {
	    	hierarchy.getLevelsProperty().get().add(newChild);
		}
		tiCurrChild.getChildren().add(tiNewLevel);
		tiCurrChild.setExpanded(true);
		
    	hierarchy.setNameProperty(hierarchy.generateName(hierarchy.getLevelsProperty().get()));
		
    	if (hierarchy.getLevelsProperty().get().indexOf(newChild) == 0) {
			if (newChild instanceof Dimension) {
				hierarchy.setCategoryProperty(((Dimension)newChild).getCategoryProperty().get());
			} else if (newChild instanceof ReferenceObject) {
				hierarchy.setCategoryProperty(((ReferenceObject)newChild).getDimensionProperty().get().getCategoryProperty().get());
			}
    	}
    	
    	Main.getContext().getBean(EditorController.class).stageUpdate(hierarchy);		
    	hasUnsavedChanges.set(true);
		
    	focus(tiNewLevel);
	}
	
	public void removeChild(TreeItem<DataObject> tiCurrChild) {
		@SuppressWarnings("unchecked")
		DataObjectHierarchy<DataObject> hierarchy = (DataObjectHierarchy<DataObject>)getHierarchyTreeItem(tiCurrChild).getValue();
		if (!tiCurrChild.getChildren().isEmpty()) {
			TreeItem<DataObject> tiChild = tiCurrChild.getChildren().get(0);
			tiCurrChild.getParent().getChildren().add(tiChild);
		}
		DataObject currChild = tiCurrChild.getValue();
		boolean wasTop = hierarchy.getLevelsProperty().get().get(0) == currChild;
		tiCurrChild.getParent().getChildren().remove(tiCurrChild);
		hierarchy.getLevelsProperty().get().remove(currChild);
		
    	hierarchy.setNameProperty(hierarchy.generateName(hierarchy.getLevelsProperty().get()));
    	
    	if (wasTop) {
    		DataObject newTopLevel = hierarchy.getLevelsProperty().get().get(0);
			if (newTopLevel instanceof Dimension) {
				hierarchy.setCategoryProperty(((Dimension)newTopLevel).getCategoryProperty().get());
			} else if (newTopLevel instanceof ReferenceObject) {
				hierarchy.setCategoryProperty(Main.getContext().getBean(DimensionManager.class)
						.getDimensions().get(((ReferenceObject)newTopLevel).getDimensionId()).getCategoryProperty().get());
			}
    	}
				
    	Main.getContext().getBean(EditorController.class).stageUpdate(hierarchy);		
    	hasUnsavedChanges.set(true);
    	editingView.requestFocus();
	}
	
	
	public void addComponent(TreeItem<DataObject> tiCurrComponent) {
		DataObject newComponent = null;
		if (currEditingViewType == EditingViewType.DIMENSION_COMBINATIONS) {
			newComponent = Main.getContext().getBean(EditorController.class).createObject(Dimension.class);
		} else if (currEditingViewType == EditingViewType.REFERENCE_OBJECT_COMBINATIONS) {
			newComponent = Main.getContext().getBean(EditorController.class).createObject(ReferenceObject.class);
		}
		newComponent.initProperties();
		
		TreeItem<DataObject> tiCombination = null;
		if (tiCurrComponent.getValue() instanceof DataObjectCombination) {
			tiCombination = tiCurrComponent;
		} else {
			tiCombination = tiCurrComponent.getParent();
		}
		 
		@SuppressWarnings("unchecked")
		DataObjectCombination<DataObject> combination = (DataObjectCombination<DataObject>)tiCombination.getValue();
		
		DataObjectTreeItem tiNewComponent = new DataObjectTreeItem(newComponent);
		tiCombination.getChildren().add(tiNewComponent);
		tiCombination.setExpanded(true);
		
		combination.getComponentsProperty().get().add(newComponent);
		combination.setNameProperty(combination.generateName(combination.getComponentsProperty().get()));
		
    	Main.getContext().getBean(EditorController.class).stageUpdate(combination);		
    	hasUnsavedChanges.set(true);
    	
    	focus(tiNewComponent);
	}

	public void removeComponent(TreeItem<DataObject> tiCurrComponent) {
		TreeItem<DataObject> tiCombination = tiCurrComponent.getParent();
		@SuppressWarnings("unchecked")
		DataObjectCombination<DataObject> combination = (DataObjectCombination<DataObject>)tiCombination.getValue();
	
		tiCombination.getChildren().remove(tiCurrComponent);
		
		DataObject currComponent = tiCurrComponent.getValue();
		combination.getComponentsProperty().get().remove(currComponent);
		combination.setNameProperty(combination.generateName(combination.getComponentsProperty().get()));
		
    	Main.getContext().getBean(EditorController.class).stageUpdate(combination);		
    	hasUnsavedChanges.set(true);
    	editingView.requestFocus();
	}
	
	
	public void expandAll(TreeItem<DataObject> root) {
		expandCollapseAll(root, true);
	}
	
	public void collapseAll(TreeItem<DataObject> root) {
		expandCollapseAll(root, false);
	}
	
	private void expandCollapseAll(TreeItem<DataObject> root, boolean expand) {
		LinkedList<TreeItem<DataObject>> queue = new LinkedList<>();
		queue.push(root);
		while (!queue.isEmpty()) {
			TreeItem<DataObject> currNode = queue.pop();
			currNode.setExpanded(expand);
			queue.addAll(currNode.getChildren());
		}
	}
	
	
	private TreeItem<DataObject> getHierarchyTreeItem(TreeItem<DataObject> tiCurrLevel) {
    	boolean found = false;
    	TreeItem<DataObject> currParent = tiCurrLevel;
    	while (!found) {
    		if (currParent.getValue() instanceof DataObjectHierarchy) {
    			found = true;
    		} else {
    			currParent = currParent.getParent();
    		}
    	}
    	
    	return currParent;
	}
	
	private void changeParent(TreeItem<DataObject> ti, long id) {
		removeFromParent(ti);
		addToParent(ti, tiHeaderMap.get(id));
		
		focus(ti);
	}
	
	private void removeFromParent(TreeItem<DataObject> ti) {
		TreeItem<DataObject> parent = ti.getParent();
		ti.getParent().getChildren().remove(ti);
		if (parent.getChildren().isEmpty()) {
			parent.getParent().getChildren().remove(parent);
		}
	}
	
	private void addToParent(TreeItem<DataObject> ti, DataObjectTreeItem newParent) {
		if (!editingView.getRoot().getChildren().contains(newParent)) {
			editingView.getRoot().getChildren().add(newParent);
		}
    	newParent.getChildren().add(ti);
	}
	
	
	public void showEditingView() {
		editingView.setVisible(true);
		searchBoxController.showSearchBox();
		
		editingView.requestFocus();
	}

	public void hideEditingView() {
		editingView.setVisible(false);
		searchBoxController.hideSearchBox();
	}
	
	
	private void resetEditingView() {
		currEditingViewType = null;
		
		editingView.setPlaceholder(new Text(""));
		editingView.getColumns().clear();
		editingView.setRoot(null);
		editingView.setShowRoot(false);
		editingView.setEditable(true);
		
		hasUnsavedChanges.set(false);
		
		searchBoxController.resetSearchBox(true);
	}
	
	private void focus(TreeItem<DataObject> ti) {
		int row = editingView.getRow(ti);
		editingView.getSelectionModel().select(row);
		editingView.getFocusModel().focus(row);
		editingView.scrollTo(row);
	}
}