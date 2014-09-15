package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;
import lombok.Setter;

public class Dimension extends DataObject {

	@Getter @Setter private long categoryId;
	@Getter private BooleanProperty isCombination = new SimpleBooleanProperty(false);
	public void setIsCombination(boolean value) { isCombination.set(value); };

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
	
	public boolean isCombination() {
		return isCombination.get();
	}
}
