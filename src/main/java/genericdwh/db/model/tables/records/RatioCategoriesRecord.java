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
public class RatioCategoriesRecord extends org.jooq.impl.UpdatableRecordImpl<genericdwh.db.model.tables.records.RatioCategoriesRecord> implements org.jooq.Record2<java.lang.Long, java.lang.String> {

	private static final long serialVersionUID = -2013814154;

	/**
	 * Setter for <code>ratio_categories.category_id</code>.
	 */
	public void setCategoryId(java.lang.Long value) {
		setValue(0, value);
	}

	/**
	 * Getter for <code>ratio_categories.category_id</code>.
	 */
	public java.lang.Long getCategoryId() {
		return (java.lang.Long) getValue(0);
	}

	/**
	 * Setter for <code>ratio_categories.name</code>.
	 */
	public void setName(java.lang.String value) {
		setValue(1, value);
	}

	/**
	 * Getter for <code>ratio_categories.name</code>.
	 */
	public java.lang.String getName() {
		return (java.lang.String) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Record1<java.lang.Long> key() {
		return (org.jooq.Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.Long, java.lang.String> fieldsRow() {
		return (org.jooq.Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Row2<java.lang.Long, java.lang.String> valuesRow() {
		return (org.jooq.Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.Long> field1() {
		return genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES.CATEGORY_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.Field<java.lang.String> field2() {
		return genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.Long value1() {
		return getCategoryId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.lang.String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RatioCategoriesRecord value1(java.lang.Long value) {
		setCategoryId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RatioCategoriesRecord value2(java.lang.String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RatioCategoriesRecord values(java.lang.Long value1, java.lang.String value2) {
		return this;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached RatioCategoriesRecord
	 */
	public RatioCategoriesRecord() {
		super(genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES);
	}

	/**
	 * Create a detached, initialised RatioCategoriesRecord
	 */
	public RatioCategoriesRecord(java.lang.Long categoryId, java.lang.String name) {
		super(genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES);

		setValue(0, categoryId);
		setValue(1, name);
	}
}