package genericdwh.dataobjects.ratio;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;
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
	

	public void initCategories() {
		for (RatioCategory cat : categories.values()) {
			cat.initProperties();
		}
	}
	
	public void initRatios() {
		for (Ratio ratio : ratios.values()) {
			ratio.initProperties();
		}
	}

	
	public void saveRatios(List<DataObject> stagedObjects) {
		List<Ratio> deletions = new ArrayList<>();
		List<Ratio> creations = new ArrayList<>();
		List<Ratio> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			Ratio ratio = (Ratio)obj;
			if (ratio.isMarkedForDeletion()) {
				if (!ratio.isMarkedForCreation()) {
					deletions.add(ratio);
				}
			} else {
				if (ratio.isMarkedForCreation()) {
					creations.add(ratio);
				} else {
					updates.add(ratio);
				}
			}
		}
		
		dbWriter.deleteRatios(deletions);
		dbWriter.createRatios(creations);
		dbWriter.updateRatios(updates);
		
		loadRatios();
	}

	public void saveCategories(List<DataObject> stagedObjects) {
		List<RatioCategory> deletions = new ArrayList<>();
		List<RatioCategory> creations = new ArrayList<>();
		List<RatioCategory> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			RatioCategory cat = (RatioCategory)obj;
			if (cat.isMarkedForDeletion()) {
				if (!cat.isMarkedForCreation()) {
					deletions.add(cat);
				}
			} else {
				if (cat.isMarkedForCreation()) {
					creations.add(cat);
				} else {
					updates.add(cat);
				}
			}
		}
		
		dbWriter.deleteRatioCategories(deletions);
		dbWriter.createRatioCategories(creations);
		dbWriter.updateRatioCategories(updates);
		
		loadCategories();
	}
}
