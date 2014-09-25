package genericdwh.gui.general;

public class ValidationMessages {
	/* Reference Objects */
	public static final String REFERENCE_OBJECT_NO_DIMENSION = "Reference Objects must have a Dimension!";
	
	/* Hierarchies */
	public static final String HIERARCHY_MIN_2_OBJECTS = "Hierarchies need at least 2 levels!";
	public static final String HIERARCHY_DUPLICATE_LEVEL = "A Hierarchy cannot contain duplicate levels!";

	/* Combinations */
	public static final String COMBINATION_MIN_2_OBJECTS = "Combinations need at least 2 components!";
	public static final String COMBINATION_DUPLICATE_COMPONENT = "A Combination cannot contain duplicate components!";
		
	/* Facts */
	public static final String FACT_NO_RATIO = "Facts must have a Ratio!";
	public static final String FACT_NO_REFERENCE_OBJECT = "Facts must have a Reference Object";
}
