package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;
import genericdwh.main.Main;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

public class Dimension extends DataObject {

	@Getter @Setter private long categoryId;
	private BooleanProperty isCombination = new SimpleBooleanProperty(false);
	public void setIsCombination(boolean value) { isCombination.set(value); };

	@Getter private List<Dimension> children = new ArrayList<>();
	@Getter private List<Dimension> components = new ArrayList<>();
	
	public static Dimension NO_DIMENSION = new Dimension(0, "No Dimension", 0);
	static { NO_DIMENSION.initProperties();	}
	
	public Dimension(long id, String name, long categoryId) {
		super(id, name);

		this.categoryId = categoryId;
	}

	public void addChildren(Dimension newChildren) {
		children.add(newChildren);
	}
	
	public void clearChildren() {
		children.clear();
	}

	public boolean isHierarchy() {
		return !children.isEmpty();
	}
	
	public void addComponent(Dimension newComponent) {
		if (components.isEmpty()) {
			setIsCombination(true);
		}
		components.add(newComponent);
	}
	
	public void clearComponents() {
		components.clear();
	}
	
	public boolean isCombination() {
		return isCombination.get();
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		if (categoryId == 0) {
			setCategoryProperty(DimensionCategory.NO_DIMENSION_CATEGORY);
		} else {
			setCategoryProperty(Main.getContext().getBean(DimensionManager.class).getCategories().get(categoryId));
		}
	};
		
	@Getter private ObjectProperty<DimensionCategory> categoryProperty = new SimpleObjectProperty<>();
	public void setCategoryProperty(DimensionCategory cat) { categoryProperty.set(cat); }
}
