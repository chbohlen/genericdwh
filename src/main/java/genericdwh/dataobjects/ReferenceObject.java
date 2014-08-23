package genericdwh.dataobjects;

import java.util.TreeMap;

public class ReferenceObject extends DataObject {

	private long id;
	private long dimensionId;
	
	private TreeMap<Long, ReferenceObject> children;
	private TreeMap<Long, ReferenceObject> components;
	
	public ReferenceObject(long id, long dimensionId, String name) {
		super(name);
		
		this.id = id;
		this.dimensionId = dimensionId;
		
		children = new TreeMap<Long, ReferenceObject>();
		components = new TreeMap<Long, ReferenceObject>();
	}
	
	public void addComponent(ReferenceObject newComponent) {
		if (components == null) {
			components = new TreeMap<Long, ReferenceObject>();
		}
		components.put(newComponent.getId(), newComponent);
	}
	
	public long getId() {
		return id;
	}

	public long getDimensionId() {
		return dimensionId;
	}
	
	public boolean isHierarchy() {
		return !children.isEmpty();
	}
	
	public boolean isCombination() {
		return !components.isEmpty();
	}
	
	public TreeMap<Long, ReferenceObject> getChildren() {
		return children;
	}
	
	public TreeMap<Long, ReferenceObject> getComponents() {
		return components;
	}
}
