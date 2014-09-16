package genericdwh.dataobjects.referenceobject;

import genericdwh.dataobjects.DataObject;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ReferenceObject extends DataObject {
	
	@Getter @Setter private long dimensionId;
	@Getter @Setter private List<Long> childrenIds;

//	@Getter private TreeMap<Long, ReferenceObject> components = new TreeMap<Long, ReferenceObject>();

	public ReferenceObject(long id, long dimensionId, String name) {
		super(id, name);

		this.dimensionId = dimensionId;
	}

	@Override
	public ReferenceObject clone() {
		ReferenceObject newRefObj = new ReferenceObject(this.id, this.dimensionId, this.name);
		newRefObj.childrenIds = this.childrenIds;
		return newRefObj;
	}

//	public void addComponent(ReferenceObject newComponent) {
//		components.put(newComponent.getId(), newComponent);
//	}


//	public boolean isCombination() {
//		return !components.isEmpty();
//	}
}
