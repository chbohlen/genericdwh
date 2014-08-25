package genericdwh.db;

import lombok.Setter;

import org.jooq.DSLContext;

public class MySQLDatabaseWriter implements DatabaseWriter {
	
	@Setter private DSLContext dslContext;

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

	public void newRatio() {
		// TODO Auto-generated method stub
	}
}
