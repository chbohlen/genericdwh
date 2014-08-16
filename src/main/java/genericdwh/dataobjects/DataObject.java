package genericdwh.dataobjects;

public abstract class DataObject {
	protected long id;
	protected String name;
	
	public DataObject(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
