package genericdwh.dataobjects;

import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.fact.FactManager;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
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
		obj.setMarkedForCreation(true);
		stagedObjects.add(obj);
	}
	
	public void stageUpdate(DataObject obj) {
		obj.setMarkedForUpdate(true);
		stagedObjects.add(obj);
	}
	
	public void stageDeletion(DataObject obj) {
		obj.setMarkedForDeletion(true);
		stagedObjects.add(obj);
	}
	
	public void saveChanges() {
		
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
