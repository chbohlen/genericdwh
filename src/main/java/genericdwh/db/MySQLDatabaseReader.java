package genericdwh.db;

import static genericdwh.db.model.tables.Dimension.DIMENSION;
import static genericdwh.db.model.tables.DimensionCategory.DIMENSION_CATEGORY;
import static genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION;
import static genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY;
import static genericdwh.db.model.tables.ReferenceObject.REFERENCE_OBJECT;
import static genericdwh.db.model.tables.ReferenceObjectHierarchy.REFERENCE_OBJECT_HIERARCHY;
import genericdwh.dataobjects.Dimension;
import genericdwh.dataobjects.ReferenceObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.RecordMapper;

public class MySQLDatabaseReader implements DatabaseReader {
	private DSLContext create;
	
	public TreeMap<Long, Dimension> loadDimensions() {
		Map<Long, Dimension> dimMap = create.select(DIMENSION.ID, DIMENSION.NAME, DIMENSION_CATEGORY.NAME)
												.from(DIMENSION
													.leftOuterJoin(DIMENSION_CATEGORY)
													.on(DIMENSION.CATEGORY_ID.equal(DIMENSION_CATEGORY.ID)))
												.fetch()
												.intoMap(DIMENSION.ID, Dimension.class);
		return new TreeMap<Long, Dimension>(dimMap);
	}
	
	public ArrayList<Entry<Long, Long>> loadDimensionHierachies() {
		List<Entry<Long, Long>> hierarchyList = create.select(DIMENSION_HIERARCHY.PARENT_ID, DIMENSION_HIERARCHY.CHILD_ID)
														.from(DIMENSION_HIERARCHY)
														.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<Entry<Long, Long>>(hierarchyList);
	}
	
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations() {
		List<Entry<Long, Long>> combinationList = create.select(DIMENSION_COMBINATION.AGGREGATE_ID, DIMENSION_COMBINATION.COMPONENT_ID)
															.from(DIMENSION_COMBINATION)
															.fetch(new EntryLongLongRecordMapper());
		return new ArrayList<Entry<Long, Long>>(combinationList);
	}
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDim(long dimId) {
		Map<Long, ReferenceObject> refObjMap = create.select(REFERENCE_OBJECT.ID, REFERENCE_OBJECT.DIMENSION_ID, REFERENCE_OBJECT.NAME)
														.from(REFERENCE_OBJECT)
														.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
														.fetch()
														.intoMap(REFERENCE_OBJECT.ID, ReferenceObject.class);
		return new TreeMap<Long, ReferenceObject>(refObjMap);
	}
	
	public TreeMap<Long, ReferenceObject> loadRefObjsForDimAndRefObjParent(long dimId, long refObjId) {
		Map<Long, ReferenceObject> refObjMap = create.select(REFERENCE_OBJECT.ID, REFERENCE_OBJECT.DIMENSION_ID, REFERENCE_OBJECT.NAME)
														.from(REFERENCE_OBJECT)
															.leftOuterJoin(REFERENCE_OBJECT_HIERARCHY)
															.on(REFERENCE_OBJECT.ID.equal(REFERENCE_OBJECT_HIERARCHY.CHILD_ID))
														.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
															.and(REFERENCE_OBJECT_HIERARCHY.PARENT_ID.equal(refObjId))
														.fetch()
														.intoMap(REFERENCE_OBJECT.ID, ReferenceObject.class);
		return new TreeMap<Long, ReferenceObject>(refObjMap);
	}
	
	public boolean dimensionHasRecords(long dimId) {
		int cnt = create.select()
							.from(REFERENCE_OBJECT)
							.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
							.fetchCount();
		return cnt == 0 ? false : true;
	}
	
	public boolean dimensionAndRefObjParentHaveRecords(long dimId, long refObjId) {
		int cnt = create.select()
							.from(REFERENCE_OBJECT)
								.leftOuterJoin(REFERENCE_OBJECT_HIERARCHY)
								.on(REFERENCE_OBJECT.ID.equal(REFERENCE_OBJECT_HIERARCHY.CHILD_ID))
							.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dimId))
								.and(REFERENCE_OBJECT_HIERARCHY.PARENT_ID.equal(refObjId))
							.fetchCount();
		return cnt == 0 ? false : true;
	}
	
	public void setDSLContext(DSLContext context) {
		this.create = context;
	}
}
