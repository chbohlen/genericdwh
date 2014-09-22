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
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectCombination;
import genericdwh.dataobjects.referenceobject.ReferenceObjectHierarchy;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.gui.general.ValidationMessages;

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
	
	public void saveChanges() {
		if (stagedObjectClass == Dimension.class) {
			dimManager.saveDimensions(stagedObjects);
		} else if (stagedObjectClass == DimensionHierarchy.class) {
			dimManager.saveHierarchies(stagedObjects);
		}  else if (stagedObjectClass == DimensionCombination.class) {
			dimManager.saveCombinations(stagedObjects);
		} else if (stagedObjectClass == ReferenceObject.class) {
			refObjManager.saveReferenceObjects(stagedObjects);
		} else if (stagedObjectClass == ReferenceObjectHierarchy.class) {
			refObjManager.saveHierarchies(stagedObjects);
		}  else if (stagedObjectClass == ReferenceObjectCombination.class) {
			refObjManager.saveCombinations(stagedObjects);
		} else if (stagedObjectClass == Ratio.class) {
			ratioManager.saveRatios(stagedObjects);
		} else if (stagedObjectClass == Fact.class) {
			factManager.saveFacts(stagedObjects);
		} else if (stagedObjectClass == DimensionCategory.class) {
			dimManager.saveCategories(stagedObjects);
		} else if (stagedObjectClass == RatioCategory.class) {
			ratioManager.saveCategories(stagedObjects);
		} else if (stagedObjectClass == Unit.class) {
			unitManager.saveUnits(stagedObjects);
		}
		
		stagedObjects.clear();
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
	}
	
	@SuppressWarnings("unchecked")
	public String validateChanges() {
		if (stagedObjectClass == ReferenceObject.class) {
			for (DataObject refObj : stagedObjects) {
				if (((ReferenceObject)refObj).getDimensionProperty().get().getId() == -1) {
					return ValidationMessages.REFERENCE_OBJECT_NO_DIMENSION;
				}
			}
		} else if (stagedObjectClass == DimensionHierarchy.class || stagedObjectClass == ReferenceObjectHierarchy.class) {
			for (DataObject hierarchy : stagedObjects) {
				if (((DataObjectHierarchy<DataObject>)hierarchy).getLevelsProperty().get().size() < 2) {
					return ValidationMessages.HIERARCHY_MIN_2_OBJECTS;
				}
			}
		} else if (stagedObjectClass == DimensionCombination.class || stagedObjectClass == ReferenceObjectCombination.class) {
			for (DataObject combination : stagedObjects) {
				if (((DataObjectCombination<DataObject>)combination).getComponentsProperty().get().size() < 2) {
					return ValidationMessages.COMBINATION_MIN_2_OBJECTS;
				}
			}
		} else if (stagedObjectClass == Fact.class) {
			for (DataObject fact : stagedObjects) {
				if (((Fact)fact).getRatioProperty().get().getId() == -1) {
					return ValidationMessages.FACT_NO_RATIO;
				} else if (((Fact)fact).getReferenceObjectProperty().get().getId() == -1) {
					return ValidationMessages.FACT_NO_REFERENCE_OBJECT;
				}
			}
		}
		
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
