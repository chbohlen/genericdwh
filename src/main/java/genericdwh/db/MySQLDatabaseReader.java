package genericdwh.db;

import static genericdwh.db.model.tables.DimensionCategories.DIMENSION_CATEGORIES;
import static genericdwh.db.model.tables.DimensionCombinations.DIMENSION_COMBINATIONS;
import static genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES;
import static genericdwh.db.model.tables.Dimensions.DIMENSIONS;
import static genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES;
import static genericdwh.db.model.tables.RatioRelations.RATIO_RELATIONS;
import static genericdwh.db.model.tables.Ratios.RATIOS;
import static genericdwh.db.model.tables.ReferenceObjectCombinations.REFERENCE_OBJECT_COMBINATIONS;
import static genericdwh.db.model.tables.ReferenceObjectHierarchies.REFERENCE_OBJECT_HIERARCHIES;
import static genericdwh.db.model.tables.ReferenceObjects.REFERENCE_OBJECTS;
import static genericdwh.db.model.tables.Facts.FACTS;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.model.tables.DimensionCombinations;
import genericdwh.db.model.tables.ReferenceObjectCombinations;

import java.util.AbstractMap.SimpleEntry;
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
import org.jooq.Result;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.impl.DSL;

public class MySQLDatabaseReader implements DatabaseReader {
	
	@Setter private DSLContext dslContext;
	
	@Override
	public TreeMap<Long, DimensionCategory> loadDimensionCategories() {
		Map<Long, DimensionCategory> result = dslContext
												.select(DIMENSION_CATEGORIES.CATEGORY_ID, DIMENSION_CATEGORIES.NAME)
												.from(DIMENSION_CATEGORIES)
												.fetch()
												.intoMap(DIMENSION_CATEGORIES.CATEGORY_ID, DimensionCategory.class);
		return new TreeMap<>(result);
	}
	
	@Override
	public TreeMap<Long, Dimension> loadDimensions() {
		Map<Long, Dimension> result = dslContext
										.select(DIMENSIONS.DIMENSION_ID, DIMENSIONS.NAME, DIMENSION_CATEGORIES.NAME)
										.from(DIMENSIONS
										.leftOuterJoin(DIMENSION_CATEGORIES)
											.on(DIMENSIONS.CATEGORY_ID.equal(DIMENSION_CATEGORIES.CATEGORY_ID)))
										.fetch()
										.intoMap(DIMENSIONS.DIMENSION_ID, Dimension.class);
		return new TreeMap<>(result);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadDimensionHierachies() {
		List<Entry<Long, Long>> result = dslContext
											.select(DIMENSION_HIERARCHIES.PARENT_ID, DIMENSION_HIERARCHIES.CHILD_ID)
											.from(DIMENSION_HIERARCHIES)
											.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<>(result);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations() {
		List<Entry<Long, Long>> result = dslContext
											.select(DIMENSION_COMBINATIONS.AGGREGATE_ID, DIMENSION_COMBINATIONS.COMPONENT_ID)
											.from(DIMENSION_COMBINATIONS)
											.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<>(result);
	}
	
	@Override
	public TreeMap<Long, RatioCategory> loadRatioCategories() {
		Map<Long, RatioCategory> result = dslContext
											.select(RATIO_CATEGORIES.CATEGORY_ID, RATIO_CATEGORIES.NAME)
											.from(RATIO_CATEGORIES)
											.fetch()
											.intoMap(RATIO_CATEGORIES.CATEGORY_ID, RatioCategory.class);
		return new TreeMap<>(result);
	}
	
	@Override
	public TreeMap<Long, Ratio> loadRatios() {
		Map<Long, Ratio> result = dslContext
									.select(RATIOS.RATIO_ID, RATIOS.NAME, RATIO_CATEGORIES.NAME)
									.from(RATIOS
									.leftOuterJoin(RATIO_CATEGORIES)
										.on(RATIOS.CATEGORY_ID.equal(RATIO_CATEGORIES.CATEGORY_ID)))
									.fetch()
									.intoMap(RATIOS.RATIO_ID, Ratio.class);
		return new TreeMap<>(result);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadRatioRelations() {
		List<Entry<Long, Long>> result = dslContext
											.select(RATIO_RELATIONS.DEPENDENT_ID, RATIO_RELATIONS.DEPENDENCY_ID)
											.from(RATIO_RELATIONS)
											.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<>(result);
	}
	

	@Override
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId) {
		Map<Long, ReferenceObject> result = dslContext
												.select(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, REFERENCE_OBJECTS.DIMENSION_ID, REFERENCE_OBJECTS.NAME)
												.from(REFERENCE_OBJECTS)
												.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
												.fetch()
												.intoMap(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, ReferenceObject.class);
		return new TreeMap<>(result);
	}
	
	@Override
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId) {
		Map<Long, ReferenceObject> result = dslContext
												.select(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, REFERENCE_OBJECTS.DIMENSION_ID, REFERENCE_OBJECTS.NAME)
												.from(REFERENCE_OBJECTS)
												.leftOuterJoin(REFERENCE_OBJECT_HIERARCHIES)
													.on(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID))
												.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
													.and(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(refObjId))
												.fetch()
												.intoMap(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID, ReferenceObject.class);
		return new TreeMap<>(result);
	}
	
	
	@Override
	public boolean dimensionHasRecords(long dimId) {
		boolean isEmpty = dslContext
							.select()
							.from(REFERENCE_OBJECTS)
							.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
							.fetch()
							.isEmpty();
		return !isEmpty;
	}
	
