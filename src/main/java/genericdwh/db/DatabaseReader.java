package genericdwh.db;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.jooq.Record;
import org.jooq.Result;

public interface DatabaseReader {
	public TreeMap<Long, DimensionCategory> loadDimensionCategories();
	public TreeMap<Long, Dimension> loadDimensions();
	public ArrayList<Entry<Long, Long>> loadDimensionHierachies();
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations();
	
	public long findDimensionCombinationId(long[] combination);
	
	public boolean dimensionHasRecords(long dimId);
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId);
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId);
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId);
	
	public long findReferenceObjectCombinationId(long[] combination);
	
	public TreeMap<Long, RatioCategory> loadRatioCategories();
	public TreeMap<Long, Ratio> loadRatios();
	public ArrayList<Entry<Long, Long>> loadRatioRelations();
	
	public Double loadFactForReferenceObject(long ratioId, long referenceObjectId);
	public Map<Long, Result<Record>> loadFactsForDimension(long ratioId, long dimensionId);
}
