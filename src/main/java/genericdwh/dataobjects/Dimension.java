package genericdwh.dataobjects;

import java.util.ArrayList;

public class Dimension extends DataObject {
	
	private long id;
	private String category;
	
	private ArrayList<Dimension> children;
	private ArrayList<Dimension> components;
	
	public Dimension(long id, String name, String category) {
		super(name);
		
		this.id = id;
		this.category = category;
		
		children = new ArrayList<Dimension>();
		components = new ArrayList<Dimension>();
	}
	
	public void addChildren(Dimension newChildren) {
		if (children == null) {
			children = new ArrayList<Dimension>();
		}
		children.add(newChildren);
	}
	
	public void addComponent(Dimension newComponent) {
		if (components == null) {
			components = new ArrayList<Dimension>();
		}
		components.add(newComponent);
	}

	public long getId() {
		return id;
	}
	
	public String getCategory() {
		return category;
	}
	
	public boolean isHierarchy() {
		return !children.isEmpty();
	}
	
	public boolean isCombination() {
		return !components.isEmpty();
	}
	
	public ArrayList<Dimension> getChildren() {
		return children;
	}
	
	public ArrayList<Dimension> getComponents() {
		return components;
	}
	
	public int getChildCount() {
		return children.size();
	}
}
