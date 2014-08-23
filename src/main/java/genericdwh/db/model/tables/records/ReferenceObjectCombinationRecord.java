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
public class ReferenceObjectCombinationRecord extends org.jooq.impl.UpdatableRecordImpl<genericdwh.db.model.tables.records.ReferenceObjectCombinationRecord> implements org.jooq.Record2<java.lang.Long, java.lang.Long> {

	private static final long serialVersionUID = 1591155250;

	/**
	 * Setter for <code>genericdwh.reference_object_combination.aggregate_id</code>.
	 */
	public void setAggregateId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>genericdwh.reference_object_combination.aggregate_id</code>.
	 */
	public java.lang.Long getAggregateId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>genericdwh.reference_object_combination.component_id</code>.
	 */
	public void setComponentId(java.lang.Long value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>genericdwh.reference_object_combination.component_id</code>.
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
		return genericdwh.db.model.tables.ReferenceObjectCombination.REFERENCE_OBJECT_COMBINATION.AGGREGATE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	public org.jooq.Field<java.lang.Long> field2() {
		return genericdwh.db.model.tables.ReferenceObjectCombination.REFERENCE_OBJECT_COMBINATION.COMPONENT_ID;
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
	public ReferenceObjectCombinationRecord value1(java.lang.Long value) {
		setAggregateId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ReferenceObjectCombinationRecord value2(java.lang.Long value) {
		setComponentId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	public ReferenceObjectCombinationRecord values(java.lang.Long value1, java.lang.Long value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ReferenceObjectCombinationRecord
	 */
	public ReferenceObjectCombinationRecord() {
		super(genericdwh.db.model.tables.ReferenceObjectCombination.REFERENCE_OBJECT_COMBINATION);
	}

	/**
	 * Create a detached, initialised ReferenceObjectCombinationRecord
	 */
	public ReferenceObjectCombinationRecord(java.lang.Long aggregateId, java.lang.Long componentId) {
		super(genericdwh.db.model.tables.ReferenceObjectCombination.REFERENCE_OBJECT_COMBINATION);

		setValue(0, aggregateId);
		setValue(1, componentId);
	}
}