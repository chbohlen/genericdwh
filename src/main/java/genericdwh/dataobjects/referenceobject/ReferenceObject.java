package genericdwh.dataobjects.referenceobject;

import genericdwh.dataobjects.DataObject;

import java.util.TreeMap;

import lombok.Getter;

public class ReferenceObject extends DataObject {

	@Getter private long id;
	@Getter private long dimensionId;

	@Getter private TreeMap<Long, ReferenceObject> children = new TreeMap<Long, ReferenceObject>();
//	@Getter private TreeMap<Long, ReferenceObject> components = new TreeMap<Long, ReferenceObject>();

	public ReferenceObject(long id, long dimensionId, String name) {
		super(name);

		this.id = id;
		this.dimensionId = dimensionId;
	}
	
	public void addChildren(ReferenceObject newChildren) {
		children.put(newChildren.getId(), newChildren);
	}

//	public void addComponent(ReferenceObject newComponent) {
//		components.put(newComponent.getId(), newComponent);
//	}

	public boolean isHierarchy() {
		return !children.isEmpty();
	}

//	public boolean isCombination() {
//		return !components.isEmpty();
//	}
	
	public int getChildCount() {
		return children.size();
	}
}
