package genericdwh.gui.subwindows.editor.editingview;

import java.util.TreeMap;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.fact.FactManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
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
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.converter.DefaultStringConverter;
import javafx.util.converter.DoubleStringConverter;
import lombok.Getter;

public class EditingViewController {
	
	private SearchBoxController searchBoxController;

	@Getter @FXML private TreeTableView<DataObject> editingView;
	
	@Getter private BooleanProperty hasUnsavedChanges = new SimpleBooleanProperty(false);		
	
	public enum EditingViewType {
		DIMENSIONS,
		DIMENSIONS_BY_CATEGORY,
		REFERENCE_OBJECTS,
		REFERENCE_OBJECTS_BY_DIMENSION,
		RATIOS,
		RATIOS_BY_CATEGORY,
		FACTS,
		FACTS_BY_RATIO,
		FACTS_BY_REFERENCE_OBJECT,
		DIMENSION_CATEGORIES,
		RATIO_CATEGORIES,
		UNITS;
	}
	
	
	public EditingViewController(SearchBoxController searchBoxController) {
		this.searchBoxController = searchBoxController;
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

	
	public void createEditorTreeTable(int id) {
		if (hasUnsavedChanges.get()) {
			Main.getContext().getBean(EditorController.class).showSaveChangesDialog();
		}
		
		resetEditingView();
				
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		FactManager factManager = Main.getContext().getBean(FactManager.class);
		
		HeaderItem tiRoot = new HeaderItem("", -1, true, false);
		editingView.setRoot(tiRoot);
				
		EditingViewType editingViewType = EditingViewType.values()[id];
		switch (editingViewType) {
			case DIMENSIONS: {
				setupDimensionsTable();
				
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
				
				TreeMap<Long, DataObjectTreeItem> tiCatMap = new TreeMap<>();
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), -1, true, true);
					tiCatMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", -1, true, true);

				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						DataObjectTreeItem tiDim = new DataObjectTreeItem(dim);
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
				
				refObjManager.loadRefObjs();

				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					if (!dimManager.getDimension(refObj.getDimensionId()).isCombination()) {
						DataObjectTreeItem tiRefObj = new DataObjectTreeItem(refObj);
						tiRoot.addChild(tiRefObj);
					}
				}
				
				break;
			}
			case REFERENCE_OBJECTS_BY_DIMENSION: {
				setupReferenceObjectsTable();
				
				TreeMap<Long, DataObjectTreeItem> tiDimMap = new TreeMap<>();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						HeaderItem tiDim = new HeaderItem(dim.getName(), -1, true, true);
						tiDimMap.put(dim.getId(), tiDim);
					}
				}
				
				for (ReferenceObject refObj : refObjManager.loadRefObjs().values()) {
					DataObjectTreeItem tiDim = tiDimMap.get(refObj.getDimensionId());
					if (tiDim != null) {
						DataObjectTreeItem tiRefObj = new DataObjectTreeItem(refObj);
						tiDim.addChild(tiRefObj);
					}
				}
				
				for (DataObjectTreeItem tiDim : tiDimMap.values()) {
					if (tiDim.hasChildren()) {
						tiRoot.addChild(tiDim);
					}
				}
				
				break;
			}
			case RATIOS: {
				setupRatiosTable();
				
				for (Ratio ratio : ratioManager.getRatios().values()) {
					DataObjectTreeItem tiRatio = new DataObjectTreeItem(ratio);
					tiRoot.addChild(tiRatio);
				}
				
				break;
			}
			case RATIOS_BY_CATEGORY: {
				setupRatiosTable();
				
				TreeMap<Long, DataObjectTreeItem> tiCatMap = new TreeMap<>();
				for (RatioCategory cat : ratioManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), -1, true, true);
					tiCatMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", -1, true, true);

				for (Ratio ratio : ratioManager.getRatios().values()) {
					DataObjectTreeItem tiRatio = new DataObjectTreeItem(ratio);
					if (ratio.getCategoryId() == 0) {
						tiNoCat.addChild(tiRatio);
					} else {
						tiCatMap.get(ratio.getCategoryId()).addChild(tiRatio);
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
			case FACTS: {
				setupFactsTable();
				
				refObjManager.loadRefObjs();
				
				for (Fact fact : factManager.loadFacts()) {
					DataObjectTreeItem tiFact = new DataObjectTreeItem(fact);
					tiRoot.addChild(tiFact);
				}
				
				break;
			}
			case FACTS_BY_RATIO: {
				setupFactsTable();
				
				refObjManager.loadRefObjs();

				TreeMap<Long, DataObjectTreeItem> tiRatioMap = new TreeMap<>();
				for (Ratio ratio : ratioManager.getRatios().values()) {
					HeaderItem tiRatio = new HeaderItem(ratio.getName(), -1, true, true);
					tiRatioMap.put(ratio.getId(), tiRatio);
				}
				
				for (Fact fact : factManager.loadFacts()) {
					DataObjectTreeItem tiFact = new DataObjectTreeItem(fact);
					tiRatioMap.get(fact.getRatioId()).addChild(tiFact);
				}
				
				for (DataObjectTreeItem tiRatio : tiRatioMap.values()) {
					if (tiRatio.hasChildren()) {
						tiRoot.addChild(tiRatio);
					}
				}
				
				break;				
			}
			case FACTS_BY_REFERENCE_OBJECT: {
				setupFactsTable();
				
				refObjManager.loadRefObjs();
				
				TreeMap<Long, DataObjectTreeItem> tiRefObjMap = new TreeMap<>();
				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					HeaderItem tiRefObj = new HeaderItem(refObj.getName(), -1, true, true);
					tiRefObjMap.put(refObj.getId(), tiRefObj);
				}
				
				for (Fact fact : factManager.loadFacts()) {
					DataObjectTreeItem tiFact = new DataObjectTreeItem(fact);
					tiRefObjMap.get(fact.getReferenceObjectId()).addChild(tiFact);
				}
				
				for (DataObjectTreeItem tiRefObj : tiRefObjMap.values()) {
					if (tiRefObj.hasChildren()) {
						tiRoot.addChild(tiRefObj);
					}
				}
				
				break;				
			}
			case DIMENSION_CATEGORIES: {
				setupDimCategoriesTable();
				
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					DataObjectTreeItem tiDimCat = new DataObjectTreeItem(cat);
					tiRoot.addChild(tiDimCat);
				}
				
				break;
			}
			case RATIO_CATEGORIES: {
				setupRatioCategoriesTable();
				
				for (RatioCategory cat : ratioManager.getCategories().values()) {
					DataObjectTreeItem tiRatioCat = new DataObjectTreeItem(cat);
					tiRoot.addChild(tiRatioCat);
				}
				
				break;
			}
			case UNITS: {
				setupUnitsTable();
				
				for (Unit unit : unitManager.getUnits().values()) {
					DataObjectTreeItem tiUnit = new DataObjectTreeItem(unit);
					tiRoot.addChild(tiUnit);
				}
				
				break;
			}
		}
	}

	
	private void setupDimensionsTable() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
				
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
		    }
		});
		
		TreeTableColumn<DataObject, DimensionCategory> colCategory = createDimCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, DimensionCategory>, ObservableValue<DimensionCategory>>() {
			public ObservableValue<DimensionCategory> call(CellDataFeatures<DataObject, DimensionCategory> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<DimensionCategory>(null);
				}
				
				Dimension dim = dimManager.getDimension(param.getValue().getValue().getId());
				long catId = dim.getCategoryId();
				
				if (catId == 0) {
					return new SimpleObjectProperty<DimensionCategory>(new DimensionCategory(-3, "Uncategorized"));
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
				Dimension dim = dimManager.getDimension(refObj.getDimensionId());
				return new SimpleObjectProperty<Dimension>(dim);
		    }
		});
	}
		
	private void setupRatiosTable() {
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
		
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
		    }
		});
		
		TreeTableColumn<DataObject, RatioCategory> colCategory = createRatioCategoryCol();
		colCategory.setCellValueFactory(new Callback<CellDataFeatures<DataObject, RatioCategory>, ObservableValue<RatioCategory>>() {
			public ObservableValue<RatioCategory> call(CellDataFeatures<DataObject, RatioCategory> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<RatioCategory>(null);
				}
				
				Ratio ratio = ratioManager.getRatio(param.getValue().getValue().getId());
				long catId = ratio.getCategoryId();
				
				if (catId == 0) {
					return new SimpleObjectProperty<RatioCategory>(new RatioCategory(-3, "Uncategorized"));
				}

				return new SimpleObjectProperty<RatioCategory>(ratioManager.getCategories().get(catId));
		    }
		});		
	}
		
	private void setupFactsTable() {
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);

		TreeTableColumn<DataObject, Ratio> colRatio = createRatioCol();
		colRatio.setCellValueFactory(new Callback<CellDataFeatures<DataObject, Ratio>, ObservableValue<Ratio>>() {
			public ObservableValue<Ratio> call(CellDataFeatures<DataObject, Ratio> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<Ratio>(new Ratio(-1, param.getValue().getValue().getName(), -1));
				}
				
				long ratioId = ((Fact)param.getValue().getValue()).getRatioId();

				return new SimpleObjectProperty<Ratio>(ratioManager.getRatio(ratioId));
		    }
		});		
		
		TreeTableColumn<DataObject, ReferenceObject> colRefObj = createReferenceObjectCol();
		colRefObj.setCellValueFactory(new Callback<CellDataFeatures<DataObject, ReferenceObject>, ObservableValue<ReferenceObject>>() {
			public ObservableValue<ReferenceObject> call(CellDataFeatures<DataObject, ReferenceObject> param) {
				if (param.getValue() instanceof HeaderItem) {
					return new SimpleObjectProperty<ReferenceObject>(null);
				}
				
				long refObjId = ((Fact)param.getValue().getValue()).getReferenceObjectId();

				return new SimpleObjectProperty<ReferenceObject>(refObjManager.getReferenceObject(refObjId));
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
				
				long unitId = ((Fact)param.getValue().getValue()).getUnitId();

				return new SimpleObjectProperty<Unit>(unitManager.getUnit(unitId));
		    }
		});	
	}
	
	private void setupDimCategoriesTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
		    }
		});
	}
	
	private void setupRatioCategoriesTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
		    }
		});
	}

	private void setupUnitsTable() {
		TreeTableColumn<DataObject, String> colName = createNameCol();
		colName.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(param.getValue().getValue().getName());
		    }
		});
		
		TreeTableColumn<DataObject, String> colSymbol = createSymbolCol();
		colSymbol.setCellValueFactory(new Callback<CellDataFeatures<DataObject, String>, ObservableValue<String>>() {
			public ObservableValue<String> call(CellDataFeatures<DataObject, String> param) {
				return new SimpleStringProperty(((Unit)param.getValue().getValue()).getSymbol());
		    }
		});		
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
                	
                	if (newName != obj.getName()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeName(obj, newName);
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colName);
		
		return colName;
	}
	
	private TreeTableColumn<DataObject, DimensionCategory> createDimCategoryCol() {
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);

		TreeTableColumn<DataObject, DimensionCategory> colCategory = new TreeTableColumn<>("Category");
		colCategory.setPrefWidth(175);
		
		colCategory.setCellFactory(new Callback<TreeTableColumn<DataObject, DimensionCategory>,TreeTableCell<DataObject, DimensionCategory>>() {
			@Override
			public TreeTableCell<DataObject, DimensionCategory> call(TreeTableColumn<DataObject, DimensionCategory> param) {
				ObservableList<DimensionCategory> cats = FXCollections.observableArrayList(dimManager.getCategories().values());
				DimensionCategory noCat = new DimensionCategory(-3, "Uncategorized");
				cats.add(noCat);
				DimensionCategory newCat = new DimensionCategory(-2, "New Category");
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
                	
                	if (newCategory.getId() != dim.getCategoryId()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeCategory(dim, newCategory.getId());
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colCategory);
		
		return colCategory;
	}
	
	private TreeTableColumn<DataObject, RatioCategory> createRatioCategoryCol() {
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);

		TreeTableColumn<DataObject, RatioCategory> colCategory = new TreeTableColumn<>("Category");
		colCategory.setPrefWidth(175);
		
		colCategory.setCellFactory(new Callback<TreeTableColumn<DataObject, RatioCategory>,TreeTableCell<DataObject, RatioCategory>>() {
			@Override
			public TreeTableCell<DataObject, RatioCategory> call(TreeTableColumn<DataObject, RatioCategory> param) {
				ObservableList<RatioCategory> cats = FXCollections.observableArrayList(ratioManager.getCategories().values());
				RatioCategory noCat = new RatioCategory(-3, "Uncategorized");
				cats.add(noCat);
				RatioCategory newCat = new RatioCategory(-2, "New Category");
				cats.add(newCat);
				
				return new DataObjectTreeTableCell<RatioCategory>(cats);
			}
		});
		
		colCategory.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, RatioCategory>>() {
            @Override
            public void handle(CellEditEvent<DataObject, RatioCategory> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	RatioCategory newCategory = event.getNewValue();
                	Ratio ratio = ratioManager.getRatio(obj.getId());
                	
                	if (newCategory.getId() != ratio.getCategoryId()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeCategory(ratio, newCategory.getId());
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
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
				Dimension newDim = new Dimension(-2, "New Dimension", -1);
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
                	
                	if (newDimension.getId() != refObj.getDimensionId()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeDimension(refObj, newDimension.getId());
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colDim);
		
		return colDim;
	}
	
	private TreeTableColumn<DataObject, Ratio> createRatioCol() {
		RatioManager ratioManager = Main.getContext().getBean(RatioManager.class);
		FactManager factManager = Main.getContext().getBean(FactManager.class);

		TreeTableColumn<DataObject, Ratio> colRatio = new TreeTableColumn<>("Ratio");
		colRatio.setPrefWidth(175);
		
		colRatio.setCellFactory(new Callback<TreeTableColumn<DataObject, Ratio>,TreeTableCell<DataObject, Ratio>>() {
			@Override
			public TreeTableCell<DataObject, Ratio> call(TreeTableColumn<DataObject, Ratio> param) {
				ObservableList<Ratio> ratios = FXCollections.observableArrayList(ratioManager.getRatios().values());
				Ratio newRatio = new Ratio(-2, "New Ratio", -1);
				ratios.add(newRatio);
				return new DataObjectTreeTableCell<Ratio>(ratios);
			}
		});
		
		colRatio.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Ratio>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Ratio> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	Ratio newRatio = event.getNewValue();
                	Fact fact = factManager.getFact(((Fact)obj).getRatioId(), ((Fact)obj).getReferenceObjectId());
                	
                	if (newRatio.getId() != fact.getRatioId()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeRatio(fact, newRatio.getId());
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colRatio);
		
		return colRatio;
	}
	
	private TreeTableColumn<DataObject, ReferenceObject> createReferenceObjectCol() {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		FactManager factManager = Main.getContext().getBean(FactManager.class);

		TreeTableColumn<DataObject, ReferenceObject> colRefObj = new TreeTableColumn<>("Reference Object");
		colRefObj.setPrefWidth(175);
		
		colRefObj.setCellFactory(new Callback<TreeTableColumn<DataObject, ReferenceObject>,TreeTableCell<DataObject, ReferenceObject>>() {
			@Override
			public TreeTableCell<DataObject, ReferenceObject> call(TreeTableColumn<DataObject, ReferenceObject> param) {
				ObservableList<ReferenceObject> refObjs = FXCollections.observableArrayList(refObjManager.getReferenceObjects().values());
				ReferenceObject newRefObj = new ReferenceObject(-1, -1, "New Reference Object");
				refObjs.add(newRefObj);
				return new DataObjectTreeTableCell<ReferenceObject>(refObjs);
			}
		});
		
		colRefObj.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, ReferenceObject>>() {
            @Override
            public void handle(CellEditEvent<DataObject, ReferenceObject> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	ReferenceObject newRefObj = event.getNewValue();
                	Fact fact = factManager.getFact(((Fact)obj).getRatioId(), ((Fact)obj).getReferenceObjectId());
                	
                	if (newRefObj.getId() != fact.getRatioId()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeReferenceObject(fact, newRefObj.getId());
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colRefObj);
		
		return colRefObj;
	}
	
	private TreeTableColumn<DataObject, Double> createValueCol() {
		FactManager factManager = Main.getContext().getBean(FactManager.class);

		TreeTableColumn<DataObject, Double> colValue = new TreeTableColumn<>("Value");
		colValue.setPrefWidth(175);
		
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
                	DataObject obj = event.getRowValue().getValue();
                	Double newValue = event.getNewValue();
                	Fact fact = factManager.getFact(((Fact)obj).getRatioId(), ((Fact)obj).getReferenceObjectId());
                	
                	if (newValue != fact.getValue()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeValue(fact, newValue);
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colValue);
		
		return colValue;
	}
	
	private TreeTableColumn<DataObject, Unit> createUnitCol() {
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);
		FactManager factManager = Main.getContext().getBean(FactManager.class);
		
		TreeTableColumn<DataObject, Unit> colUnit = new TreeTableColumn<>("Unit");
		colUnit.setPrefWidth(175);
		
		colUnit.setCellFactory(new Callback<TreeTableColumn<DataObject, Unit>,TreeTableCell<DataObject, Unit>>() {
			@Override
			public TreeTableCell<DataObject, Unit> call(TreeTableColumn<DataObject, Unit> param) {
				ObservableList<Unit> units = FXCollections.observableArrayList(unitManager.getUnits().values());
				Unit newUnit = new Unit(-2, "New Unit", "");
				units.add(newUnit);
				return new DataObjectTreeTableCell<Unit>(units);
			}
		});
		
		colUnit.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Unit>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Unit> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	Unit newUnit = event.getNewValue();
                	Fact fact = factManager.getFact(((Fact)obj).getRatioId(), ((Fact)obj).getReferenceObjectId());
                	
                	if (newUnit.getId() != fact.getUnitId()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeReferenceObject(fact, newUnit.getId());
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colUnit);
		
		return colUnit;
	}

	private TreeTableColumn<DataObject, String> createSymbolCol() {
		UnitManager unitManager = Main.getContext().getBean(UnitManager.class);

		TreeTableColumn<DataObject, String> colSymbol = new TreeTableColumn<>("Symbol");
		colSymbol.setPrefWidth(175);
		
		colSymbol.setCellFactory(new Callback<TreeTableColumn<DataObject, String>,TreeTableCell<DataObject, String>>() {
			@Override
			public TreeTableCell<DataObject, String> call(TreeTableColumn<DataObject, String> param) {
				return new TextFieldTreeTableCell<DataObject, String>(new DefaultStringConverter());
			}
		});
		
		colSymbol.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, String>>() {
            @Override
            public void handle(CellEditEvent<DataObject, String> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	DataObject obj = event.getRowValue().getValue();
                	String newSymbol = event.getNewValue();
                	Unit unit = unitManager.getUnit(obj.getId());
                	
                	if (newSymbol != unit.getSymbol()) {
                    	DataObject changedObj = Main.getContext().getBean(EditorController.class).changeSymbol(unit, newSymbol);
                    	event.getRowValue().setValue(changedObj);
                    	hasUnsavedChanges.set(true);
                    	editingView.requestFocus();
                	}
                }
            }
        });
		
		editingView.getColumns().add(colSymbol);
		
		return colSymbol;
	}

	
	private void resetEditingView() {
		hasUnsavedChanges.set(false);
		
		editingView.setPlaceholder(new Text(""));
		editingView.getColumns().clear();
		editingView.setRoot(null);
		editingView.setShowRoot(false);
		editingView.setEditable(true);
		
		searchBoxController.resetSearchBox(true);
	}		
}
