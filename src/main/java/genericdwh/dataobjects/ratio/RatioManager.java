package genericdwh.dataobjects.ratio;

import java.util.ArrayList;
import java.util.LinkedList;
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
	@Getter private List<RatioRelation> relations;

	public RatioManager(DatabaseController dbController) {
		super(dbController);
	}
	
	
	public void loadRatios() {
		ratios = dbReader.loadRatios();
	}
	
	public void loadRelations() {
		for (Ratio ratio : ratios.values()) {
			ratio.clearDependecies();
		}
		
		List<Entry<Long, Long>> ratioRelations = dbReader.loadRatioRelations();
		for (Entry<Long, Long> relation : ratioRelations) {
			ratios.get(relation.getKey()).addDependency(ratios.get(relation.getValue()));
		}
		
		relations = generateRelations();
	}

	public void loadCategories() {
		categories = dbReader.loadRatioCategories();
	}
	
	private List<RatioRelation> generateRelations() {
		List<RatioRelation> newRelations = new ArrayList<>();
		
		for (Ratio currRatio : ratios.values()) {
			if (currRatio.isRelation()) {
				LinkedList<RatioRelation> tmpNewRelations = new LinkedList<>();
				tmpNewRelations.add(new RatioRelation(currRatio));
				
				do {
					RatioRelation currNewRelation = tmpNewRelations.pop();
					
					Ratio lastLevel = currNewRelation.getLevels().getLast();
					if (lastLevel.isRelation()) {
						for (Ratio dependency : lastLevel.getDependencies()) {
							RatioRelation currNewRelationClone = (RatioRelation)currNewRelation.clone();
							currNewRelationClone.addLevel(dependency);
							tmpNewRelations.add(currNewRelationClone);
						}
					} else {
						newRelations.add(currNewRelation);
					}
				} while (!tmpNewRelations.isEmpty());
			}
		}
		
		return newRelations;
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
	
	public void initRelations() {
		for (RatioRelation ratioRelation : relations) {
			ratioRelation.initProperties();
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

	public void saveRelations(List<DataObject> stagedObjects) {
		List<RatioRelation> deletions = new ArrayList<>();
		List<RatioRelation> creations = new ArrayList<>();
		List<RatioRelation> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			RatioRelation relation = (RatioRelation)obj;
			if (relation.isMarkedForDeletion()) {
				if (!relation.isMarkedForCreation()) {
					deletions.add(relation);
				}
			} else {
				if (relation.isMarkedForCreation()) {
					creations.add(relation);
				} else {
					updates.add(relation);
				}
			}
		}
		
		dbWriter.deleteRatioRelations(deletions);
		dbWriter.createRatioRelations(creations);
		dbWriter.updateRatioRelations(updates);
		
		loadRelations();
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
