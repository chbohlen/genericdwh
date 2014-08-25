package genericdwh.dataobjects.dimension;

import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;

import lombok.Getter;

public class DimensionManager {
	
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	@Getter private TreeMap<Long, DimensionCategory> categories;
	@Getter private TreeMap<Long, Dimension> dimensions;
	@Getter private ArrayList<DimensionHierarchy> hierarchies;
	
	public DimensionManager(DatabaseController dbController) {
		dbReader = dbController.getDbReader();
		dbWriter = dbController.getDbWriter();
	}
	
	public void loadDimensions() {
		dimensions = dbReader.loadDimensions();
		
		ArrayList<Entry<Long, Long>> dimCombinations = dbReader.loadDimensionCombinations();
		for (Entry<Long, Long> combination : dimCombinations) {
			dimensions.get(combination.getKey()).addComponent(dimensions.get(combination.getValue()));
		}
		
		ArrayList<Entry<Long, Long>> dimHierarchies = dbReader.loadDimensionHierachies();
		for (Entry<Long, Long> hierarchy : dimHierarchies) {
			dimensions.get(hierarchy.getKey()).addChildren(dimensions.get(hierarchy.getValue()));
		}
		
		hierarchies = generateHierarchies();
	}
	
	public void loadCategories() {
		categories = dbReader.loadDimensionCategories();
	}
	
	private ArrayList<DimensionHierarchy> generateHierarchies() {
		ArrayList<DimensionHierarchy> newHierarchies = new ArrayList<DimensionHierarchy>();
		
		for (Entry<Long, Dimension> currEntry : dimensions.entrySet()) {
			Dimension currDim = currEntry.getValue();
			if (currDim.isHierarchy()) {
				LinkedList<DimensionHierarchy> tmpNewHierarchies = new LinkedList<DimensionHierarchy>();
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
	
	public Dimension findCombination(ArrayList<Dimension> combination) {
		for (Entry<Long, Dimension> currEntry : dimensions.entrySet()) {
			Dimension currDim = currEntry.getValue();
			if (currDim.isCombination() 
					&& currDim.getComponents().size() == combination.size() 
					&& currDim.getComponents().containsAll(combination)) {
				
				return currDim;
			}
		}
		
		return null;
	}
}