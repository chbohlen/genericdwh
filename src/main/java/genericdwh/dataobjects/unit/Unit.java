package genericdwh.dataobjects.unit;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;
import genericdwh.dataobjects.DataObject;

public class Unit extends DataObject {
	
	@Getter @Setter private String symbol;
	
	public static Unit NO_UNIT = new Unit(0, "No Unit", "");
	static { NO_UNIT.initProperties();	}

	public Unit(long id, String name, String symbol) {
		super(id, name);
		
		this.symbol = symbol;
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		setSymbolProperty(symbol);
	}
	
	@Getter private StringProperty symbolProperty = new SimpleStringProperty();
	public void setSymbolProperty(String symbol) { symbolProperty.set(symbol); };
}
