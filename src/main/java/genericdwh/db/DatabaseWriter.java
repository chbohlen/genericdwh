package genericdwh.db;

public interface DatabaseWriter {
	public void newDimension();
	public void newDimensionHierarchy();
	public void newDimensionCombination();
	
	public void newReferenceObject();
	public void newReferenceObjectHierarchy();
	public void newReferenceObjectCombination();
}
