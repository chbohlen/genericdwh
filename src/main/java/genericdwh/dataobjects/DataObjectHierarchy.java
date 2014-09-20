package genericdwh.dataobjects;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.main.Main;

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
	
	public void addLevel(T level) {		
		if (levels.isEmpty()) {
			if (level instanceof Dimension) {
				categoryId = ((Dimension)level).getCategoryId();
			} else if (level instanceof ReferenceObject) {
				categoryId = ((ReferenceObject)level).getDimensionProperty().get().getCategoryProperty().get().getId();
			}
		}
		
		levels.add(level);
		
		name = generateName(levels);
	}
	
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
		setCategoryProperty(Main.getContext().getBean(DimensionManager.class).getCategories().get(categoryId));
		getLevelsProperty().get().clear();
		for (T level : levels) {
			getLevelsProperty().get().add(level);
		}
	};
		
	@Getter private ObjectProperty<DimensionCategory> categoryProperty = new SimpleObjectProperty<>();
	public void setCategoryProperty(DimensionCategory cat) { categoryProperty.set(cat); }
	
	@Getter private ObjectProperty<List<T>> levelsProperty = new SimpleObjectProperty<>(new ArrayList<>());
}
