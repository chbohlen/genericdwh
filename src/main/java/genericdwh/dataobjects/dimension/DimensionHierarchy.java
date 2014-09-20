package genericdwh.dataobjects.dimension;

import java.util.LinkedList;

import genericdwh.dataobjects.DataObjectHierarchy;

public class DimensionHierarchy extends DataObjectHierarchy<Dimension> {
	
	public DimensionHierarchy() {
		super();
	}
	
	public DimensionHierarchy(Dimension dim) {
		super(dim);
	}
	
	public DimensionHierarchy(LinkedList<Dimension> levels) {
		super(levels);
	}
	
	public DimensionHierarchy clone() {
		return new DimensionHierarchy(levels);
	}
}
