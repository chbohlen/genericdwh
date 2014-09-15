package genericdwh.dataobjects;

import lombok.Getter;
import lombok.Setter;

public abstract class DataObject {
	
	@Getter protected long id;
	@Getter @Setter protected String name;
	
	@Getter @Setter private boolean hasChanged = false;
	
	public DataObject(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public DataObject(String name) {
		this(0, name);
		
		this.name = name;
	}
	
	public DataObject() {
		this(new String());
	}
	
	@Override
	public String toString() {
		return name;
	}
}
