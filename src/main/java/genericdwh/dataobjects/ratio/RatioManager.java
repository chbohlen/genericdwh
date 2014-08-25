package genericdwh.dataobjects.ratio;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import lombok.Getter;
import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.db.DatabaseWriter;

public class RatioManager {
	
	private DatabaseReader dbReader;
	private DatabaseWriter dbWriter;
	
	@Getter private TreeMap<Long, RatioCategory> categories;
	@Getter private TreeMap<Long, Ratio> ratios;

	public RatioManager(DatabaseController dbController) {
		dbReader = dbController.getDbReader();
		dbWriter = dbController.getDbWriter();
	}
	
	public void loadRatios() {
		ratios = dbReader.loadRatios();
		
		ArrayList<Entry<Long, Long>> ratioHierarchies = dbReader.loadRatioRelations();
		for (Entry<Long, Long> hierarchy : ratioHierarchies) {
			ratios.get(hierarchy.getKey()).addDependency(ratios.get(hierarchy.getValue()));
		}
	}

	public void loadCategories() {
		categories = dbReader.loadRatioCategories();
	}
}
