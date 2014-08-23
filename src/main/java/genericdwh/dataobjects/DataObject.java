package genericdwh.dataobjects;

public abstract class DataObject {
	
	protected String name;
	
	public DataObject(String name) {
		this.name = name;
	}
	
	public DataObject() {
		this(new String());
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
