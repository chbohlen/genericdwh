package genericdwh.db;

import org.jooq.DSLContext;

public class MySQLDatabaseWriter implements DatabaseWriter {
	private DSLContext create;

	public void newDimension() {
		// TODO Auto-generated method stub
	}

	public void newDimensionHierarchy() {
		// TODO Auto-generated method stub		
	}

	public void newDimensionCombination() {
		// TODO Auto-generated method stub
	}
	
	public void newReferenceObject() {
		// TODO Auto-generated method stub
	}

	public void newReferenceObjectHierarchy() {
		// TODO Auto-generated method stub
	}

	public void newReferenceObjectCombination() {
		// TODO Auto-generated method stub
	}

	public void setDSLContext(DSLContext context) {
		this.create = context;
	}
}