	@Override
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId) {
		boolean isEmpty = dslContext
							.select()
							.from(REFERENCE_OBJECTS)
							.leftOuterJoin(REFERENCE_OBJECT_HIERARCHIES)
								.on(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECT_HIERARCHIES.CHILD_ID))
							.where(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
								.and(REFERENCE_OBJECT_HIERARCHIES.PARENT_ID.equal(refObjId))
							.fetch()
							.isEmpty();
		return !isEmpty;
	}
	
	
	@Override
	public long findDimAggregateId(Long[] componentIds) {
		int componentCount = componentIds.length;
		
		Table<Record1<Long>> query = dslContext
										.select(DIMENSION_COMBINATIONS.AGGREGATE_ID)
										.from(DIMENSION_COMBINATIONS)
										.groupBy(DIMENSION_COMBINATIONS.AGGREGATE_ID)
										.having(DSL.count().equal(componentCount))
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
			joinQuery.addConditions(aliases[i].COMPONENT_ID.equal(componentIds[i]));
		}
		
		Table<Record> joinTable = joinQuery.asTable();
		
		Result<Record1<Long>> result = dslContext
										.select(query.field("aggregate_id").cast(Long.class))
										.from(query)
										.join(joinTable)
											.on(query.field(DIMENSION_COMBINATIONS.AGGREGATE_ID).equal(joinTable.field(DIMENSION_COMBINATIONS.AGGREGATE_ID)))
										.fetch();
		
		if (result.size() != 1) {
			return -1;
		}
		
		return result.getValue(0, DIMENSION_COMBINATIONS.AGGREGATE_ID);
	}
		
	@Override
	public long findRefObjAggregateId(Long[] componentIds) {
		int componentCount = componentIds.length;
		
		Table<Record1<Long>> query = dslContext
										.select(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID)
										.from(REFERENCE_OBJECT_COMBINATIONS)
										.groupBy(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID)
										.having(DSL.count().equal(componentCount))
										.asTable();
		
		ReferenceObjectCombinations[] aliases = new ReferenceObjectCombinations[componentCount];
		for (int i = 0; i < componentCount; i++) {
			aliases[i] = REFERENCE_OBJECT_COMBINATIONS.as("reference_object_combination_"+i);
		}
		
		SelectQuery<Record> joinQuery = dslContext.selectQuery();
		joinQuery.addSelect(aliases[0].AGGREGATE_ID);
		joinQuery.addFrom(aliases[0]);
		
		for (int i = 1; i < componentCount; i++) {
			joinQuery.addJoin(aliases[i], JoinType.LEFT_OUTER_JOIN, aliases[0].AGGREGATE_ID.equal(aliases[i].AGGREGATE_ID));
		}
		
		for (int i = 0; i < componentCount; i++) {
			joinQuery.addConditions(aliases[i].COMPONENT_ID.equal(componentIds[i]));
		}
		
		Table<Record> joinTable = joinQuery.asTable();
		
		Result<Record1<Long>> result = dslContext
										.select(query.field("aggregate_id").cast(Long.class))
										.from(query)
										.join(joinTable)
											.on(query.field(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID).equal(joinTable.field(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID)))
										.fetch();
		
		if (result.size() != 1) {
			return -1;
		}
		
		return result.getValue(0, REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID);
	}
	

	@Override
	public Entry<Long, Double> loadFactForSingleRefObj(long ratioId, long refObjId) {
		List<Entry<Long, Double>> result = dslContext.select(FACTS.REFERENCE_OBJECT_ID, FACTS.VALUE)
														.from(FACTS)
														.where(FACTS.RATIO_ID.equal(ratioId)
															.and(FACTS.REFERENCE_OBJECT_ID.equal(refObjId)))
														.fetch(new EntryLongDoubleMapper());	
		if (result.isEmpty()) {
			return null;
		}
		
		return result.get(0);
	}
	
	@Override
	public Entry<Long, Entry<Long[], Double>> loadFactForRefObjCombination(long ratioId, long refObjId) {
		Result<Record> result = dslContext.select()
											.from(FACTS)
											.join(REFERENCE_OBJECT_COMBINATIONS)
												.on(FACTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID))
											.where(FACTS.RATIO_ID.equal(ratioId)
												.and(FACTS.REFERENCE_OBJECT_ID.equal(refObjId)))
											.fetch();
		
		if (result.isEmpty()) {
			return null;
		}
		
		Long id = result.getValue(0, FACTS.REFERENCE_OBJECT_ID);
		Long[] combination = result.getValues(REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID).toArray(new Long[0]);
		Double value = result.getValue(0, FACTS.VALUE);
		
		return new SimpleEntry<>(id, new SimpleEntry<>(combination, value));
	}
	
	@Override
	public TreeMap<Long, Double> loadFactsForSingleDim(long ratioId, long dimId) {
		Map<Long, Double> result = dslContext.select()
									.from(FACTS)
									.join(REFERENCE_OBJECTS)
										.on(FACTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID))
									.where(FACTS.RATIO_ID.equal(ratioId))
										.and(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
									.fetch().intoMap(FACTS.REFERENCE_OBJECT_ID, FACTS.VALUE);
		
		if (result.isEmpty()) {
			return null;
		}
		
		return new TreeMap<>(result);
	}
	
	@Override
	public TreeMap<Long, Entry<Long[], Double>> loadFactsForDimCombination(long ratioId, long dimId) {
		Result<Record> result = dslContext.select()
									.from(FACTS)
									.join(REFERENCE_OBJECTS)
										.on(FACTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID))
									.join(REFERENCE_OBJECT_COMBINATIONS)
										.on(FACTS.REFERENCE_OBJECT_ID.equal(REFERENCE_OBJECT_COMBINATIONS.AGGREGATE_ID))
									.where(FACTS.RATIO_ID.equal(ratioId))
										.and(REFERENCE_OBJECTS.DIMENSION_ID.equal(dimId))
									.fetch();
		
		if (result.isEmpty()) {
			return null;
		}

		TreeMap<Long, Entry<Long[], Double>> resultMap = new TreeMap<>();
		for (Entry<Long, Result<Record>> currRecord : result.intoGroups(FACTS.REFERENCE_OBJECT_ID).entrySet()) {
			Long[] combination = currRecord.getValue().getValues(REFERENCE_OBJECT_COMBINATIONS.COMPONENT_ID).toArray(new Long[0]);
			Double value = currRecord.getValue().getValue(0, FACTS.VALUE);
			resultMap.put(currRecord.getKey(), new SimpleEntry<>(combination, value));
		}

		return resultMap;
	}
}
