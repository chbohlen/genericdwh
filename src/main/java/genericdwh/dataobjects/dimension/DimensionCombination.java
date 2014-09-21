package genericdwh.dataobjects.dimension;

import java.util.List;

import genericdwh.dataobjects.DataObjectCombination;

public class DimensionCombination extends DataObjectCombination<Dimension> {

	public DimensionCombination() {
		super();
		
		combination = new Dimension(-1, "New Combination", 0);
		combination.initProperties();
	}
	
	public DimensionCombination(Dimension combination) {
		super(combination);
	}
	
	@Override
	public List<Dimension> getComponents() {
		return combination.getComponents();
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		getComponentsProperty().get().clear();
		getComponentsProperty().get().addAll(combination.getComponents());
	};
}
