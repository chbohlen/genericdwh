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
public class ReferenceObjectCombinations extends org.jooq.impl.TableImpl<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord> {

	private static final long serialVersionUID = -2040820611;

	/**
	 * The singleton instance of <code>reference_object_combinations</code>
	 */
	public static final genericdwh.db.model.tables.ReferenceObjectCombinations REFERENCE_OBJECT_COMBINATIONS = new genericdwh.db.model.tables.ReferenceObjectCombinations();

	/**
	 * The class holding records for this type
	 */
	@Override
	public java.lang.Class<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord> getRecordType() {
		return genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord.class;
	}

	/**
	 * The column <code>reference_object_combinations.aggregate_id</code>.
	 */
	public final org.jooq.TableField<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord, java.lang.Long> AGGREGATE_ID = createField("aggregate_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * The column <code>reference_object_combinations.component_id</code>.
	 */
	public final org.jooq.TableField<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord, java.lang.Long> COMPONENT_ID = createField("component_id", org.jooq.impl.SQLDataType.BIGINT.nullable(false), this, "");

	/**
	 * Create a <code>reference_object_combinations</code> table reference
	 */
	public ReferenceObjectCombinations() {
		this("reference_object_combinations", null);
	}

	/**
	 * Create an aliased <code>reference_object_combinations</code> table reference
	 */
	public ReferenceObjectCombinations(java.lang.String alias) {
		this(alias, genericdwh.db.model.tables.ReferenceObjectCombinations.REFERENCE_OBJECT_COMBINATIONS);
	}

	private ReferenceObjectCombinations(java.lang.String alias, org.jooq.Table<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord> aliased) {
		this(alias, aliased, null);
	}

	private ReferenceObjectCombinations(java.lang.String alias, org.jooq.Table<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord> aliased, org.jooq.Field<?>[] parameters) {
		super(alias, genericdwh.db.model.DefaultSchema.DEFAULT_SCHEMA, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.jooq.UniqueKey<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord> getPrimaryKey() {
		return genericdwh.db.model.Keys.KEY_REFERENCE_OBJECT_COMBINATIONS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.UniqueKey<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord>> getKeys() {
		return java.util.Arrays.<org.jooq.UniqueKey<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord>>asList(genericdwh.db.model.Keys.KEY_REFERENCE_OBJECT_COMBINATIONS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public java.util.List<org.jooq.ForeignKey<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord, ?>> getReferences() {
		return java.util.Arrays.<org.jooq.ForeignKey<genericdwh.db.model.tables.records.ReferenceObjectCombinationsRecord, ?>>asList(genericdwh.db.model.Keys.REFERENCE_OBJECT_COMBINATIONS_IBFK_1, genericdwh.db.model.Keys.REFERENCE_OBJECT_COMBINATIONS_IBFK_2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public genericdwh.db.model.tables.ReferenceObjectCombinations as(java.lang.String alias) {
		return new genericdwh.db.model.tables.ReferenceObjectCombinations(alias, this);
	}

	/**
	 * Rename this table
	 */
	public genericdwh.db.model.tables.ReferenceObjectCombinations rename(java.lang.String name) {
		return new genericdwh.db.model.tables.ReferenceObjectCombinations(name, null);
	}
}