package genericdwh.dataobjects.ratio;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;

public class RatioCategory extends DataObject {
	
	@Getter private long id;

	public RatioCategory(long id, String name) {
		super(name);
		
		this.id = id;
	}

	@Override
	public RatioCategory clone() {
		RatioCategory newCat = new RatioCategory(this.id, this.name);
		return newCat;
	}
}
