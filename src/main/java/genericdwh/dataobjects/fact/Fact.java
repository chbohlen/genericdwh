package genericdwh.dataobjects.fact;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.main.Main;
import lombok.Getter;
import lombok.Setter;

public class Fact extends DataObject {

	@Getter @Setter private long ratioId;
	@Getter @Setter private long referenceObjectId;
	@Getter @Setter private double value;
	@Getter @Setter private long unitId;

	public Fact(long ratioId, long referenceObjectId, double value, long unitId) {
		this.ratioId = ratioId;
		this.referenceObjectId = referenceObjectId;
		this.value = value;
		this.unitId = unitId;
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		if (ratioId == -1) {
			setRatioProperty(Ratio.SELECT_RATIO);
		} else {
			setRatioProperty(Main.getContext().getBean(RatioManager.class).getRatio(ratioId));
		}
		if (referenceObjectId == -1) {
			setReferenceObjectProperty(ReferenceObject.SELECT_REFERENCE_OBJECT);
		} else {
			setReferenceObjectProperty(Main.getContext().getBean(ReferenceObjectManager.class).getReferenceObject(referenceObjectId));
		}
		setValueProperty(value);
		if (unitId == 0) {
			setUnitProperty(Unit.NO_UNIT);
		} else {
			setUnitProperty(Main.getContext().getBean(UnitManager.class).getUnit(unitId));
		}
	}
	
	@Getter private ObjectProperty<Ratio> ratioProperty = new SimpleObjectProperty<>();
	public void setRatioProperty(Ratio ratio) { ratioProperty.set(ratio); };
	
	@Getter private ObjectProperty<ReferenceObject> referenceObjectProperty = new SimpleObjectProperty<>();
	public void setReferenceObjectProperty(ReferenceObject refObj) { referenceObjectProperty.set(refObj); };
	
	@Getter private DoubleProperty valueProperty = new SimpleDoubleProperty();
	public void setValueProperty(double value) { valueProperty.set(value); };
	
	@Getter private ObjectProperty<Unit> unitProperty = new SimpleObjectProperty<>();
	public void setUnitProperty(Unit unit) { unitProperty.set(unit); };
}
