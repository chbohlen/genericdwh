package genericdwh.dataobjects;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

public abstract class DataObjectCombination<T extends DataObject> extends DataObject {

	@Getter protected T combination;
	
	
	public DataObjectCombination() {
		super();
	}
	
	public DataObjectCombination(T combination) {
		super();
		
		this.combination = combination;
	}
	
	public abstract List<T> getComponents();
	
	public String generateName(List<T> components) {
		String newName = "";
		for (T component : components) {
			newName += component.getName();
			if (component != components.get(components.size() - 1)) {
				newName += ".";
			}
		}
		return newName;
	}
	
	@Override
	public void initProperties() {
		setNameProperty(combination.getName());
	};
	
	@Getter private ObjectProperty<List<T>> componentsProperty = new SimpleObjectProperty<>(new ArrayList<>());
}
