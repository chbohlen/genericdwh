package genericdwh.db;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

public interface DatabaseReader {
	public TreeMap<Long, DimensionCategory> loadDimensionCategories();
	public TreeMap<Long, Dimension> loadDimensions();
	public ArrayList<Entry<Long, Long>> loadDimensionHierachies();
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations();
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId);
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId);
		
	public TreeMap<Long, RatioCategory> loadRatioCategories();
	public TreeMap<Long, Ratio> loadRatios();
	public ArrayList<Entry<Long, Long>> loadRatioRelations();
		
	public boolean dimensionHasRecords(long dimId);
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId);
	
	public long findDimAggregateId(Long[] componentIds);
	public long findRefObjAggregateId(Long[] componentIds);
	
	public Entry<Long, Double> loadFactForSingleRefObj(long ratioId, long refObjId);
	public Entry<Long, Entry<Long[], Double>> loadFactForRefObjCombination(long ratioId, long refObjId);
	public TreeMap<Long, Double> loadFactsForSingleDim(long ratioId, long dimId);
	public TreeMap<Long, Entry<Long[], Double>> loadFactsForDimCombination(long ratioId, long dimId);
}
