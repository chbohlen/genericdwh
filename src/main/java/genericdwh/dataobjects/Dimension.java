package genericdwh.dataobjects;

import java.util.ArrayList;

public class Dimension extends DataObject {
	
	private String category;
	
	private ArrayList<Dimension> children;
	private ArrayList<Dimension> components;
	
	public Dimension(long id, String name, String category) {
		super(id, name);
		
		this.category = category;
	}
	
	public void addChild(Dimension newChild) {
		if (children == null) {
			children = new ArrayList<Dimension>();
		}
		children.add(newChild);
	}
	
	public void addComponent(Dimension newComponent) {
		if (components == null) {
			components = new ArrayList<Dimension>();
		}
		components.add(newComponent);
	}
	

	public String getCategory() {
		return category;
	}
	
	public ArrayList<Dimension> getChildren() {
		return children;
	}

	public ArrayList<Dimension> getComponents() {
		return components;
	}
}
