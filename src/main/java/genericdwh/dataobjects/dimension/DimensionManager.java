package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.DatabaseController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import lombok.Getter;

public class DimensionManager extends DataObjectManager {

	@Getter private TreeMap<Long, DimensionCategory> categories;
	@Getter private TreeMap<Long, Dimension> dimensions;
	@Getter private List<DimensionHierarchy> hierarchies;
		
	public DimensionManager(DatabaseController dbController) {
		super(dbController);
	}
	
	
	public void loadDimensions() {
		dimensions = dbReader.loadDimensions();
		
		List<Entry<Long, Long>> dimCombinations = dbReader.loadDimensionCombinations();
			for (Entry<Long, Long> combination : dimCombinations) {
				dimensions.get(combination.getKey()).addComponent(dimensions.get(combination.getValue()));
		}
	}
	
	public void loadHierarchies() {
		for (Dimension dim : dimensions.values()) {
			dim.clearChildren();
		}
		
		List<Entry<Long, Long>> dimHierarchies = dbReader.loadDimensionHierachies();
		for (Entry<Long, Long> hierarchy : dimHierarchies) {
			dimensions.get(hierarchy.getKey()).addChildren(dimensions.get(hierarchy.getValue()));
		}
		
		hierarchies = generateHierarchies();
	}
	
	public void loadCategories() {
		categories = dbReader.loadDimensionCategories();
	}
	
	private List<DimensionHierarchy> generateHierarchies() {
		ArrayList<DimensionHierarchy> newHierarchies = new ArrayList<>();
		
		for (Dimension currDim : dimensions.values()) {
			if (currDim.isHierarchy()) {
				LinkedList<DimensionHierarchy> tmpNewHierarchies = new LinkedList<>();
				tmpNewHierarchies.add(new DimensionHierarchy(currDim));
				
				do {
					DimensionHierarchy currNewHierarchy = tmpNewHierarchies.pop();
					
					Dimension lastLevel = currNewHierarchy.getLevels().getLast();
					if (lastLevel.isHierarchy()) {
						for (Dimension child : lastLevel.getChildren()) {
							DimensionHierarchy currNewHierarchyClone = (DimensionHierarchy)currNewHierarchy.clone();
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

	
	public long findDimAggregateId(ArrayList<DataObject> combinedDims)  {
		if (combinedDims.size() < 2) {
			return determineDimCombinationComponentIds(combinedDims)[0];
		}
		return dbReader.findDimAggregateId(determineDimCombinationComponentIds(combinedDims));
	}
	
	private Long[] determineDimCombinationComponentIds(ArrayList<DataObject> combinedDims) {
		ArrayList<Long> combination = new ArrayList<>();
		
		for (DataObject obj : combinedDims) {
			if (obj instanceof DimensionHierarchy) {
				combination.add(((DimensionHierarchy)obj).getTopLevel().getId());
			} else if (obj instanceof ReferenceObject) {
				combination.add(((ReferenceObject)obj).getDimensionId());
			} else {
				combination.add(obj.getId());
			}
		}
		if (combination.isEmpty()) {
			return new Long[] { (long)-1 };
		}
		return combination.toArray(new Long[0]);
	}
	
	
	public Dimension getDimension(long dimId) {
		return dimensions.get(dimId);
	}
	
	
	public void initAll() {
		initCategories();
		initDimensions();
		initHierarchies();
	}

	public void initCategories() {
		for (DimensionCategory cat : categories.values()) {
			cat.initProperties();
		}
	}

	public void initDimensions() {
		for (Dimension dim : dimensions.values()) {
			dim.initProperties();
		}
	}
	
	public void initHierarchies() {
		for (DimensionHierarchy dimHierarchy : hierarchies) {
			dimHierarchy.initProperties();
		}
	}

	
	public void saveDimensions(List<DataObject> stagedObjects) {
		List<Dimension> deletions = new ArrayList<>();
		List<Dimension> creations = new ArrayList<>();
		List<Dimension> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			Dimension dim = (Dimension)obj;
			if (dim.isMarkedForDeletion()) {
				if (!dim.isMarkedForCreation()) {
					deletions.add(dim);
				}
			} else {
				if (dim.isMarkedForCreation()) {
					creations.add(dim);
				} else {
					updates.add(dim);
				}
			}
		}
		
		dbWriter.deleteDimensions(deletions);
		dbWriter.createDimensions(creations);
		dbWriter.updateDimensions(updates);
		
		loadDimensions();
	}

	public void saveHierarchies(List<DataObject> stagedObjects) {
		List<DimensionHierarchy> deletions = new ArrayList<>();
		List<DimensionHierarchy> creations = new ArrayList<>();
		List<DimensionHierarchy> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			DimensionHierarchy hierarchy = (DimensionHierarchy)obj;
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
		
		dbWriter.deleteDimensionHierarchies(deletions);
		dbWriter.createDimensionHierarchies(creations);
		dbWriter.updateDimensionHierarchies(updates);
		
		loadHierarchies();
	}

	public void saveCategories(List<DataObject> stagedObjects) {
		List<DimensionCategory> deletions = new ArrayList<>();
		List<DimensionCategory> creations = new ArrayList<>();
		List<DimensionCategory> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			DimensionCategory cat = (DimensionCategory)obj;
			if (cat.isMarkedForDeletion()) {
				if (!cat.isMarkedForCreation()) {
					deletions.add(cat);
				}
			} else {
				if (cat.isMarkedForCreation()) {
					creations.add(cat);
				} else {
					updates.add(cat);
				}
			}
		}
		
		dbWriter.deleteDimensionCategories(deletions);
		dbWriter.createDimensionCategories(creations);
		dbWriter.updateDimensionCategories(updates);
		
		loadCategories();
	}
}
