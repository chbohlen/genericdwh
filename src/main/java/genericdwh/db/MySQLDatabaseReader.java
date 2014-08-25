package genericdwh.db;

import static genericdwh.db.model.tables.Dimension.DIMENSION;
import static genericdwh.db.model.tables.DimensionCategory.DIMENSION_CATEGORY;
import static genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION;
import static genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY;
import static genericdwh.db.model.tables.Ratio.RATIO;
import static genericdwh.db.model.tables.RatioCategory.RATIO_CATEGORY;
import static genericdwh.db.model.tables.ReferenceObject.REFERENCE_OBJECT;
import static genericdwh.db.model.tables.ReferenceObjectHierarchy.REFERENCE_OBJECT_HIERARCHY;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.model.tables.DimensionCombination;

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
												.select(DIMENSION_CATEGORY.ID, DIMENSION_CATEGORY.NAME)
												.from(DIMENSION_CATEGORY)
												.fetch()
												.intoMap(DIMENSION_CATEGORY.ID, DimensionCategory.class);
		return new TreeMap<Long, DimensionCategory>(catMap);
	}
	
	@Override
	public TreeMap<Long, Dimension> loadDimensions() {
		Map<Long, Dimension> dimMap = dslContext
										.select(DIMENSION.ID, DIMENSION.NAME, DIMENSION_CATEGORY.NAME)
										.from(DIMENSION
										.leftOuterJoin(DIMENSION_CATEGORY)
										.on(DIMENSION.CATEGORY_ID.equal(DIMENSION_CATEGORY.ID)))
										.fetch()
										.intoMap(DIMENSION.ID, Dimension.class);
		return new TreeMap<Long, Dimension>(dimMap);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadDimensionHierachies() {
		List<Entry<Long, Long>> hierarchyList = dslContext
													.select(DIMENSION_HIERARCHY.PARENT_ID, DIMENSION_HIERARCHY.CHILD_ID)
													.from(DIMENSION_HIERARCHY)
													.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<Entry<Long, Long>>(hierarchyList);
	}

	@Override
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations() {

		List<Entry<Long, Long>> combinationList = dslContext
													.select(DIMENSION_COMBINATION.AGGREGATE_ID, DIMENSION_COMBINATION.COMPONENT_ID)
													.from(DIMENSION_COMBINATION)
													.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<Entry<Long, Long>>(combinationList);
	}

	
	@Override
	public boolean dimensionHasRecords(long dimId) {
		int cnt = dslContext
					.select()
					.from(REFERENCE_OBJECT)
					.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
					.fetchCount();
		return cnt == 0 ? false : true;
	}
	
	@Override
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId) {
		int cnt = dslContext
					.select()
					.from(REFERENCE_OBJECT)
					.leftOuterJoin(REFERENCE_OBJECT_HIERARCHY)
					.on(REFERENCE_OBJECT.ID.equal(REFERENCE_OBJECT_HIERARCHY.CHILD_ID))
					.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
						.and(REFERENCE_OBJECT_HIERARCHY.PARENT_ID.equal(refObjId))
					.fetchCount();
		return cnt == 0 ? false : true;
	}
	
	
	@Override
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId) {
		Map<Long, ReferenceObject> refObjMap = dslContext
												.select(REFERENCE_OBJECT.ID, REFERENCE_OBJECT.DIMENSION_ID, REFERENCE_OBJECT.NAME)
												.from(REFERENCE_OBJECT)
												.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
												.fetch()
												.intoMap(REFERENCE_OBJECT.ID, ReferenceObject.class);
		return new TreeMap<Long, ReferenceObject>(refObjMap);
	}
	
	@Override
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId) {
		Map<Long, ReferenceObject> refObjMap = dslContext
													.select(REFERENCE_OBJECT.ID, REFERENCE_OBJECT.DIMENSION_ID, REFERENCE_OBJECT.NAME)
													.from(REFERENCE_OBJECT)
													.leftOuterJoin(REFERENCE_OBJECT_HIERARCHY)
													.on(REFERENCE_OBJECT.ID.equal(REFERENCE_OBJECT_HIERARCHY.CHILD_ID))
													.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
														.and(REFERENCE_OBJECT_HIERARCHY.PARENT_ID.equal(refObjId))
													.fetch()
													.intoMap(REFERENCE_OBJECT.ID, ReferenceObject.class);
		return new TreeMap<Long, ReferenceObject>(refObjMap);
	}

	
	@Override
	public TreeMap<Long, RatioCategory> loadRatioCategories() {
		Map<Long, RatioCategory> catMap = dslContext
											.select(RATIO_CATEGORY.ID, RATIO_CATEGORY.NAME)
											.from(RATIO_CATEGORY)
											.fetch()
											.intoMap(RATIO_CATEGORY.ID, RatioCategory.class);
		return new TreeMap<Long, RatioCategory>(catMap);
	}
	
	@Override
	public TreeMap<Long, Ratio> loadRatios() {
		Map<Long, Ratio> ratioMap = dslContext
										.select(RATIO.ID, RATIO.NAME, RATIO_CATEGORY.NAME)
										.from(RATIO
										.leftOuterJoin(RATIO_CATEGORY)
										.on(RATIO.CATEGORY_ID.equal(RATIO_CATEGORY.ID)))
										.fetch()
										.intoMap(RATIO.ID, Ratio.class);
		return new TreeMap<Long, Ratio>(ratioMap);
	}
	
	
	@Override
	public long findDimensionCombination(ArrayList<Dimension> combination) {
		int componentCount = combination.size();
		
		Table<Record2<Long, Integer>> query = dslContext
												.select(DIMENSION_COMBINATION.AGGREGATE_ID, DSL.count().as("count"))
												.from(DIMENSION_COMBINATION)
												.groupBy(DIMENSION_COMBINATION.AGGREGATE_ID)
												.asTable();
		
		DimensionCombination[] aliases = new DimensionCombination[componentCount];
		for (int i = 0; i < componentCount; i++) {
			aliases[i] = DIMENSION_COMBINATION.as("dimension_combination_"+i);
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
										.on(query.field(DIMENSION_COMBINATION.AGGREGATE_ID).equal(joinTable.field(DIMENSION_COMBINATION.AGGREGATE_ID)))
										.where(query.field("count").cast(Integer.class).equal(combination.size()))
										.fetch();
		
		if (result.size() != 1) {
			return -1;
		}
		
		return result.getValue(0, DIMENSION_COMBINATION.AGGREGATE_ID);
	}
}
