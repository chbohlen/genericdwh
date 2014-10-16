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
	
	public void createDimensions(List<Dimension> creations);
	public void updateDimensions(List<Dimension> updates);
	public void deleteDimensions(List<Dimension> deletions);
	
	public void createReferenceObjects(List<ReferenceObject> creations);
	public void updateReferenceObjects(List<ReferenceObject> updates);
	public void deleteReferenceObjects(List<ReferenceObject> deletions);
	
	public void createDimensionHierarchies(List<DimensionHierarchy> creations);
	public void updateDimensionHierarchies(List<DimensionHierarchy> updates);
	public void deleteDimensionHierarchies(List<DimensionHierarchy> deletions);
	
	public void createReferenceObjectHierarchies(List<ReferenceObjectHierarchy> creations);
	public void updateReferenceObjectHierarchies(List<ReferenceObjectHierarchy> updates);
	public void deleteReferenceObjectHierarchies(List<ReferenceObjectHierarchy> deletions);
	
	public void createDimensionCombinations(List<DimensionCombination> creations);
	public void updateDimensionCombinations(List<DimensionCombination> updates);
	public void deleteDimensionCombinations(List<DimensionCombination> deletions);
	
	public void createReferenceObjectCombinations(List<ReferenceObjectCombination> creations);
	public void updateReferenceObjectCombinations(List<ReferenceObjectCombination> updates);
	public void deleteReferenceObjectCombinations(List<ReferenceObjectCombination> deletions);
	
	public void createRatios(List<Ratio> creations);
	public void updateRatios(List<Ratio> updates);
	public void deleteRatios(List<Ratio> deletions);
	
	public void createRatioRelations(List<RatioRelation> creations);
	public void updateRatioRelations(List<RatioRelation> updates);
	public void deleteRatioRelations(List<RatioRelation> deletions);
	
	public void createFacts(List<Fact> creations);
	public void updateFacts(List<Fact> updates);
	public void deleteFacts(List<Fact> deletions);
	
	public void createDimensionCategories(List<DimensionCategory> creations);
	public void updateDimensionCategories(List<DimensionCategory> updates);
	public void deleteDimensionCategories(List<DimensionCategory> deletions);
	
	public void createRatioCategories(List<RatioCategory> creations);
	public void updateRatioCategories(List<RatioCategory> updates);
	public void deleteRatioCategories(List<RatioCategory> deletions);
	
	public void createUnits(List<Unit> creations);
	public void updateUnits(List<Unit> updates);
	public void deleteUnits(List<Unit> deletions);
}
