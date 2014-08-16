package genericdwh.dataobjects;

import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

import java.util.TreeMap;

public class ReferenceObjectManager {
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	private TreeMap<Long, TreeMap<Long, ReferenceObject>> referenceObjects;
	
	public ReferenceObjectManager(DatabaseController dbController) {
		dbReader = dbController.getReader();
		dbWriter = dbController.getWriter();
		
		referenceObjects = new TreeMap<Long, TreeMap<Long, ReferenceObject>>();
	}
	
	public TreeMap<Long, ReferenceObject> loadReferenceObjectsForDimension(Dimension dim) {
		referenceObjects.put(dim.getId(), dbReader.loadReferenceObjectsForDimension(dim));
		return referenceObjects.get(dim.getId());
	}
}
