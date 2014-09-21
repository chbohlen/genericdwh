package genericdwh.dataobjects.referenceobject;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.main.Main;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

public class ReferenceObject extends DataObject {
	
	@Getter @Setter private long dimensionId;
	private BooleanProperty isCombination = new SimpleBooleanProperty(false);
	public void setIsCombination(boolean value) { isCombination.set(value); };
	
	@Getter @Setter private List<Long> childrenIds;
	
	@Getter private List<ReferenceObject> children = new ArrayList<>();
	@Getter private List<ReferenceObject> components = new ArrayList<>();
	
	public static ReferenceObject SELECT_REFERENCE_OBJECT = new ReferenceObject(-1, 0, "Select Reference Object ...");
	static { SELECT_REFERENCE_OBJECT.initProperties();	}

	public ReferenceObject(long id, long dimensionId, String name) {
		super(id, name);

		this.dimensionId = dimensionId;
	}
	
	public void addChildren(ReferenceObject newChildren) {
		children.add(newChildren);
	}
	
	public void clearChildren() {
		children.clear();
	}
	
	public boolean isHierarchy() {
		return !children.isEmpty();
	}
	
	public void addComponent(ReferenceObject newComponent) {
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
		if (dimensionId == -1) {
			setDimensionProperty(Dimension.SELECT_DIMENSION);
		} else {
			setDimensionProperty(Main.getContext().getBean(DimensionManager.class).getDimension(dimensionId));
		}
	}
	
	@Getter private ObjectProperty<Dimension> dimensionProperty = new SimpleObjectProperty<>();
	public void setDimensionProperty(Dimension dim) { dimensionProperty.set(dim); };
}
