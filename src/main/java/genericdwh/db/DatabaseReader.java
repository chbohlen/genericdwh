package genericdwh.db;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.unit.Unit;

import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

public interface DatabaseReader {	
	public TreeMap<Long, DimensionCategory> loadDimensionCategories();
	public TreeMap<Long, Dimension> loadDimensions();
	public List<Entry<Long, Long>> loadDimensionHierachies();
	public List<Entry<Long, Long>> loadDimensionCombinations();
	
	public TreeMap<Long, ReferenceObject> loadRefObjs();
	public List<Entry<Long, Long>> loadReferenceObjectHierachies();
	public List<Entry<Long, Long>> loadReferenceObjectCombinations();
	public ReferenceObject loadRefObj(long refObjId);
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId);
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId);
	public List<Long> loadRefObjChildrenIds(long refObjId);
		
	public TreeMap<Long, RatioCategory> loadRatioCategories();
	public TreeMap<Long, Ratio> loadRatios();
	public List<Entry<Long, Long>> loadRatioRelations();
	
	public TreeMap<Long, Unit> loadUnits();
		
	public boolean dimensionHasRecords(long dimId);
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId);
	public boolean dimensionIsCombination(long dimId);
	
	public long findDimAggregateId(Long[] componentIds);
	public long findRefObjAggregateId(Long[] componentIds);
	
	public List<Fact> loadFacts();
	public ResultObject loadFactForSingleRefObj(long ratioId, long refObjId);
	public ResultObject loadFactForRefObjCombination(long ratioId, long refObjId);
	public List<ResultObject> loadFactsForSingleDim(long ratioId, long dimId, Long[] filterRefObjIds);
	public List<ResultObject> loadFactsForDimCombination(long ratioId, long dimId, Long[] filterRefObjIds);
	public List<ResultObject> loadFactsForDimCombinationAndRefObjs(long ratioId, long dimId, Long[] refObjIds, Long[] filterRefObjIds);
	public List<ResultObject> loadAllFactsForRatio(long ratioId, Long[] filterRefObjIds);
}
