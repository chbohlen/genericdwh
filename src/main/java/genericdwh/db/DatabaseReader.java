package genericdwh.db;

import genericdwh.dataobjects.Dimension;
import genericdwh.dataobjects.ReferenceObject;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

public interface DatabaseReader {
	public TreeMap<Long, Dimension> loadDimensions();
	public ArrayList<Entry<Long, Long>> loadDimensionHierachies();
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations();
	
	public boolean dimensionHasRecords(long id);
	
	public TreeMap<Long, ReferenceObject> loadReferenceObjectsForDimension(Dimension dim);
}
