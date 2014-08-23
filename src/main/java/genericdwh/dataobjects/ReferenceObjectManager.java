package genericdwh.dataobjects;

import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

import java.util.TreeMap;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class ReferenceObjectManager {
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	private Table<Long, Long, TreeMap<Long, ReferenceObject>> referenceObjects;
	
	public ReferenceObjectManager(DatabaseController dbController) {
		dbReader = dbController.getReader();
		dbWriter = dbController.getWriter();
		
		referenceObjects = TreeBasedTable.create();
	}
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(Dimension dim) {
		referenceObjects.put(dim.getId(), new Long(0), dbReader.loadRefObjsForDim(dim.getId()));
		return new TreeMap<Long, ReferenceObject>(referenceObjects.get(dim.getId(), new Long(0)));
	}
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(Dimension dim, ReferenceObject refObjParent) {
		referenceObjects.put(dim.getId(), refObjParent.getId(), dbReader.loadRefObjsForDimAndRefObjParent(dim.getId(), refObjParent.getId()));
		return new TreeMap<Long, ReferenceObject>(referenceObjects.get(dim.getId(), refObjParent.getId()));
	}
	
	public boolean dimensionHasRecords(Dimension dim) {
		return dbReader.dimensionHasRecords(dim.getId());
	}
	
	public boolean dimensionAndRefObjParentHaveRecords(Dimension dim, ReferenceObject refObj) {
		return dbReader.dimensionAndRefObjParentHaveRecords(dim.getId(), refObj.getId());
	}
}
