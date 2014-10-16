package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObjectCategory;

public class DimensionCategory extends DataObjectCategory {
	
	public static DimensionCategory NO_DIMENSION_CATEGORY = new DimensionCategory(0, "Uncategorized");
	static { NO_DIMENSION_CATEGORY.initProperties(); }
	
	public DimensionCategory(long id, String name) {
		super(id, name);
	}
}
