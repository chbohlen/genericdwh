package genericdwh.dataobjects.ratio;

import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import lombok.Getter;
import genericdwh.dataobjects.DataObjectManager;
import genericdwh.db.DatabaseController;

public class RatioManager extends DataObjectManager {
	
	@Getter private TreeMap<Long, RatioCategory> categories;
	@Getter private TreeMap<Long, Ratio> ratios;

	public RatioManager(DatabaseController dbController) {
		super(dbController);
	}
	
	public void loadRatios() {
		ratios = dbReader.loadRatios();
		
		List<Entry<Long, Long>> ratioHierarchies = dbReader.loadRatioRelations();
		for (Entry<Long, Long> hierarchy : ratioHierarchies) {
			ratios.get(hierarchy.getKey()).addDependency(ratios.get(hierarchy.getValue()));
		}
	}

	public void loadCategories() {
		categories = dbReader.loadRatioCategories();
	}
	
	public Ratio getRatio(long id) {
		return ratios.get(id);
	}
}
