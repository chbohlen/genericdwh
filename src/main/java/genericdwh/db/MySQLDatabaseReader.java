package genericdwh.db;

import static genericdwh.db.model.tables.DimensionCategories.DIMENSION_CATEGORIES;
import static genericdwh.db.model.tables.DimensionCombinations.DIMENSION_COMBINATIONS;
import static genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES;
import static genericdwh.db.model.tables.Dimensions.DIMENSIONS;
import static genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES;
import static genericdwh.db.model.tables.RatioRelations.RATIO_RELATIONS;
import static genericdwh.db.model.tables.Ratios.RATIOS;
import static genericdwh.db.model.tables.ReferenceObjectHierarchies.REFERENCE_OBJECT_HIERARCHIES;
import static genericdwh.db.model.tables.ReferenceObjects.REFERENCE_OBJECTS;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.model.tables.DimensionCombinations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import lombok.Setter;

import org.jooq.DSLContext;
import org.jooq.JoinType;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class MySQLDatabaseReader implements DatabaseReader {
	
	@Setter private DSLContext dslContext;
	
	
	@Override
	public TreeMap<Long, DimensionCategory> loadDimensionCategories() {
		Map<Long, DimensionCategory> catMap = dslContext
												.select(DIMENSION_CATEGORIES.CATEGORY_ID, DIMENSION_CATEGORIES.NAME)
												.from(DIMENSION_CATEGORIES)
												.fetch()
												.intoMap(DIMENSION_CATEGORIES.CATEGORY_ID, DimensionCategory.class);
		return new TreeMap<Long, DimensionCategory>(catMap);
	}
	
	@Override
	public TreeMap<Long, Dimension> loadDimensions() {
		Map<Long, Dimension> dimMap = dslContext
										.select(DIMENSIONS.DIMENSION_ID, DIMENSIONS.NAME, DIMENSION_CATEGORIES.NAME)
										.from(DIMENSIONS
										.leftOuterJoin(DIMENSION_CATEGORIES)
										.on(DIMENSIONS.CATEGORY_ID.equal(DIMENSION_CATEGORIES.CATEGORY_ID)))
										.fetch()
										.intoMap(DIMENSIONS.DIMENSION_ID, Dimension.class);
		return new TreeMap<Long, Dimension>(dimMap);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadDimensionHierachies() {
		List<Entry<Long, Long>> hierarchyList = dslContext
													.select(DIMENSION_HIERARCHIES.PARENT_ID, DIMENSION_HIERARCHIES.CHILD_ID)
													.from(DIMENSION_HIERARCHIES)
													.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<Entry<Long, Long>>(hierarchyList);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations() {

		List<Entry<Long, Long>> combinationList = dslContext
													.select(DIMENSION_COMBINATIONS.AGGREGATE_ID, DIMENSION_COMBINATIONS.COMPONENT_ID)
													.from(DIMENSION_COMBINATIONS)
													.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<Entry<Long, Long>>(combinationList);
	}

	
	@Override
	public long findDimensionCombination(ArrayList<Dimension> combination) {
		int componentCount = combination.size();
		
		Table<Record2<Long, Integer>> query = dslContext
												.select(DIMENSION_COMBINATIONS.AGGREGATE_ID, DSL.count().as("count"))
												.from(DIMENSION_COMBINATIONS)
												.groupBy(DIMENSION_COMBINATIONS.AGGREGATE_ID)
												.asTable();
		
		DimensionCombinations[] aliases = new DimensionCombinations[componentCount];
		for (int i = 0; i < componentCount; i++) {
			aliases[i] = DIMENSION_COMBINATIONS.as("dimension_combination_"+i);
		}
		
		SelectQuery<Record> joinQuery = dslContext.selectQuery();
		joinQuery.addSelect(aliases[0].AGGREGATE_ID);
		joinQuery.addFrom(aliases[0]);
		
		for (int i = 1; i < componentCount; i++) {
			joinQuery.addJoin(aliases[i], JoinType.LEFT_OUTER_JOIN, aliases[0].AGGREGATE_ID.equal(aliases[i].AGGREGATE_ID));
		}
		
		for (int i = 0; i < componentCount; i++) {
			joinQuery.addConditions(aliases[i].COMPONENT_ID.equal(combination.get(i).getId()));
		}
		
		Table<Record> joinTable = joinQuery.asTable();
		
		Result<Record1<Long>> result = dslContext
										.select(query.field("aggregate_id").cast(Long.class))
										.from(query)
										.leftOuterJoin(joinTable)
										.on(query.field(DIMENSION_COMBINATIONS.AGGREGATE_ID).equal(joinTable.field(DIMENSION_COMBINATIONS.AGGREGATE_ID)))
										.where(query.field("count").cast(Integer.class).equal(combination.size()))
										.fetch();
		
		if (result.size() != 1) {
			return -1;
		}
		
		return result.getValue(0, DIMENSION_COMBINATIONS.AGGREGATE_ID);
	}

	
	@Override
	public boolean dimensionHasRecords(long dimId) {
		int cnt = dslContext
					.select()
					.from(REFERENCE_OBJECTS)
					.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
					.fetchCount();
		return cnt == 0 ? false : true;
	}
	
	@Override
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId) {
		int cnt = dslContext
					.select()
					.from(REFERENCE_OBJECTS)
					.leftOuterJoin(REFERENCE_OBJECT_HIERARCHIES)
					.on(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID))
					.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
						.and(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(refObjId))
					.fetchCount();
		return cnt == 0 ? false : true;
	}
	
	
	@Override
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId) {
		Map<Long, ReferenceObject> refObjMap = dslContext
												.select(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, REFERENCE_OBJECTS.DIMENSION_ID, REFERENCE_OBJECTS.NAME)
												.from(REFERENCE_OBJECTS)
												.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
												.fetch()
												.intoMap(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, ReferenceObject.class);
		return new TreeMap<Long, ReferenceObject>(refObjMap);
	}
	
	@Override
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId) {
		Map<Long, ReferenceObject> refObjMap = dslContext
													.select(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, REFERENCE_OBJECTS.DIMENSION_ID, REFERENCE_OBJECTS.NAME)
													.from(REFERENCE_OBJECTS)
													.leftOuterJoin(REFERENCE_OBJECT_HIERARCHIES)
													.on(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID))
													.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
														.and(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(refObjId))
													.fetch()
													.intoMap(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, ReferenceObject.class);
		return new TreeMap<Long, ReferenceObject>(refObjMap);
	}

	
	@Override
	public TreeMap<Long, RatioCategory> loadRatioCategories() {
		Map<Long, RatioCategory> catMap = dslContext
											.select(RATIO_CATEGORIES.CATEGORY_ID, RATIO_CATEGORIES.NAME)
											.from(RATIO_CATEGORIES)
											.fetch()
											.intoMap(RATIO_CATEGORIES.CATEGORY_ID, RatioCategory.class);
		return new TreeMap<Long, RatioCategory>(catMap);
	}
	
	@Override
	public TreeMap<Long, Ratio> loadRatios() {
		Map<Long, Ratio> ratioMap = dslContext
										.select(RATIOS.RATIO_ID, RATIOS.NAME, RATIO_CATEGORIES.NAME)
										.from(RATIOS
										.leftOuterJoin(RATIO_CATEGORIES)
										.on(RATIOS.CATEGORY_ID.equal(RATIO_CATEGORIES.CATEGORY_ID)))
										.fetch()
										.intoMap(RATIOS.RATIO_ID, Ratio.class);
		return new TreeMap<Long, Ratio>(ratioMap);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadRatioRelations() {
		List<Entry<Long, Long>> hierarchyList = dslContext
													.select(RATIO_RELATIONS.DEPENDENT_ID, RATIO_RELATIONS.DEPENDENCY_ID)
													.from(RATIO_RELATIONS)
													.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<Entry<Long, Long>>(hierarchyList);
	}	
}
