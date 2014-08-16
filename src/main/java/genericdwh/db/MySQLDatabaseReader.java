package genericdwh.db;

import static genericdwh.db.model.tables.Dimension.DIMENSION;
import static genericdwh.db.model.tables.DimensionCategory.DIMENSION_CATEGORY;
import static genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION;
import static genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY;
import static genericdwh.db.model.tables.ReferenceObject.REFERENCE_OBJECT;
import genericdwh.dataobjects.Dimension;
import genericdwh.dataobjects.ReferenceObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

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
														.fetch(new EntryLongLongMapper());
		return new ArrayList<Entry<Long, Long>>(hierarchyList);
	}
	
	public ArrayList<Entry<Long, Long>> loadDimensionCombinations() {
		List<Entry<Long, Long>> combinationList = create.select(DIMENSION_COMBINATION.AGGREGATE_ID, DIMENSION_COMBINATION.COMPONENT_ID)
															.from(DIMENSION_COMBINATION)
															.fetch(new EntryLongLongMapper());
		return new ArrayList<Entry<Long, Long>>(combinationList);
	}
	
	public boolean dimensionHasRecords(long id) {
		int cnt = create.select()
							.from(REFERENCE_OBJECT)
							.where(REFERENCE_OBJECT.DIMENSION_ID.equal(id))
							.fetchCount();
		return cnt == 0 ? false : true;
	}
	
	public TreeMap<Long, ReferenceObject> loadReferenceObjectsForDimension(Dimension dim) {
		Map<Long, ReferenceObject> refObjMap = create.select(REFERENCE_OBJECT.ID, REFERENCE_OBJECT.DIMENSION_ID, REFERENCE_OBJECT.NAME)
														.from(REFERENCE_OBJECT)
														.where(REFERENCE_OBJECT.DIMENSION_ID.equal(dim.getId()))
														.fetch()
														.intoMap(REFERENCE_OBJECT.ID, ReferenceObject.class);
		return new TreeMap<Long, ReferenceObject>(refObjMap);
	}
	
	private class EntryLongLongMapper implements RecordMapper<Record2<Long, Long>, Entry<Long, Long>> {
		public Entry<Long, Long> map(Record2<Long, Long> record) {
			return new AbstractMap.SimpleEntry<Long, Long>((Long)record.getValue(0), (Long)record.getValue(1));
		}
	}
	
	public void setDSLContext(DSLContext context) {
		this.create = context;
	}
}
