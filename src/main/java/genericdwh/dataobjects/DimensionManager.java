package genericdwh.dataobjects;

import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

public class DimensionManager {
	
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	private TreeMap<Long, Dimension> dimensions;
	
	public DimensionManager(DatabaseController dbController) {
		dbReader = dbController.getReader();
		dbWriter = dbController.getWriter();
	}
	
	public TreeMap<Long, Dimension> loadDimensions() {
		dimensions = dbReader.loadDimensions();
		
		ArrayList<Entry<Long, Long>> dimHierarchies = dbReader.loadDimensionHierachies();
		ArrayList<Entry<Long, Long>> dimCombinations = dbReader.loadDimensionCombinations();
		
		for (Entry<Long, Long> hierarchy : dimHierarchies) {
			dimensions.get(hierarchy.getKey()).addChild(dimensions.get(hierarchy.getValue()));
		}
		
		for (Entry<Long, Long> combination : dimCombinations) {
			dimensions.get(combination.getKey()).addComponent(dimensions.get(combination.getValue()));
		}
		
		return dimensions;
	}
	
	public boolean dimensionHasRecords(Dimension dim) {
		return dbReader.dimensionHasRecords(dim.getId());
	}
}
