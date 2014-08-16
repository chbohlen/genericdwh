package genericdwh.dataobjects;

import java.util.TreeMap;

public class ReferenceObject extends DataObject {
	
	private long dimensionId;
	
	private TreeMap<Long, ReferenceObject> children;
	private TreeMap<Long, ReferenceObject> components;
	
	public ReferenceObject(long id, long dimensionId, String name) {
		super(id, name);
		
		this.dimensionId = dimensionId;
	}
	
	public void addChild(ReferenceObject newChild) {
		if (children == null) {
			children = new TreeMap<Long, ReferenceObject>();
		}
		children.put(newChild.getId(), newChild);
	}
	
	public void addComponent(ReferenceObject newComponent) {
		if (components == null) {
			components = new TreeMap<Long, ReferenceObject>();
		}
		components.put(newComponent.getId(), newComponent);
	}

	public long getDimensionId() {
		return dimensionId;
	}
}
