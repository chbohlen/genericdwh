package genericdwh.db;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.unit.Unit;

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
	
	public TreeMap<Long, Unit> loadUnits();
		
	public boolean dimensionHasRecords(long dimId);
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId);
	
	public long findDimAggregateId(Long[] componentIds);
	public long findRefObjAggregateId(Long[] componentIds);
	
	public ResultObject loadFactForSingleRefObj(long ratioId, long refObjId);
	public ResultObject loadFactForRefObjCombination(long ratioId, long refObjId);
	public ArrayList<ResultObject> loadFactsForSingleDim(long ratioId, long dimId);
	public ArrayList<ResultObject> loadFactsForDimCombination(long ratioId, long dimId);
	public ArrayList<ResultObject> loadFactsForDimCombinationAndRefObjs(long ratioId, long dimId, Long[] refObjIds);
}
