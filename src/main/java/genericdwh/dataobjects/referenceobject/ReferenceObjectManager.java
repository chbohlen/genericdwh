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
	
	public ArrayList<TreeMap<Long, ReferenceObject>> loadRefObjs(List<DataObject> dims) {
		ArrayList<TreeMap<Long, ReferenceObject>> result = new ArrayList<>();
		for (DataObject dim : dims) {
			if (dim instanceof ReferenceObject) {
				ReferenceObject refObj = getReferenceObject(dim.getId());
				TreeMap<Long, ReferenceObject> refObjInTreeMap = new TreeMap<>();
				refObjInTreeMap.put(refObj.getId(), refObj);
				result.add(refObjInTreeMap);
			} else {
				if (dim instanceof DimensionHierarchy) {
					dim = ((DimensionHierarchy)dim).getTopLevel();
				}
				result.add(loadRefObjsForDim(dim.getId()));
			}
		}
		
		return result;	
	}
		
	public long findRefObjAggregateId(ArrayList<DataObject> combinedDims) {
		return dbReader.findRefObjAggregateId(readRefObjComponentIds(combinedDims));
	}
	
	public Long[] readRefObjComponentIds(ArrayList<DataObject> combinedDims) {
		ArrayList<Long> refObjIds = new ArrayList<>();
		for (DataObject obj : combinedDims) {
			if (obj instanceof ReferenceObject) {
				refObjIds.add(obj.getId());
			}
		}
		return refObjIds.toArray(new Long[0]);
	}
	
	public ReferenceObject getReferenceObject(long refObjId) {
		return referenceObjects.get(refObjId);
	}
}
