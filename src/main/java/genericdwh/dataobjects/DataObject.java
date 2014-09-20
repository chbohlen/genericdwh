package genericdwh.dataobjects;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;
import lombok.Setter;

public abstract class DataObject {
	
	@Getter @Setter private boolean markedForCreation = false;
	@Getter @Setter private boolean markedForUpdate = false;
	@Getter @Setter private boolean markedForDeletion = false;
	
	@Getter protected long id;
	@Getter @Setter protected String name;
	
	public DataObject(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public DataObject(String name) {
		this(-1, name);
		
		this.name = name;
	}
	
	public DataObject() {
		this(new String());
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public void initProperties() {
		setNameProperty(name);
	}
	
	@Getter private StringProperty nameProperty = new SimpleStringProperty(null);
	public void setNameProperty(String name) { nameProperty.set(name); }
}
