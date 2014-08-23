package genericdwh.dataobjects;

import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Map.Entry;

public class DimensionManager {
	
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	private TreeMap<Long, Dimension> dimensions;
	private ArrayList<DimensionHierarchy> hierarchies;
	
	public DimensionManager(DatabaseController dbController) {
		dbReader = dbController.getReader();
		dbWriter = dbController.getWriter();
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
		
		hierarchies = generateDimensionHierarchies();
	}
	
	private ArrayList<DimensionHierarchy> generateDimensionHierarchies() {
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
	
	public TreeMap<Long, Dimension> getDimensions() {
		return dimensions;
	}

	public ArrayList<DimensionHierarchy> getHierachies() {
		return hierarchies;
	}
}
