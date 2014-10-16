package genericdwh.dataobjects;

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
import genericdwh.dataobjects.ratio.RatioRelation;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectCombination;
import genericdwh.dataobjects.referenceobject.ReferenceObjectHierarchy;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.gui.general.ValidationMessages;
import genericdwh.main.Main;

import java.util.ArrayList;
import java.util.List;


public class ChangeManager {

	private DimensionManager dimManager;
	private ReferenceObjectManager refObjManager;
	private RatioManager ratioManager;
	private UnitManager unitManager;
	private FactManager factManager;
	
	private List<DataObject> stagedObjects = new ArrayList<>();
	
	private Class<?> stagedObjectClass = null;
	
	public ChangeManager(DimensionManager dimManager, ReferenceObjectManager refObjManager, RatioManager ratioManager, UnitManager unitManager, FactManager factManager) {
		this.dimManager = dimManager;
		this.refObjManager = refObjManager;
		this.ratioManager = ratioManager;
		this.unitManager = unitManager;
		this.factManager = factManager;
	}
	
	public void stageCreation(DataObject obj) {
		stageObject(obj);
		obj.setMarkedForCreation(true);
	}
	
	public void stageUpdate(DataObject obj) {
		stageObject(obj);
		obj.setMarkedForUpdate(true);
	}
	
	public void stageDeletion(DataObject obj) {
		stageObject(obj);
		obj.setMarkedForDeletion(true);
	}
	
	public boolean saveChanges() {
		boolean changesSaved = false;
		if (stagedObjectClass == Dimension.class) {
			changesSaved = dimManager.saveDimensions(stagedObjects);
		} else if (stagedObjectClass == DimensionHierarchy.class) {
			changesSaved = dimManager.saveHierarchies(stagedObjects);
		}  else if (stagedObjectClass == DimensionCombination.class) {
			changesSaved = dimManager.saveCombinations(stagedObjects);
		} else if (stagedObjectClass == ReferenceObject.class) {
			changesSaved = refObjManager.saveReferenceObjects(stagedObjects);
		} else if (stagedObjectClass == ReferenceObjectHierarchy.class) {
			changesSaved = refObjManager.saveHierarchies(stagedObjects);
		}  else if (stagedObjectClass == ReferenceObjectCombination.class) {
			changesSaved = refObjManager.saveCombinations(stagedObjects);
		} else if (stagedObjectClass == Ratio.class) {
			changesSaved = ratioManager.saveRatios(stagedObjects);
		} else if (stagedObjectClass == RatioRelation.class) {
			changesSaved = ratioManager.saveRelations(stagedObjects);
		} else if (stagedObjectClass == Fact.class) {
			changesSaved = factManager.saveFacts(stagedObjects);
		} else if (stagedObjectClass == DimensionCategory.class) {
			changesSaved = dimManager.saveCategories(stagedObjects);
		} else if (stagedObjectClass == RatioCategory.class) {
			changesSaved = ratioManager.saveCategories(stagedObjects);
		} else if (stagedObjectClass == Unit.class) {
			changesSaved = unitManager.saveUnits(stagedObjects);
		}
		
		
		stagedObjects.clear();
		
		if (!changesSaved) {
			Main.getLogger().info("Not all changes saved. Check log for further information.");
			return false;
		}

		Main.getLogger().info("Changes saved.");
		return true;
	}
		
	public void discardChanges() {
		for (DataObject obj : stagedObjects) {
			obj.initProperties();
			obj.setMarkedForCreation(false);
			obj.setMarkedForUpdate(false);
			obj.setMarkedForDeletion(false);
		}
		
		stagedObjects.clear();
		stagedObjectClass = null;
		Main.getLogger().info("Changes discarded.");
	}
	
	@SuppressWarnings("unchecked")
	public String validateChanges() {
		if (stagedObjectClass == ReferenceObject.class) {
			for (DataObject refObj : stagedObjects) {
				if (((ReferenceObject)refObj).getDimensionProperty().get().getId() == -1) {
					Main.getLogger().info("Validation failed: " + ValidationMessages.REFERENCE_OBJECT_NO_DIMENSION);
					return ValidationMessages.REFERENCE_OBJECT_NO_DIMENSION;
				}
			}
		} else if (stagedObjectClass == DimensionHierarchy.class || stagedObjectClass == ReferenceObjectHierarchy.class || stagedObjectClass == RatioRelation.class) {
			for (DataObject hierarchy : stagedObjects) {
				if (((DataObjectHierarchy<DataObject>)hierarchy).getLevelsProperty().get().size() < 2) {
					Main.getLogger().info("Validation failed: " + ValidationMessages.HIERARCHY_RELATION__MIN_2_OBJECTS);
					return ValidationMessages.HIERARCHY_RELATION__MIN_2_OBJECTS;
				}
				List<DataObject> containedLevels = new ArrayList<DataObject>();
				for (DataObject lvl : ((DataObjectHierarchy<DataObject>)hierarchy).getLevelsProperty().get()) {
					if (containedLevels.contains(lvl)) {
						Main.getLogger().info("Validation failed: " + ValidationMessages.HIERARCHY_RELATION__DUPLICATE_LEVEL);
						return ValidationMessages.HIERARCHY_RELATION__DUPLICATE_LEVEL;
					}
					containedLevels.add(lvl);
				}
			}
		} else if (stagedObjectClass == DimensionCombination.class || stagedObjectClass == ReferenceObjectCombination.class) {
			for (DataObject combination : stagedObjects) {
				if (((DataObjectCombination<DataObject>)combination).getComponentsProperty().get().size() < 2) {
					Main.getLogger().info("Validation failed: " + ValidationMessages.COMBINATION_MIN_2_OBJECTS);
					return ValidationMessages.COMBINATION_MIN_2_OBJECTS;
				}
				List<DataObject> containedComponents = new ArrayList<DataObject>();
				for (DataObject comp : ((DataObjectCombination<DataObject>)combination).getComponentsProperty().get()) {
					if (containedComponents.contains(comp)) {
						Main.getLogger().info("Validation failed: " + ValidationMessages.COMBINATION_DUPLICATE_COMPONENT);
						return ValidationMessages.COMBINATION_DUPLICATE_COMPONENT;
					}
					containedComponents.add(comp);
				}
			}
		} else if (stagedObjectClass == Fact.class) {
			for (DataObject fact : stagedObjects) {
				if (((Fact)fact).getRatioProperty().get().getId() == -1) {
					Main.getLogger().info("Validation failed: " + ValidationMessages.FACT_NO_RATIO);
					return ValidationMessages.FACT_NO_RATIO;
				} else if (((Fact)fact).getReferenceObjectProperty().get().getId() == -1) {
					Main.getLogger().info("Validation failed: " + ValidationMessages.FACT_NO_REFERENCE_OBJECT);
					return ValidationMessages.FACT_NO_REFERENCE_OBJECT;
				}
			}
		}
		Main.getLogger().info("Validation successful.");
		return null;
	}
	
	private void stageObject(DataObject obj) {
		if (!stagedObjects.contains(obj)) {
			stagedObjects.add(obj);
			if (stagedObjectClass == null) {
				stagedObjectClass = obj.getClass();
			}
		}
	}
}
