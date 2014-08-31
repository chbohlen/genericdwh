package genericdwh.dataobjects.unit;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;

public class Unit extends DataObject {
	
	@Getter private String symbol;

	public Unit(long id, String name, String symbol) {
		super(id, name);
		
		this.symbol = symbol;
	}
}
