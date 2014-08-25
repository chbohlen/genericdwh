package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;

import java.util.ArrayList;

import lombok.Getter;

public class Dimension extends DataObject {

	@Getter private long id;
	@Getter private String category;

	@Getter private ArrayList<Dimension> children;
	@Getter private ArrayList<Dimension> components;

	public Dimension(long id, String name, String category) {
		super(name);

		this.id = id;
		if (category == null) {
			this.category = "";
		} else {
			this.category = category;
		}

		children = new ArrayList<Dimension>();
		components = new ArrayList<Dimension>();
	}

	public void addChildren(Dimension newChildren) {
		children.add(newChildren);
	}

	public void addComponent(Dimension newComponent) {
		components.add(newComponent);
	}

	public boolean isHierarchy() {
		return !children.isEmpty();
	}

	public boolean isCombination() {
		return !components.isEmpty();
	}

	public int getChildCount() {
		return children.size();
	}
}
