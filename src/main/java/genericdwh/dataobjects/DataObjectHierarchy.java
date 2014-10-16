package genericdwh.dataobjects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

public abstract class DataObjectHierarchy<T extends DataObject> extends DataObject {
	
	@Getter @Setter protected long categoryId;
	
	@Getter protected LinkedList<T> levels = new LinkedList<>();
	
	protected DataObjectHierarchy() {
		super();
	}
	
	public DataObjectHierarchy(T top) {
		this();
		
		addLevel(top);
	}
	
	public DataObjectHierarchy(LinkedList<T> levels) {
		this();

		for (T level : levels) {
			addLevel(level);
		}
	}
	
	public abstract void addLevel(T level);
	
	public T getTopLevel() {
		return levels.get(0);
	}
	
	public String generateName(List<T> lvls) {
		String newName = "";
		for (T level : lvls) {
			newName += level.getName();
			if (level != lvls.get(lvls.size() - 1)) {
				newName += "-";
			}
		}
		return newName;
	}
	
	public abstract DataObjectHierarchy<T> clone();
	
	@Override
	public void initProperties() {
		super.initProperties();
		getLevelsProperty().get().clear();
		getLevelsProperty().get().addAll(levels);
	};
	
	@Getter private ObjectProperty<DataObjectCategory> categoryProperty = new SimpleObjectProperty<>();
	public void setCategoryProperty(DataObjectCategory cat) { categoryProperty.set(cat); }
	
	@Getter private ObjectProperty<List<T>> levelsProperty = new SimpleObjectProperty<>(new ArrayList<>());
}
