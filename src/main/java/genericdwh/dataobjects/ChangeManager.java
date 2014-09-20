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

import java.util.ArrayList;
import java.util.List;


public class ChangeManager {

	private DimensionManager dimManager;
	private ReferenceObjectManager refObjManager;
	private RatioManager ratioManager;
	private UnitManager unitManager;
	private FactManager factManager;
	
	private List<DataObject> stagedObjects = new ArrayList<>();
	
	public ChangeManager(DimensionManager dimManager, ReferenceObjectManager refObjManager, RatioManager ratioManager, UnitManager unitManager, FactManager factManager) {
		this.dimManager = dimManager;
		this.refObjManager = refObjManager;
		this.ratioManager = ratioManager;
		this.unitManager = unitManager;
		this.factManager = factManager;
	}
	
	public void stageCreation(DataObject obj) {
		if (!stagedObjects.contains(obj)) {
			stagedObjects.add(obj);
		}
		obj.setMarkedForCreation(true);
	}
	
	public void stageUpdate(DataObject obj) {
		if (!stagedObjects.contains(obj)) {
			stagedObjects.add(obj);
		}
		obj.setMarkedForUpdate(true);
	}
	
	public void stageDeletion(DataObject obj) {
		if (!stagedObjects.contains(obj)) {
			stagedObjects.add(obj);
		}
		obj.setMarkedForDeletion(true);
	}
	
	public void saveChanges() {
		if (stagedObjects.get(0) instanceof Dimension) {
			dimManager.saveDimensions(stagedObjects);
		} else if (stagedObjects.get(0) instanceof DimensionHierarchy) {
			dimManager.saveHierarchies(stagedObjects);
		}  else if (stagedObjects.get(0) instanceof DimensionCombination) {
			dimManager.saveCombinations(stagedObjects);
		} else if (stagedObjects.get(0) instanceof ReferenceObject) {
			refObjManager.saveReferenceObjects(stagedObjects);
		} else if (stagedObjects.get(0) instanceof ReferenceObjectHierarchy) {
			refObjManager.saveHierarchies(stagedObjects);
		}  else if (stagedObjects.get(0) instanceof ReferenceObjectCombination) {
			refObjManager.saveCombinations(stagedObjects);
		} else if (stagedObjects.get(0) instanceof Ratio) {
			ratioManager.saveRatios(stagedObjects);
		} else if (stagedObjects.get(0) instanceof Fact) {
			factManager.saveFacts(stagedObjects);
		} else if (stagedObjects.get(0) instanceof DimensionCategory) {
			dimManager.saveCategories(stagedObjects);
		} else if (stagedObjects.get(0) instanceof RatioCategory) {
			ratioManager.saveCategories(stagedObjects);
		} else if (stagedObjects.get(0) instanceof Unit) {
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
	}
}
