package genericdwh.dataobjects.referenceobject;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectManager;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.db.DatabaseController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import lombok.Getter;

public class ReferenceObjectManager extends DataObjectManager {

	@Getter private TreeMap<Long, ReferenceObject> referenceObjects = new TreeMap<>();
	@Getter private List<ReferenceObjectHierarchy> hierarchies;
	@Getter private List<ReferenceObjectCombination> combinations;
	
	public ReferenceObjectManager(DatabaseController dbController) {
		super(dbController);
	}
	
	
	public void loadReferenceObjects() {
		referenceObjects = dbReader.loadRefObjs();
	}
	
	public void loadHierarchies() {
		for (ReferenceObject refObj : referenceObjects.values()) {
			refObj.clearChildren();
		}
		
		List<Entry<Long, Long>> refObjHierarchies = dbReader.loadReferenceObjectHierachies();
		for (Entry<Long, Long> hierarchy : refObjHierarchies) {
			referenceObjects.get(hierarchy.getKey()).addChildren(referenceObjects.get(hierarchy.getValue()));
		}
		
		hierarchies = generateHierarchies();
	}
	
	public void loadCombinations() {
		for (ReferenceObject refObj : referenceObjects.values()) {
			refObj.clearComponents();
		}
		
		combinations = new ArrayList<>();
		List<Entry<Long, Long>> refObjCombinations = dbReader.loadReferenceObjectCombinations();
		for (Entry<Long, Long> combination : refObjCombinations) {
			referenceObjects.get(combination.getKey()).addComponent(referenceObjects.get(combination.getValue()));
		}
		for (ReferenceObject refObj : referenceObjects.values()) {
			if (refObj.isCombination()) {
				combinations.add(new ReferenceObjectCombination(refObj));
			}
		}
	}
	
	private List<ReferenceObjectHierarchy> generateHierarchies() {
		ArrayList<ReferenceObjectHierarchy> newHierarchies = new ArrayList<>();
		
		for (ReferenceObject currRefObj : referenceObjects.values()) {
			if (currRefObj.isHierarchy()) {
				LinkedList<ReferenceObjectHierarchy> tmpNewHierarchies = new LinkedList<>();
				tmpNewHierarchies.add(new ReferenceObjectHierarchy(currRefObj));
				
				do {
					ReferenceObjectHierarchy currNewHierarchy = tmpNewHierarchies.pop();
					
					ReferenceObject lastLevel = currNewHierarchy.getLevels().getLast();
					if (lastLevel.isHierarchy()) {
						for (ReferenceObject child : lastLevel.getChildren()) {
							ReferenceObjectHierarchy currNewHierarchyClone = (ReferenceObjectHierarchy)currNewHierarchy.clone();
							currNewHierarchyClone.addLevel(child);
							tmpNewHierarchies.add(currNewHierarchyClone);
						}
					} else {
						newHierarchies.add(currNewHierarchy);
					}
				} while (!tmpNewHierarchies.isEmpty());
			}
		}
		
		return newHierarchies;
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
				if (!refObjs.isEmpty()) {
					result.add(refObjs);
				}
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

		
	public long findRefObjAggregateId(List<DataObject> combinedDims) {
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
	
	
	public void initHierarchies() {
		for (ReferenceObjectHierarchy refObjHierarchy : hierarchies) {
			refObjHierarchy.initProperties();
		}
	}
	
	public void initReferenceObjects() {
		for (ReferenceObject refObj : referenceObjects.values()) {
			refObj.initProperties();
		}
	}
	
	public void initCombinations() {
		for (ReferenceObjectCombination refObjCombination : combinations) {
			refObjCombination.initProperties();
		}
	}
	
	
	public void saveReferenceObjects(List<DataObject> stagedObjects) {
		List<ReferenceObject> deletions = new ArrayList<>();
		List<ReferenceObject> creations = new ArrayList<>();
		List<ReferenceObject> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			ReferenceObject refObj = (ReferenceObject)obj;
			if (refObj.isMarkedForDeletion()) {
				if (!refObj.isMarkedForCreation()) {
					deletions.add(refObj);
				}
			} else {
				if (refObj.isMarkedForCreation()) {
					creations.add(refObj);
				} else {
					updates.add(refObj);
				}
			}
		}
		
		dbWriter.deleteReferenceObjects(deletions);
		dbWriter.createReferenceObjects(creations);
		dbWriter.updateReferenceObjects(updates);
		
		loadReferenceObjects();
	}

	public void saveHierarchies(List<DataObject> stagedObjects) {
		List<ReferenceObjectHierarchy> deletions = new ArrayList<>();
		List<ReferenceObjectHierarchy> creations = new ArrayList<>();
		List<ReferenceObjectHierarchy> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			ReferenceObjectHierarchy hierarchy = (ReferenceObjectHierarchy)obj;
			if (hierarchy.isMarkedForDeletion()) {
				if (!hierarchy.isMarkedForCreation()) {
					deletions.add(hierarchy);
				}
			} else {
				if (hierarchy.isMarkedForCreation()) {
					creations.add(hierarchy);
				} else {
					updates.add(hierarchy);
				}
			}
		}
		
		dbWriter.deleteReferenceObjectHierarchies(deletions);
		dbWriter.createReferenceObjectHierarchies(creations);
		dbWriter.updateReferenceObjectHierarchies(updates);
		
		loadHierarchies();
	}

	public void saveCombinations(List<DataObject> stagedObjects) {
		List<ReferenceObjectCombination> deletions = new ArrayList<>();
		List<ReferenceObjectCombination> creations = new ArrayList<>();
		List<ReferenceObjectCombination> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			ReferenceObjectCombination combination = (ReferenceObjectCombination)obj;
			if (combination.isMarkedForDeletion()) {
				if (!combination.isMarkedForCreation()) {
					deletions.add(combination);
				}
			} else {
				if (combination.isMarkedForCreation()) {
					creations.add(combination);
				} else {
					updates.add(combination);
				}
			}
		}
		
		dbWriter.deleteReferenceObjectCombinations(deletions);
		dbWriter.createReferenceObjectCombinations(creations);
		dbWriter.updateReferenceObjectCombinations(updates);
		
		if (!creations.isEmpty() || !deletions.isEmpty()) {
			loadReferenceObjects();
		}
		loadCombinations();
	}
}