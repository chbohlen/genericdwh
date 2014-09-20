package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;

public class DimensionCategory extends DataObject {
	
	public static DimensionCategory NO_DIMENSION_CATEGORY = new DimensionCategory(0, "Uncategorized");
	static { NO_DIMENSION_CATEGORY.initProperties(); }
	
	public DimensionCategory(long id, String name) {
		super(id, name);
	}
}
