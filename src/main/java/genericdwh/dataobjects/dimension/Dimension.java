package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;
import genericdwh.main.Main;

import java.util.ArrayList;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

public class Dimension extends DataObject {

	@Getter @Setter private long categoryId;
	@Getter private BooleanProperty isCombination = new SimpleBooleanProperty(false);
	public void setIsCombination(boolean value) { isCombination.set(value); };

	@Getter private ArrayList<Dimension> children = new ArrayList<Dimension>();
	
	public Dimension(long id, String name, long categoryId) {
		super(id, name);

		this.categoryId = categoryId;
	}

	public void addChildren(Dimension newChildren) {
		children.add(newChildren);
	}

	public boolean isHierarchy() {
		return !children.isEmpty();
	}

	public int getChildCount() {
		return children.size();
	}
	
	public boolean isCombination() {
		return isCombination.get();
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		setCategoryProperty(Main.getContext().getBean(DimensionManager.class).getCategories().get(categoryId));
	};
		
	@Getter private ObjectProperty<DimensionCategory> categoryProperty = new SimpleObjectProperty<>();
	public void setCategoryProperty(DimensionCategory cat) { categoryProperty.set(cat); }
}
