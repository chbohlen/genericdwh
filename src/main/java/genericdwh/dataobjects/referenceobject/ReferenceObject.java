package genericdwh.dataobjects.referenceobject;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.main.Main;

import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;

public class ReferenceObject extends DataObject {
	
	@Getter @Setter private long dimensionId;
	@Getter @Setter private List<Long> childrenIds;
	
	public ReferenceObject(long id, long dimensionId, String name) {
		super(id, name);

		this.dimensionId = dimensionId;
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		setDimensionProperty(Main.getContext().getBean(DimensionManager.class).getDimension(dimensionId));
	}
	
	@Getter private ObjectProperty<Dimension> dimensionProperty = new SimpleObjectProperty<>();
	public void setDimensionProperty(Dimension dim) { dimensionProperty.set(dim); };
}
