package genericdwh.gui.subwindows.editor.editingview;

import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.gui.general.sidebar.DataObjectTreeItem;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.main.Main;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;

public class EditingViewController {

	@FXML private TreeTableView<DataObject> editingView;
	
	private boolean hasUnsavedChanges = false;
	
	public enum EditingViewType {
		DIMENSIONS,
		DIMENSIONS_BY_CATEGORY,
		REFERENCE_OBJECTS,
		REFERENCE_OBJECTS_BY_DIMENSION,
		REFERENCE_OBJECTS_BY_CATEGORY,
		RATIOS,
		RATIOS_BY_CATEGORY,
		FACTS,
		FACTS_BY_RATIO,
		FACTS_BY_REFERENCE_OBJECT,
		DIMENSION_CATEGORIES,
		RATIO_CATEGORIES,
		UNITS;
	}
	
	public EditingViewController() {
	}
	
	public void show() {
		editingView.setVisible(true);
	}
	
	public void hide() {
		editingView.setVisible(false);
	}

	public void createEditorTreeTable(int id) {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		editingView.setPlaceholder(new Text(""));
		editingView.getColumns().clear();
		editingView.setRoot(null);
		editingView.setShowRoot(false);
		editingView.setEditable(true);
		
		EditingViewType editingViewType = EditingViewType.values()[id];
		switch (editingViewType) {
			case DIMENSIONS: {
				setupDimensionsTable();
				
				HeaderItem tiRoot = new HeaderItem("", -1, true, false);
				editingView.setRoot(tiRoot);
				
				for (Dimension dim : dimManager.getDimensions().values()) {
					DataObjectTreeItem tiDim = new DataObjectTreeItem(dim);
					if (!dim.isCombination()) {
						tiRoot.addChild(tiDim);
					}
				}
				
				break;
			}
			case DIMENSIONS_BY_CATEGORY: {
				setupDimensionsTable();
				
				HeaderItem tiRoot = new HeaderItem("", -1, true, false);
				editingView.setRoot(tiRoot);
				
				TreeMap<Long, DataObjectTreeItem> tiCatMap = new TreeMap<>();
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), -1, true, true);
					tiCatMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", -1, true, true);

				for (Dimension dim : dimManager.getDimensions().values()) {
					DataObjectTreeItem tiDim = new DataObjectTreeItem(dim);
					if (!dim.isCombination()) {
						if (dim.getCategoryId() == 0) {
							tiNoCat.addChild(tiDim);
						} else {
							tiCatMap.get(dim.getCategoryId()).addChild(tiDim);
						}
					}
				}
				
				for (DataObjectTreeItem tiCat : tiCatMap.values()) {
					if (tiCat.hasChildren()) {
						tiRoot.addChild(tiCat);
					}
				}
				if (tiNoCat.hasChildren()) {
					tiRoot.addChild(tiNoCat);
				}
				
				break;
			}
			case REFERENCE_OBJECTS: {
				setupReferenceObjectsTable();
				
				HeaderItem tiRoot = new HeaderItem("", -1, true, false);
				editingView.setRoot(tiRoot);
				
				for (ReferenceObject refObj : refObjManager.loadRefObjsRange(0, 999).values()) {
					if (!dimManager.getDimension(refObj.getDimensionId()).isCombination()) {
						tiRoot.addChild(new DataObjectTreeItem(refObj));
					}
				}
				
				break;
			}
			case REFERENCE_OBJECTS_BY_DIMENSION: {
				setupReferenceObjectsTable();
				
				HeaderItem tiRoot = new HeaderItem("", -1, true, false);
				editingView.setRoot(tiRoot);
				
				TreeMap<Long, DataObjectTreeItem> tiDimMap = new TreeMap<>();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						HeaderItem tiDim = new HeaderItem(dim.getName(), -1, true, true);
						tiDimMap.put(dim.getId(), tiDim);
					}
				}
				
				for (ReferenceObject refObj : refObjManager.loadRefObjsRange(0, 999).values()) {
					DataObjectTreeItem tiDim = tiDimMap.get(refObj.getDimensionId());
					if (tiDim != null) {
						tiDim.addChild(new DataObjectTreeItem(refObj));
					}
				}
				
				for (DataObjectTreeItem tiDim : tiDimMap.values()) {
					if (tiDim.hasChildren()) {
						tiRoot.addChild(tiDim);
					}
				}
				
				break;
			}
			case REFERENCE_OBJECTS_BY_CATEGORY: {
				setupReferenceObjectsTable();
				
				HeaderItem tiRoot = new HeaderItem("", -1, true, false);
				editingView.setRoot(tiRoot);
				
				TreeMap<Long, DataObjectTreeItem> tiCatMap = new TreeMap<>();
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), -1, true, true);
					tiCatMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", -1, true, true);

				for (ReferenceObject refObj : refObjManager.loadRefObjsRange(0, 999).values()) {
					DataObjectTreeItem tiRefObj = new DataObjectTreeItem(refObj);
					
					Dimension dim = dimManager.getDimension(refObj.getDimensionId());
					if (!dim.isCombination()) {
						if (dim.getCategoryId() == 0) {
							tiNoCat.addChild(tiRefObj);
						} else {
							tiCatMap.get(dim.getCategoryId()).addChild(tiRefObj);
						}
					}
				}
				
				for (DataObjectTreeItem tiCat : tiCatMap.values()) {
					if (tiCat.hasChildren()) {
						tiRoot.addChild(tiCat);
					}
				}
				if (tiNoCat.hasChildren()) {
					tiRoot.addChild(tiNoCat);
				}
				
				break;
			}
			case RATIOS: {
				setupRatiosTable();
				break;
			}
			case RATIOS_BY_CATEGORY: {
				setupRatiosTable();
				break;
			}
			case FACTS: {
				setupFactsTable();
				break;
			}
			case FACTS_BY_RATIO: {
				setupFactsTable();
				break;				
			}
			case FACTS_BY_REFERENCE_OBJECT: {
				setupFactsTable();
				break;				
			}
			case DIMENSION_CATEGORIES: {
				break;
			}
			case RATIO_CATEGORIES: {
				break;
			}
			case UNITS: {
				break;
			}
		}
		
		show();
	}
	

	private void setupDimensionsTable() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
		    }
		});
		
		TreeTableColumn<DataObject, DimensionCategory> colCategory = createCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, DimensionCategory>, ObservableValue<DimensionCategory>>() {
			public ObservableValue<DimensionCategory> call(CellDataFeatures<DataObject, DimensionCategory> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<DimensionCategory>(null);
				}
				
				Dimension dim = dimManager.getDimension(param.getValue().getValue().getId());
				long catId = dim.getCategoryId();
				
				if (catId == 0) {
					return new SimpleObjectProperty<DimensionCategory>(new DimensionCategory(-1, "Uncategorized"));
				}

				return new SimpleObjectProperty<DimensionCategory>(dimManager.getCategories().get(catId));
		    }
		});
	}
	
	private void setupReferenceObjectsTable() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);

		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
		    }
		});
		
		TreeTableColumn<DataObject, Dimension> colDimension = createDimensionCol();
		colDimension.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Dimension>, ObservableValue<Dimension>>() {
			public ObservableValue<Dimension> call(CellDataFeatures<DataObject, Dimension> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<Dimension>(null);
				}
				ReferenceObject refObj = refObjManager.getReferenceObject(param.getValue().getValue().getId());
				return new SimpleObjectProperty<Dimension>(dimManager.getDimension(refObj.getDimensionId()));
		    }
		});
		
		TreeTableColumn<DataObject, DimensionCategory> colCategory = createCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, DimensionCategory>, ObservableValue<DimensionCategory>>() {
			public ObservableValue<DimensionCategory> call(CellDataFeatures<DataObject, DimensionCategory> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<DimensionCategory>(null);
				}
				
				ReferenceObject refObj = refObjManager.getReferenceObject(param.getValue().getValue().getId());
				Dimension dim = dimManager.getDimension(refObj.getDimensionId());
				long catId = dim.getCategoryId();
				
				if (catId == 0) {
					return new SimpleObjectProperty<DimensionCategory>(new DimensionCategory(-1, "Uncategorized"));
				}

				return new SimpleObjectProperty<DimensionCategory>(dimManager.getCategories().get(catId));
		    }
		});
	}
	
	private void setupRatiosTable() {
		createNameCol();
		createCategoryCol();		
	}
	
	private void setupFactsTable() {
		createRatioCol();
		createReferenceObjectCol();
		createValueCol();
		createUnitSymbolCol();
	}
	
	
	private TreeTableColumn<DataObject, String> createNameCol() {
		TreeTableColumn<DataObject, String> colName = new TreeTableColumn<>("Name");
		colName.setPrefWidth(175);
		
		colName.setCellFactory(new Callback<TreeTableColumn<DataObject, String>,TreeTableCell<DataObject, String>>() {
			@Override
			public TreeTableCell<DataObject, String> call(TreeTableColumn<DataObject, String> param) {
				return new TextFieldTreeTableCell<DataObject, String>(new DefaultStringConverter());
			}
		});
		
		colName.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, String>>() {
            @Override
            public void handle(CellEditEvent<DataObject, String> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	String newName = event.getNewValue();
                	
                	obj.setName(newName);
                	obj.setHasChanged(true);
                }
            }
        });
		
		editingView.getColumns().add(colName);
		
		return colName;
	}
	
	private TreeTableColumn<DataObject, DimensionCategory> createCategoryCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);

		TreeTableColumn<DataObject, DimensionCategory> colCategory = new TreeTableColumn<>("Category");
		colCategory.setPrefWidth(175);
		
		colCategory.setCellFactory(new Callback<TreeTableColumn<DataObject, DimensionCategory>,TreeTableCell<DataObject, DimensionCategory>>() {
			@Override
			public TreeTableCell<DataObject, DimensionCategory> call(TreeTableColumn<DataObject, DimensionCategory> param) {
				ObservableList<DimensionCategory> cats = FXCollections.observableArrayList(dimManager.getCategories().values());
				DimensionCategory noCat = new DimensionCategory(-1, "Uncategorized");
				cats.add(noCat);
				DimensionCategory newCat = new DimensionCategory(-1, "New Category");
				cats.add(newCat);
				
				return new DataObjectTreeTableCell<DimensionCategory>(cats);
			}
		});
		
		colCategory.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, DimensionCategory>>() {
            @Override
            public void handle(CellEditEvent<DataObject, DimensionCategory> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	DimensionCategory newCategory = event.getNewValue();
                	
                	Dimension dim = dimManager.getDimension(obj.getId());
                	dim.setCategoryId(newCategory.getId());
                	dim.setHasChanged(true);
                }
            }
        });
		
		editingView.getColumns().add(colCategory);
		
		return colCategory;
	}
	
	private TreeTableColumn<DataObject, Dimension> createDimensionCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		TreeTableColumn<DataObject, Dimension> colDim = new TreeTableColumn<>("Dimension");
		colDim.setPrefWidth(175);
		
		colDim.setCellFactory(new Callback<TreeTableColumn<DataObject, Dimension>,TreeTableCell<DataObject, Dimension>>() {
			@Override
			public TreeTableCell<DataObject, Dimension> call(TreeTableColumn<DataObject, Dimension> param) {
				ObservableList<Dimension> dims = FXCollections.observableArrayList();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						dims.add(dim);
					}
				}
				Dimension newDim = new Dimension(-1, "New Dimension", -1);
				dims.add(newDim);
				return new DataObjectTreeTableCell<Dimension>(dims);
			}
		});
		
		colDim.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Dimension>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Dimension> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	Dimension newDimension = event.getNewValue();
                	
                	ReferenceObject refObj = refObjManager.getReferenceObject(obj.getId());
                	refObj.setDimensionId(newDimension.getId());
                	refObj.setHasChanged(true);
                }
            }
        });
		
		editingView.getColumns().add(colDim);
		
		return colDim;
	}
	
	private TreeTableColumn<DataObject, String> createRatioCol() {
		TreeTableColumn<DataObject, String> colRatio = new TreeTableColumn<>("Ratio");
		colRatio.setPrefWidth(175);
		editingView.getColumns().add(colRatio);
		
		return colRatio;
	}
	
	private TreeTableColumn<DataObject, String> createReferenceObjectCol() {
		TreeTableColumn<DataObject, String> colRefObj = new TreeTableColumn<>("Reference Object");
		colRefObj.setPrefWidth(175);
		editingView.getColumns().add(colRefObj);
		
		return colRefObj;
	}
	
	private TreeTableColumn<DataObject, Double> createValueCol() {
		TreeTableColumn<DataObject, Double> colValue = new TreeTableColumn<>("Value");
		colValue.setPrefWidth(175);
		editingView.getColumns().add(colValue);
		
		return colValue;
	}
	
	private TreeTableColumn<DataObject, String> createUnitSymbolCol() {
		TreeTableColumn<DataObject, String> colUnitSymbol = new TreeTableColumn<>("Unit Symbol");
		colUnitSymbol.setPrefWidth(175);
		editingView.getColumns().add(colUnitSymbol);
		
		return colUnitSymbol;
	}
}