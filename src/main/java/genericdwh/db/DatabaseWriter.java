package genericdwh.db;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.referenceobject.ReferenceObject;

public interface DatabaseWriter {
	
	public void updateDimension(Dimension dim);
	public void updateReferenceObject(ReferenceObject refObj);
}
