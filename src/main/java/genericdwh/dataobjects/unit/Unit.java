package genericdwh.dataobjects.unit;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;

public class Unit extends DataObject {
	
	@Getter private String symbol;

	public Unit(long id, String name, String symbol) {
		super(id, name);
		
		this.symbol = symbol;
	}

	@Override
	public Unit clone() {
		Unit newUnit = new Unit(this.id, this.name, this.symbol);
		return newUnit;
	}
}
