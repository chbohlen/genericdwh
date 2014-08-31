package genericdwh.dataobjects.unit;

import genericdwh.dataobjects.DataObjectManager;
import genericdwh.db.DatabaseController;

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
}
