package genericdwh.dataobjects.ratio;

import genericdwh.dataobjects.DataObjectCategory;

public class RatioCategory extends DataObjectCategory {
	
	public static RatioCategory NO_RATIO_CATEGORY = new RatioCategory(0, "Uncategorized");
	static { NO_RATIO_CATEGORY.initProperties();	}

	public RatioCategory(long id, String name) {
		super(id, name);
	}
}
