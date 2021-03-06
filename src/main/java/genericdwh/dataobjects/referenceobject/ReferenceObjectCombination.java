package genericdwh.dataobjects.referenceobject;

import java.util.List;

import genericdwh.dataobjects.DataObjectCombination;

public class ReferenceObjectCombination extends DataObjectCombination<ReferenceObject> {

	public ReferenceObjectCombination() {
		super();
		
		combination = new ReferenceObject(-1, -1, "New Combination");
		combination.initProperties();
	}
	
	public ReferenceObjectCombination(ReferenceObject combination) {
		super(combination);
	}
	
	@Override
	public List<ReferenceObject> getComponents() {
		return combination.getComponents();
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		getComponentsProperty().get().clear();
		getComponentsProperty().get().addAll(combination.getComponents());
	};
}
