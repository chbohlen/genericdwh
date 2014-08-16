/**
 * This class is generated by jOOQ
 */
package genericdwh.db.model;

/**
 * This class is generated by jOOQ.
 */
@javax.annotation.Generated(value    = { "http://www.jooq.org", "3.4.1" },
                            comments = "This class is generated by jOOQ")
@java.lang.SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Genericdwh extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = -1888866256;

	/**
	 * The singleton instance of <code>genericdwh</code>
	 */
	public static final Genericdwh GENERICDWH = new Genericdwh();

	/**
	 * No further instances allowed
	 */
	private Genericdwh() {
		super("genericdwh");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			genericdwh.db.model.tables.Dimension.DIMENSION,
			genericdwh.db.model.tables.DimensionCategory.DIMENSION_CATEGORY,
			genericdwh.db.model.tables.DimensionCombination.DIMENSION_COMBINATION,
			genericdwh.db.model.tables.DimensionHierarchy.DIMENSION_HIERARCHY,
			genericdwh.db.model.tables.ReferenceObject.REFERENCE_OBJECT,
			genericdwh.db.model.tables.ReferenceObjectCombination.REFERENCE_OBJECT_COMBINATION,
			genericdwh.db.model.tables.ReferenceObjectHierarchy.REFERENCE_OBJECT_HIERARCHY);
	}
}
