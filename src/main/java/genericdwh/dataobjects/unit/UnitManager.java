package genericdwh.dataobjects.unit;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectManager;
import genericdwh.db.DatabaseController;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import lombok.Getter;

public class UnitManager extends DataObjectManager {

	@Getter private TreeMap<Long, Unit> units;
	
	public UnitManager(DatabaseController dbController) {
		super(dbController);
	}
	
	
	public void loadUnits() {
		units = dbReader.loadUnits();
	}
	
	
	public Unit getUnit(long id) {
		return units.get(id);
	}
	
	
	public void initUnits() {
		for (Unit unit : units.values()) {
			unit.initProperties();
		}
	}

	
	public void saveUnits(List<DataObject> stagedObjects) {
		List<Unit> deletions = new ArrayList<>();
		List<Unit> creations = new ArrayList<>();
		List<Unit> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			Unit unit = (Unit)obj;
			if (unit.isMarkedForDeletion()) {
				if (!unit.isMarkedForCreation()) {
					deletions.add(unit);
				}
			} else {
				if (unit.isMarkedForCreation()) {
					creations.add(unit);
				} else {
					updates.add(unit);
				}
			}
		}
		
		dbWriter.deleteUnits(deletions);
		dbWriter.createUnits(creations);
		dbWriter.updateUnits(updates);
		
		loadUnits();
	}
}
