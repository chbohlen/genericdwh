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
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId);
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId);
	
	public boolean dimensionHasRecords(long dimId);
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId);
}
