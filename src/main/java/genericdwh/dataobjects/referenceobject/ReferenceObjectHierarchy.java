package genericdwh.dataobjects.referenceobject;

import java.util.LinkedList;

import genericdwh.dataobjects.DataObjectHierarchy;

public class ReferenceObjectHierarchy extends DataObjectHierarchy<ReferenceObject> {
	
	public ReferenceObjectHierarchy() {
		super();
	}
	
	public ReferenceObjectHierarchy(ReferenceObject refObj) {
		super(refObj);
	}
	
	public ReferenceObjectHierarchy(LinkedList<ReferenceObject> levels) {
		super(levels);
	}
	
	public ReferenceObjectHierarchy clone() {
		return new ReferenceObjectHierarchy(levels);
	}
}
