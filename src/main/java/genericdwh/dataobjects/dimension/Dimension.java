package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;

import java.util.ArrayList;

import lombok.Getter;

public class Dimension extends DataObject {

	@Getter private long categoryId;

	@Getter private ArrayList<Dimension> children = new ArrayList<Dimension>();
//	@Getter private ArrayList<Dimension> components = new ArrayList<Dimension>();

	public Dimension(long id, String name, long categoryId) {
		super(id, name);

		this.categoryId = categoryId;
	}

	public void addChildren(Dimension newChildren) {
		children.add(newChildren);
	}

//	public void addComponent(Dimension newComponent) {
//		components.add(newComponent);
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
