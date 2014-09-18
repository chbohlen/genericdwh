package genericdwh.gui.subwindows.editor.editingview;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableColumn.CellEditEvent;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.text.Text;
import javafx.stage.WindowEvent;
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
	
	private EditingViewType currEditingViewType;
	
	private List<TreeItem<DataObject>> changedObjects = new ArrayList<>(); 
	
	public enum EditingViewType {
		DIMENSIONS(Dimension.class),
		DIMENSIONS_BY_CATEGORY(Dimension.class),
		REFERENCE_OBJECTS(ReferenceObject.class),
		REFERENCE_OBJECTS_BY_DIMENSION(ReferenceObject.class),
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
				treeTableRow.setContextMenu(createContextMenu(treeTableRow));
				return treeTableRow;
			}
		});
	}
	
	private ContextMenu createContextMenu(TreeTableRow<DataObject> treeTableRow) {
		ContextMenu contextMenu = new ContextMenu();
		
		MenuItem expand = new MenuItem("Expand");
		expand.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingView.getTreeItem(treeTableRow.getIndex()).setExpanded(true);
            }
        });
		
		MenuItem expandAll = new MenuItem("Expand All");
		expandAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				for (TreeItem<DataObject> ti : editingView.getRoot().getChildren()) {
					ti.setExpanded(true);
				}
            }
        });
		
		MenuItem collapse = new MenuItem("Collapse");
		collapse.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				editingView.getTreeItem(treeTableRow.getIndex()).setExpanded(false);
            }
        });
		
		MenuItem collapseAll = new MenuItem("Collapse All");
		collapseAll.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				for (TreeItem<DataObject> ti : editingView.getRoot().getChildren()) {
					ti.setExpanded(false);
				}
            }
        });
		
		MenuItem duplicate = new MenuItem("Duplicate Object");
		duplicate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				Main.getContext().getBean(EditorController.class).duplicateObject(treeTableRow.getTreeItem().getValue());
            }
        });
		
		MenuItem add = new MenuItem("Add Object");
		add.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				addObject();
            }
        });
		
		MenuItem delete = new MenuItem("Delete Object");
		delete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				Main.getContext().getBean(EditorController.class).confirmDeletion(treeTableRow.getTreeItem());
            }
        });
		
		contextMenu.getItems().addAll(expand, collapse, expandAll, collapseAll, add, duplicate, delete);
		
		contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				if (editingView.getTreeItem(treeTableRow.getIndex()) instanceof HeaderItem) {
					if (editingView.getTreeItem(treeTableRow.getIndex()).isExpanded()) {
						expand.setVisible(false);
						collapse.setVisible(true);
					} else {
						expand.setVisible(true);
						collapse.setVisible(false);
					}
					expandAll.setVisible(true);
					collapseAll.setVisible(true);
					add.setVisible(false);
					duplicate.setVisible(false);
					delete.setVisible(false);
				} else if (treeTableRow.isEmpty()) {
					expand.setVisible(false);
					collapse.setVisible(false);
					expandAll.setVisible(false);
					collapseAll.setVisible(false);
					add.setVisible(true);
					duplicate.setVisible(false);
					delete.setVisible(false);
				} else {
					expand.setVisible(false);
					collapse.setVisible(false);
					expandAll.setVisible(false);
					collapseAll.setVisible(false);
					add.setVisible(true);
					duplicate.setVisible(true);
					delete.setVisible(true);
				}
			}
		});
				
		return contextMenu;
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
				
		currEditingViewType = EditingViewType.values()[id];
		switch (currEditingViewType) {
			case DIMENSIONS: {
				setupDimensionsTable();
				
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						dim.initProperties();
						DataObjectTreeItem tiDim = new DataObjectTreeItem(dim);
						tiRoot.addChild(tiDim);
					}
				}
								
				break;
			}
			case DIMENSIONS_BY_CATEGORY: {
				setupDimensionsTable();
				
				tiHeaderMap = new TreeMap<>();
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), cat.getId(), true, true);
					tiHeaderMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", 0, true, true);
				tiHeaderMap.put((long)0, tiNoCat);

				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						dim.initProperties();
						DataObjectTreeItem tiDim = new DataObjectTreeItem(dim);
						tiHeaderMap.get(dim.getCategoryId()).addChild(tiDim);
					}
				}
				
				for (DataObjectTreeItem tiCat : tiHeaderMap.values()) {
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
						refObj.initProperties();
						DataObjectTreeItem tiRefObj = new DataObjectTreeItem(refObj);
						tiRoot.addChild(tiRefObj);
					}
				}
								
				break;
			}
			case REFERENCE_OBJECTS_BY_DIMENSION: {
				setupReferenceObjectsTable();
				
				tiHeaderMap = new TreeMap<>();
				for (Dimension dim : dimManager.getDimensions().values()) {
					if (!dim.isCombination()) {
						HeaderItem tiDim = new HeaderItem(dim.getName(), dim.getId(), true, true);
						tiHeaderMap.put(dim.getId(), tiDim);
					}
				}
				
				for (ReferenceObject refObj : refObjManager.loadRefObjs().values()) {
					DataObjectTreeItem tiDim = tiHeaderMap.get(refObj.getDimensionId());
					if (tiDim != null) {
						refObj.initProperties();
						DataObjectTreeItem tiRefObj = new DataObjectTreeItem(refObj);
						tiDim.addChild(tiRefObj);
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
				
				for (Ratio ratio : ratioManager.getRatios().values()) {
					ratio.initProperties();
					DataObjectTreeItem tiRatio = new DataObjectTreeItem(ratio);
					tiRoot.addChild(tiRatio);
				}
								
				break;
			}
			case RATIOS_BY_CATEGORY: {
				setupRatiosTable();
				
				tiHeaderMap = new TreeMap<>();
				for (RatioCategory cat : ratioManager.getCategories().values()) {
					HeaderItem tiCat = new HeaderItem(cat.getName(), cat.getId(), true, true);
					tiHeaderMap.put(cat.getId(), tiCat);
				}
				HeaderItem tiNoCat = new HeaderItem("Uncategorized", 0, true, true);
				tiHeaderMap.put((long)0, tiNoCat);


				for (Ratio ratio : ratioManager.getRatios().values()) {
					ratio.initProperties();
					DataObjectTreeItem tiRatio = new DataObjectTreeItem(ratio);
					tiHeaderMap.get(ratio.getCategoryId()).addChild(tiRatio);
				}
				
				for (DataObjectTreeItem tiCat : tiHeaderMap.values()) {
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
					fact.initProperties();
					DataObjectTreeItem tiFact = new DataObjectTreeItem(fact);
					tiRoot.addChild(tiFact);
				}
				
				break;
			}
			case FACTS_BY_RATIO: {
				setupFactsTable();
				
				refObjManager.loadRefObjs();

				tiHeaderMap = new TreeMap<>();
				for (Ratio ratio : ratioManager.getRatios().values()) {
					HeaderItem tiRatio = new HeaderItem(ratio.getName(), ratio.getId(), true, true);
					tiHeaderMap.put(ratio.getId(), tiRatio);
				}
				
				for (Fact fact : factManager.loadFacts()) {
					fact.initProperties();
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
				
				refObjManager.loadRefObjs();
				
				tiHeaderMap = new TreeMap<>();
				for (ReferenceObject refObj : refObjManager.getReferenceObjects().values()) {
					HeaderItem tiRefObj = new HeaderItem(refObj.getName(), refObj.getId(), true, true);
					tiHeaderMap.put(refObj.getId(), tiRefObj);
				}
				
				for (Fact fact : factManager.loadFacts()) {
					fact.initProperties();
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
				
				for (DimensionCategory cat : dimManager.getCategories().values()) {
					cat.initProperties();
					DataObjectTreeItem tiDimCat = new DataObjectTreeItem(cat);
					tiRoot.addChild(tiDimCat);
				}
				
				break;
			}
			case RATIO_CATEGORIES: {
				setupRatioCategoriesTable();
				
				for (RatioCategory cat : ratioManager.getCategories().values()) {
					cat.initProperties();
					DataObjectTreeItem tiRatioCat = new DataObjectTreeItem(cat);
					tiRoot.addChild(tiRatioCat);
				}
				
				break;
			}
			case UNITS: {
				setupUnitsTable();
				
				for (Unit unit : unitManager.getUnits().values()) {
					unit.initProperties();
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
		colName.setPrefWidth(175);
		
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
				
				return new DataObjectCBTreeTableCell<DimensionCategory>(cats);
			}
		});
		
		colCategory.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, DimensionCategory>>() {
            @Override
            public void handle(CellEditEvent<DataObject, DimensionCategory> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	Dimension dim = (Dimension)event.getRowValue().getValue();
                	DimensionCategory newCategory = event.getNewValue();
                	
                	if (newCategory != dim.getCategoryProperty().get()) {
                    	dim.setCategoryProperty(newCategory);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(dim);
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
				
				return new DataObjectCBTreeTableCell<RatioCategory>(cats);
			}
		});
		
		colCategory.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, RatioCategory>>() {
            @Override
            public void handle(CellEditEvent<DataObject, RatioCategory> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	Ratio ratio = (Ratio)event.getRowValue().getValue();
                	RatioCategory newCategory = event.getNewValue();
                	
                	if (newCategory != ratio.getCategoryProperty().get()) {
                    	ratio.setCategoryProperty(newCategory); 
                    	Main.getContext().getBean(EditorController.class).stageUpdate(ratio);
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
				return new DataObjectCBTreeTableCell<Dimension>(dims);
			}
		});
		
		colDim.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Dimension>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Dimension> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	ReferenceObject refObj = (ReferenceObject)event.getRowValue().getValue();
                	Dimension newDimension = event.getNewValue();
                	
                	if (newDimension != refObj.getDimensionProperty().get()) {
                    	refObj.setDimensionProperty(newDimension);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(refObj);
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

		TreeTableColumn<DataObject, Ratio> colRatio = new TreeTableColumn<>("Ratio");
		colRatio.setPrefWidth(175);
		
		colRatio.setCellFactory(new Callback<TreeTableColumn<DataObject, Ratio>,TreeTableCell<DataObject, Ratio>>() {
			@Override
			public TreeTableCell<DataObject, Ratio> call(TreeTableColumn<DataObject, Ratio> param) {
				ObservableList<Ratio> ratios = FXCollections.observableArrayList(ratioManager.getRatios().values());
				Ratio newRatio = new Ratio(-2, "New Ratio", -1);
				ratios.add(newRatio);
				return new DataObjectCBTreeTableCell<Ratio>(ratios);
			}
		});
		
		colRatio.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, Ratio>>() {
            @Override
            public void handle(CellEditEvent<DataObject, Ratio> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	Fact fact = (Fact)event.getRowValue().getValue();
                	Ratio newRatio = event.getNewValue();
                	
                	if (newRatio != fact.getRatioProperty().get()) {
                    	fact.setRatioProperty(newRatio);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(fact);
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

		TreeTableColumn<DataObject, ReferenceObject> colRefObj = new TreeTableColumn<>("Reference Object");
		colRefObj.setPrefWidth(175);
		
		colRefObj.setCellFactory(new Callback<TreeTableColumn<DataObject, ReferenceObject>,TreeTableCell<DataObject, ReferenceObject>>() {
			@Override
			public TreeTableCell<DataObject, ReferenceObject> call(TreeTableColumn<DataObject, ReferenceObject> param) {
				ObservableList<ReferenceObject> refObjs = FXCollections.observableArrayList(refObjManager.getReferenceObjects().values());
				ReferenceObject newRefObj = new ReferenceObject(-1, -1, "New Reference Object");
				refObjs.add(newRefObj);
				return new DataObjectCBTreeTableCell<ReferenceObject>(refObjs);
			}
		});
		
		colRefObj.setOnEditCommit(new EventHandler<CellEditEvent<DataObject, ReferenceObject>>() {
            @Override
            public void handle(CellEditEvent<DataObject, ReferenceObject> event) {
                if (event.getEventType() == TreeTableColumn.editCommitEvent()) {
                	Fact fact = (Fact)event.getRowValue().getValue();
                	ReferenceObject newRefObj = event.getNewValue();
                	
                	if (newRefObj != fact.getReferenceObjectProperty().get()) {
                    	fact.setReferenceObjectProperty(newRefObj);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(fact);
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
                	Fact fact = (Fact)event.getRowValue().getValue();
                	Double newValue = event.getNewValue();
                	
                	if (newValue != fact.getValueProperty().get()) {
                    	fact.setValueProperty(newValue);
                    	Main.getContext().getBean(EditorController.class).stageUpdate(fact);
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
		
		TreeTableColumn<DataObject, Unit> colUnit = new TreeTableColumn<>("Unit");
		colUnit.setPrefWidth(175);
		
		colUnit.setCellFactory(new Callback<TreeTableColumn<DataObject, Unit>,TreeTableCell<DataObject, Unit>>() {
			@Override
			public TreeTableCell<DataObject, Unit> call(TreeTableColumn<DataObject, Unit> param) {
				ObservableList<Unit> units = FXCollections.observableArrayList(unitManager.getUnits().values());
				Unit newUnit = new Unit(-2, "New Unit", "");
				units.add(newUnit);
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
                }
            }
        });
		
		editingView.getColumns().add(colUnit);
		
		return colUnit;
	}
	
	private TreeTableColumn<DataObject, String> createSymbolCol() {
		TreeTableColumn<DataObject, String> colSymbol = new TreeTableColumn<>("Symbol");
		colSymbol.setPrefWidth(175);
		
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
                }
            }
        });
		
		editingView.getColumns().add(colSymbol);
		
		return colSymbol;
	}

	
	private void resetEditingView() {
		changedObjects.clear();
		hasUnsavedChanges.set(false);
		
		editingView.setPlaceholder(new Text(""));
		editingView.getColumns().clear();
		editingView.setRoot(null);
		editingView.setShowRoot(false);
		editingView.setEditable(true);
		
		searchBoxController.resetSearchBox(true);
	}
	
	private void addObject() {
		DataObject newObj = Main.getContext().getBean(EditorController.class).addObject(currEditingViewType.objectClass);
		DataObjectTreeItem newTiObj = new DataObjectTreeItem(newObj);
		editingView.getRoot().getChildren().add(newTiObj);
		newObj.initProperties();
		
		changedObjects.add(newTiObj);
		
    	hasUnsavedChanges.set(true);
    	editingView.requestFocus();
    	
		int row = editingView.getRow(newTiObj);
		editingView.getSelectionModel().select(row);
		editingView.getFocusModel().focus(row);
		editingView.scrollTo(row);
	}

	public void deleteObject(TreeItem<DataObject> tiObj) {
		TreeItem<DataObject> parent = tiObj.getParent();
		parent.getChildren().remove(tiObj);
		if (parent.getChildren().isEmpty()) {
			parent.getParent().getChildren().remove(parent);
		}
		
		changedObjects.add(tiObj);
		
    	hasUnsavedChanges.set(true);
    	editingView.requestFocus();
	}
	
	public void saveChanges() {
		
	}
	
	public void discardChanges() {
		for (TreeItem<DataObject> tiObj : changedObjects) {
			DataObject obj = tiObj.getValue();
			if (obj.isMarkedForCreation()) {
				TreeItem<DataObject> parent = tiObj.getParent();
				parent.getChildren().remove(tiObj);
				if (parent.getChildren().isEmpty()) {
					parent.getParent().getChildren().remove(parent);
				}
			} else if (obj.isMarkedForDeletion()) {
				long headerId = 0;
				if (obj instanceof Dimension) {
					headerId = ((Dimension)obj).getCategoryId();
				} else if (obj instanceof ReferenceObject) {
					headerId = ((ReferenceObject)obj).getDimensionId();
				} else if (obj instanceof Ratio) {
					headerId = ((Ratio)obj).getCategoryId();
				} else if (obj instanceof Fact) {
					if (currEditingViewType == EditingViewType.FACTS_BY_RATIO) {
						headerId = ((Fact)obj).getRatioId();
					} else if (currEditingViewType == EditingViewType.FACTS_BY_REFERENCE_OBJECT) {
						headerId = ((Fact)obj).getRatioId();
					}
				}
				TreeItem<DataObject> tiParent;
				if (tiHeaderMap != null) {
					tiParent = tiHeaderMap.get(headerId);
					if (!editingView.getRoot().getChildren().contains(tiParent)) {
						editingView.getRoot().getChildren().add(tiParent);
					}
				} else {
					tiParent = editingView.getRoot();
				}
				tiParent.getChildren().add(tiObj);
			}
		}
		changedObjects.clear();
		hasUnsavedChanges.set(false);		
	}
}
