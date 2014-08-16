/**
 * This class is generated by jOOQ
 */
package genericdwh.db.model.tables;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DimensionHierarchy extends org.jooq.impl.TableImpl<genericdwh.db.model.tables.records.DimensionHierarchyRecord> {

	private static final long serialVersionUID = -613373854;

	/**
	 * The singleton instance of <code>genericdwh.dimension_hierarchy</code>
	 */
	public static final genericdwh.db.model.tables.DimensionHierarchy DIMENSION_HIERARCHY = new genericdwh.db.model.tables.DimensionHierarchy();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<genericdwh.db.model.tables.records.DimensionHierarchyRecord> getRecordType() {
		return genericdwh.db.model.tables.records.DimensionHierarchyRecord.class;
	}

	/**
	 * The column <code>genericdwh.dimension_hierarchy.parent_id</code>.
	 */
	public final org.jooq.TableField<genericdwh.db.model.tables.records.DimensionHierarchyRecord, java.lang.Long> PARENT_ID = createField("parent_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>genericdwh.dimension_hierarchy.child_id</code>.
	 */
	public final org.jooq.TableField<genericdwh.db.model.tables.records.DimensionHierarchyRecord, java.lang.Long> CHILD_ID = createField("child_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * Create a <code>genericdwh.dimension_hierarchy</code> table reference
	 */
	public DimensionHierarchy() {
		this("dimension_hierarchy", null);
	}

	/**
	 * Create an aliased <code>genericdwh.dimension_hierarchy</code> table reference
	 */
	public DimensionHierarchy(java.lang.String alias) {
		this(alias, genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY);
	}

	private DimensionHierarchy(java.lang.String alias, org.jooq.Table<genericdwh.db.model.tables.records.DimensionHierarchyRecord> aliased) {
		this(alias, aliased, null);
	}

	private DimensionHierarchy(java.lang.String alias, org.jooq.Table<genericdwh.db.model.tables.records.DimensionHierarchyRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, genericdwh.db.model.Genericdwh.GENERICDWH, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<genericdwh.db.model.tables.records.DimensionHierarchyRecord> getPrimaryKey() {
		return genericdwh.db.model.Keys.KEY_DIMENSION_HIERARCHY_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<genericdwh.db.model.tables.records.DimensionHierarchyRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<genericdwh.db.model.tables.records.DimensionHierarchyRecord>>asList(genericdwh.db.model.Keys.KEY_DIMENSION_HIERARCHY_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<genericdwh.db.model.tables.records.DimensionHierarchyRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<genericdwh.db.model.tables.records.DimensionHierarchyRecord, ?>>asList(genericdwh.db.model.Keys.DIMENSION_HIERARCHY_IBFK_1, genericdwh.db.model.Keys.DIMENSION_HIERARCHY_IBFK_2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public genericdwh.db.model.tables.DimensionHierarchy as(java.lang.String alias) {
		return new genericdwh.db.model.tables.DimensionHierarchy(alias, this);
	}

	/**
	 * Rename this table
	 */
	public genericdwh.db.model.tables.DimensionHierarchy rename(java.lang.String name) {
		return new genericdwh.db.model.tables.DimensionHierarchy(name, null);
	}
}
