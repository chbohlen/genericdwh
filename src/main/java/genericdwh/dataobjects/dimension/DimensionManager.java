package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.DatabaseController;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;

import lombok.Getter;

public class DimensionManager extends DataObjectManager {

	@Getter private TreeMap<Long, DimensionCategory> categories;
	@Getter private TreeMap<Long, Dimension> dimensions;
	@Getter private ArrayList<DimensionHierarchy> hierarchies;
	
	public DimensionManager(DatabaseController dbController) {
		super(dbController);
	}
	
	public void loadDimensions() {
		dimensions = dbReader.loadDimensions();
		
//		ArrayList<Entry<Long, Long>> dimCombinations = dbReader.loadDimensionCombinations();
//		for (Entry<Long, Long> combination : dimCombinations) {
//			dimensions.get(combination.getKey()).addComponent(dimensions.get(combination.getValue()));
//		}
		
		ArrayList<Entry<Long, Long>> dimHierarchies = dbReader.loadDimensionHierachies();
		for (Entry<Long, Long> hierarchy : dimHierarchies) {
			dimensions.get(hierarchy.getKey()).addChildren(dimensions.get(hierarchy.getValue()));
		}
		
		for (Dimension dim : dimensions.values()) {
			dim.setIsCombination(dbReader.dimensionIsCombination(dim.getId()));
		}
		
		hierarchies = generateHierarchies();
	}
	
	public void loadCategories() {
		categories = dbReader.loadDimensionCategories();
	}
	
	private ArrayList<DimensionHierarchy> generateHierarchies() {
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
							DimensionHierarchy currNewHierarchyClone = currNewHierarchy.clone();
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
	
//	public Dimension findCombination(ArrayList<Dimension> combination) {
//	for (Entry<Long, Dimension> currEntry : dimensions.entrySet()) {
//		Dimension currDim = currEntry.getValue();
//		if (currDim.isCombination() 
//				&& currDim.getComponents().size() == combination.size() 
//				&& currDim.getComponents().containsAll(combination)) {
//			
//			return currDim;
//		}
//	}
//	
//	return null;
//}
	public Dimension getDimension(long dimId) {
		return dimensions.get(dimId);
	}	
}
