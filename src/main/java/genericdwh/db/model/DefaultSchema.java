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
public class DefaultSchema extends org.jooq.impl.SchemaImpl {

	private static final long serialVersionUID = -2117327980;

	/**
	 * The singleton instance of <code></code>
	 */
	public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

	/**
	 * No further instances allowed
	 */
	private DefaultSchema() {
		super("");
	}

	@Override
	public final java.util.List<org.jooq.Table<?>> getTables() {
		java.util.List result = new java.util.ArrayList();
		result.addAll(getTables0());
		return result;
	}

	private final java.util.List<org.jooq.Table<?>> getTables0() {
		return java.util.Arrays.<org.jooq.Table<?>>asList(
			genericdwh.db.model.tables.Dimensions.DIMENSIONS,
			genericdwh.db.model.tables.DimensionCategories.DIMENSION_CATEGORIES,
			genericdwh.db.model.tables.DimensionCombinations.DIMENSION_COMBINATIONS,
			genericdwh.db.model.tables.DimensionHierarchies.DIMENSION_HIERARCHIES,
			genericdwh.db.model.tables.Ratios.RATIOS,
			genericdwh.db.model.tables.RatioCategories.RATIO_CATEGORIES,
			genericdwh.db.model.tables.RatioRelations.RATIO_RELATIONS,
			genericdwh.db.model.tables.ReferenceObjects.REFERENCE_OBJECTS,
			genericdwh.db.model.tables.ReferenceObjectCombinations.REFERENCE_OBJECT_COMBINATIONS,
			genericdwh.db.model.tables.ReferenceObjectHierarchies.REFERENCE_OBJECT_HIERARCHIES);
	}
}
