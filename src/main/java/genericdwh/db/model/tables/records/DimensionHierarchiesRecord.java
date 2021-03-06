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
public class DimensionHierarchiesRecord extends org.jooq.impl.UpdatableRecordImpl<genericdwh.db.model.tables.records.DimensionHierarchiesRecord> implements org.jooq.Record2<java.lang.Long, java.lang.Long> {

	private static final long serialVersionUID = -2049525792;

	/**
	 * Setter for <code>dimension_hierarchies.parent_id</code>.
	 */
	public void setParentId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>dimension_hierarchies.parent_id</code>.
	 */
	public java.lang.Long getParentId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>dimension_hierarchies.child_id</code>.
	 */
	public void setChildId(java.lang.Long value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>dimension_hierarchies.child_id</code>.
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
		return genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES.PARENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field2() {
		return genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES.CHILD_ID;
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
	public DimensionHierarchiesRecord value1(java.lang.Long value) {
		setParentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DimensionHierarchiesRecord value2(java.lang.Long value) {
		setChildId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DimensionHierarchiesRecord values(java.lang.Long value1, java.lang.Long value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached DimensionHierarchiesRecord
	 */
	public DimensionHierarchiesRecord() {
		super(genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES);
	}

	/**
	 * Create a detached, initialised DimensionHierarchiesRecord
	 */
	public DimensionHierarchiesRecord(java.lang.Long parentId, java.lang.Long childId) {
		super(genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES);

		setValue(0, parentId);
		setValue(1, childId);
	}
}
