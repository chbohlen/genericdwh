package genericdwh.dataobjects.ratio;

import java.util.TreeMap;

import lombok.Getter;
import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

public class RatioManager {
	
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	@Getter private TreeMap<Long, Ratio> ratios;
	@Getter private TreeMap<Long, RatioCategory> categories;

	public RatioManager(DatabaseController dbController) {
		dbReader = dbController.getDbReader();
		dbWriter = dbController.getDbWriter();
	}
	
	public void loadRatios() {
		ratios = dbReader.loadRatios();
	}
	
	public void loadCategories() {
		categories = dbReader.loadRatioCategories();
	}
}
