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
public class DimensionCombinationRecord extends org.jooq.impl.UpdatableRecordImpl<genericdwh.db.model.tables.records.DimensionCombinationRecord> implements org.jooq.Record2<java.lang.Long, java.lang.Long> {

	private static final long serialVersionUID = 1718946362;

	/**
	 * Setter for <code>genericdwh.dimension_combination.aggregate_id</code>.
	 */
	public void setAggregateId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>genericdwh.dimension_combination.aggregate_id</code>.
	 */
	public java.lang.Long getAggregateId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>genericdwh.dimension_combination.component_id</code>.
	 */
	public void setComponentId(java.lang.Long value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>genericdwh.dimension_combination.component_id</code>.
	 */
	public java.lang.Long getComponentId() {
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
	public org.jooq.Field<java.lang.Long> field1() {
		return genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION.AGGREGATE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public org.jooq.Field<java.lang.Long> field2() {
		return genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION.COMPONENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public java.lang.Long value1() {
		return getAggregateId();
	}

	/**
	 * {@inheritDoc}
	 */
	public java.lang.Long value2() {
		return getComponentId();
	}

	/**
	 * {@inheritDoc}
	 */
	public DimensionCombinationRecord value1(java.lang.Long value) {
		setAggregateId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public DimensionCombinationRecord value2(java.lang.Long value) {
		setComponentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public DimensionCombinationRecord values(java.lang.Long value1, java.lang.Long value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached DimensionCombinationRecord
	 */
	public DimensionCombinationRecord() {
		super(genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION);
	}

	/**
	 * Create a detached, initialised DimensionCombinationRecord
	 */
	public DimensionCombinationRecord(java.lang.Long aggregateId, java.lang.Long componentId) {
		super(genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION);

		setValue(0, aggregateId);
		setValue(1, componentId);
	}
}
