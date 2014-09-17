package genericdwh.db;

import static genericdwh.db.model.tables.Dimensions.DIMENSIONS;
import static genericdwh.db.model.tables.ReferenceObjects.REFERENCE_OBJECTS;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import lombok.Setter;

import org.jooq.DSLContext;

public class MySQLDatabaseWriter implements DatabaseWriter {
	
	@Setter private DSLContext dslContext;

	public void updateDimension(Dimension dim) {
		dslContext
			.update(DIMENSIONS)
			.set(DIMENSIONS.NAME, dim.getName())
			.set(DIMENSIONS.CATEGORY_ID, dim.getCategoryId())
			.where(DIMENSIONS.DIMENSION_ID.equal(dim.getId()));			
	}
	
	public void updateReferenceObject(ReferenceObject refObj) {
		dslContext
			.update(REFERENCE_OBJECTS)
			.set(REFERENCE_OBJECTS.NAME, refObj.getName())
			.set(REFERENCE_OBJECTS.DIMENSION_ID, refObj.getDimensionId())
			.where(REFERENCE_OBJECTS.REFERENCE_OBJECT_ID.equal(refObj.getId()));			
	}
}
