package genericdwh.dataobjects.dimension;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;

public class DimensionCategory extends DataObject {

	@Getter private long id;

	public DimensionCategory(long id, String name) {
		super(name);
		
		this.id = id;
	}

	@Override
	public DimensionCategory clone() {
		DimensionCategory newCat = new DimensionCategory(this.id, this.name);
		return newCat;
	}
}
