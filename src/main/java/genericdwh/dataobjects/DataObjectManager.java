package genericdwh.dataobjects;

import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

public abstract class DataObjectManager {

	protected DatabaseReader dbReader;
	protected DatabaseWriter dbWriter;

	public DataObjectManager(DatabaseController dbController) {
		dbReader = dbController.getDbReader();
		dbWriter = dbController.getDbWriter();
	}
}
