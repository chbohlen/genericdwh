package genericdwh.dataobjects.referenceobject;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectManager;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.db.DatabaseController;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ReferenceObjectManager extends DataObjectManager {

	private TreeMap<Long, ReferenceObject> referenceObjects = new TreeMap<>();
	
	public ReferenceObjectManager(DatabaseController dbController) {
		super(dbController);
	}
	
	public ReferenceObject loadRefObj(long refObjId) {
		ReferenceObject newRefObj = dbReader.loadRefObj(refObjId);
		referenceObjects.put(newRefObj.getId(), newRefObj);
		return newRefObj;
	}
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId) {
		TreeMap<Long, ReferenceObject> newRefObjs = dbReader.loadRefObjsForDim(dimId);
		referenceObjects.putAll(newRefObjs);
		return newRefObjs;
	}
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjParentId) {
		TreeMap<Long, ReferenceObject> newRefObjs = dbReader.loadRefObjsForDimAndRefObjParent(dimId, refObjParentId);
		referenceObjects.putAll(newRefObjs);
		return newRefObjs;
	}
	
	public boolean dimensionHasRecords(Dimension dim) {
		return dbReader.dimensionHasRecords(dim.getId());
	}
	
	public boolean dimensionAndRefObjParentHaveRecords(Dimension dim, ReferenceObject refObj) {
		return dbReader.dimensionAndRefObjParentHaveRecords(dim.getId(), refObj.getId());
	}
	
	public ArrayList<TreeMap<Long, ReferenceObject>> loadRefObjs(List<DataObject> dims, List<DataObject> filter) {
		ArrayList<TreeMap<Long, ReferenceObject>> result = new ArrayList<>();
		for (DataObject dim : dims) {
			if (dim instanceof ReferenceObject) {
				ReferenceObject refObj = getReferenceObject(dim.getId());
				refObj.setChildrenIds(dbReader.loadRefObjChildrenIds(refObj.getId()));
				TreeMap<Long, ReferenceObject> refObjInTreeMap = new TreeMap<>();
				refObjInTreeMap.put(refObj.getId(), refObj);
				result.add(refObjInTreeMap);
			} else {
				if (dim instanceof DimensionHierarchy) {
					dim = ((DimensionHierarchy)dim).getTopLevel();
				}
				TreeMap<Long, ReferenceObject> refObjs = loadRefObjsForDim(dim.getId());
				for (ReferenceObject refObj : refObjs.values()) {
					refObj.setChildrenIds(dbReader.loadRefObjChildrenIds(refObj.getId()));
				}
				result.add(refObjs);
			}
		}
		
		Long[] filterRefObjIds = readRefObjIds(filter);
		for (long filterRefObjId : filterRefObjIds) {
			for (TreeMap<Long, ReferenceObject> dim : result) {
				dim.remove(filterRefObjId);
			}
		}
		
		return result;	
	}
		
	public long findRefObjAggregateId(ArrayList<DataObject> combinedDims) {
		if (combinedDims.size() < 2) {
			return readRefObjIds(combinedDims)[0];
		}
		return dbReader.findRefObjAggregateId(readRefObjIds(combinedDims));
	}
	
	public Long[] readRefObjIds(List<DataObject> list) {
		ArrayList<Long> refObjIds = new ArrayList<>();
		for (DataObject obj : list) {
			if (obj instanceof ReferenceObject) {
				refObjIds.add(obj.getId());
			}
		}
		if (refObjIds.isEmpty()) {
			return new Long[] { (long)-1 };
		}
		return refObjIds.toArray(new Long[0]);
	}
	
	public ReferenceObject getReferenceObject(long refObjId) {
		return referenceObjects.get(refObjId);
	}
}
