package genericdwh.dataobjects.ratio;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;

public class Ratio extends DataObject {
	
	@Getter private long id;
	@Getter private String category;
	
	public Ratio(long id, String name, String category) {
		super(name);
		
		this.id = id;
		this.category = category;
	}
}
