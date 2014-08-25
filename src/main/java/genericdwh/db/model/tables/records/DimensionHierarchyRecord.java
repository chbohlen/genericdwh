/**
 * This class is generated by jOOQ
 */
package genericdwh.db.model.tables.records;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DimensionHierarchyRecord extends org.jooq.impl.UpdatableRecordImpl<genericdwh.db.model.tables.records.DimensionHierarchyRecord> implements org.jooq.Record2<java.lang.Long, java.lang.Long> {

	private static final long serialVersionUID = -674251518;

	/**
	 * Setter for <code>dimension_hierarchy.parent_id</code>.
	 */
	public void setParentId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>dimension_hierarchy.parent_id</code>.
	 */
	public java.lang.Long getParentId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>dimension_hierarchy.child_id</code>.
	 */
	public void setChildId(java.lang.Long value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>dimension_hierarchy.child_id</code>.
	 */
	public java.lang.Long getChildId() {
		return (java.lang.Long) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record2<java.lang.Long, java.lang.Long> key() {
		return (org.jooq.Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.Long, java.lang.Long> fieldsRow() {
		return (org.jooq.Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.Long, java.lang.Long> valuesRow() {
		return (org.jooq.Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field1() {
		return genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY.PARENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field2() {
		return genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY.CHILD_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value1() {
		return getParentId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value2() {
		return getChildId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DimensionHierarchyRecord value1(java.lang.Long value) {
		setParentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DimensionHierarchyRecord value2(java.lang.Long value) {
		setChildId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DimensionHierarchyRecord values(java.lang.Long value1, java.lang.Long value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached DimensionHierarchyRecord
	 */
	public DimensionHierarchyRecord() {
		super(genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY);
	}

	/**
	 * Create a detached, initialised DimensionHierarchyRecord
	 */
	public DimensionHierarchyRecord(java.lang.Long parentId, java.lang.Long childId) {
		super(genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY);

		setValue(0, parentId);
		setValue(1, childId);
	}
}
