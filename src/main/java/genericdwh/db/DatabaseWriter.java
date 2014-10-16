package genericdwh.db;

import java.util.List;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionCombination;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.ratio.RatioRelation;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectCombination;
import genericdwh.dataobjects.referenceobject.ReferenceObjectHierarchy;
import genericdwh.dataobjects.unit.Unit;

public interface DatabaseWriter {
	
	public boolean createDimensions(List<Dimension> creations);
	public boolean updateDimensions(List<Dimension> updates);
	public boolean deleteDimensions(List<Dimension> deletions);
	
	public boolean createReferenceObjects(List<ReferenceObject> creations);
	public boolean updateReferenceObjects(List<ReferenceObject> updates);
	public boolean deleteReferenceObjects(List<ReferenceObject> deletions);
	
	public boolean createDimensionHierarchies(List<DimensionHierarchy> creations);
	public boolean updateDimensionHierarchies(List<DimensionHierarchy> updates);
	public boolean deleteDimensionHierarchies(List<DimensionHierarchy> deletions);
	
	public boolean createReferenceObjectHierarchies(List<ReferenceObjectHierarchy> creations);
	public boolean updateReferenceObjectHierarchies(List<ReferenceObjectHierarchy> updates);
	public boolean deleteReferenceObjectHierarchies(List<ReferenceObjectHierarchy> deletions);
	
	public boolean createDimensionCombinations(List<DimensionCombination> creations);
	public boolean updateDimensionCombinations(List<DimensionCombination> updates);
	public boolean deleteDimensionCombinations(List<DimensionCombination> deletions);
	
	public boolean createReferenceObjectCombinations(List<ReferenceObjectCombination> creations);
	public boolean updateReferenceObjectCombinations(List<ReferenceObjectCombination> updates);
	public boolean deleteReferenceObjectCombinations(List<ReferenceObjectCombination> deletions);
	
	public boolean createRatios(List<Ratio> creations);
	public boolean updateRatios(List<Ratio> updates);
	public boolean deleteRatios(List<Ratio> deletions);
	
	public boolean createRatioRelations(List<RatioRelation> creations);
	public boolean updateRatioRelations(List<RatioRelation> updates);
	public boolean deleteRatioRelations(List<RatioRelation> deletions);
	
	public boolean createFacts(List<Fact> creations);
	public boolean updateFacts(List<Fact> updates);
	public boolean deleteFacts(List<Fact> deletions);
	
	public boolean createDimensionCategories(List<DimensionCategory> creations);
	public boolean updateDimensionCategories(List<DimensionCategory> updates);
	public boolean deleteDimensionCategories(List<DimensionCategory> deletions);
	
	public boolean createRatioCategories(List<RatioCategory> creations);
	public boolean updateRatioCategories(List<RatioCategory> updates);
	public boolean deleteRatioCategories(List<RatioCategory> deletions);
	
	public boolean createUnits(List<Unit> creations);
	public boolean updateUnits(List<Unit> updates);
	public boolean deleteUnits(List<Unit> deletions);
}
