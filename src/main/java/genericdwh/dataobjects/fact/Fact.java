package genericdwh.dataobjects.fact;

import genericdwh.dataobjects.DataObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class Fact extends DataObject {

	@Getter @Setter private long ratioId;
	@Getter @Setter private long referenceObjectId;
	@Getter @Setter private double value;
	@Getter @Setter private long unitId;
	
	@Override
	public DataObject clone() {
		Fact newFact = new Fact(this.ratioId, this.referenceObjectId, this.value, this.unitId);
		return newFact;
	}
}
