package genericdwh.dataobjects.fact;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectManager;
import genericdwh.db.DatabaseController;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class FactManager extends DataObjectManager {
	
	@Getter private Table<Long, Long, Fact> facts = TreeBasedTable.create();
	
	public FactManager(DatabaseController dbController) {
		super(dbController);
	}
	
	
	public List<Fact> loadFacts() {
		List<Fact> loadedFacts = dbReader.loadFacts();
		for(Fact fact : loadedFacts) {
			facts.put(fact.getRatioId(), fact.getReferenceObjectId(), fact);
		}
		return new ArrayList<Fact>(facts.values());
	}
	
	
	public Fact getFact(long ratioId, long refObjId) {
		return facts.get(ratioId, refObjId);
	}
	
	
	public void initFacts() {
		for (Fact fact : facts.values()) {
			fact.initProperties();
		}
	}

	
	public void saveFacts(List<DataObject> stagedObjects) {
		List<Fact> deletions = new ArrayList<>();
		List<Fact> creations = new ArrayList<>();
		List<Fact> updates = new ArrayList<>();
		
		for (DataObject obj : stagedObjects) {
			Fact fact = (Fact)obj;
			if (fact.isMarkedForDeletion()) {
				if (!fact.isMarkedForCreation()) {
					deletions.add(fact);
				}
			} else {
				if (fact.isMarkedForCreation()) {
					creations.add(fact);
				} else {
					updates.add(fact);
				}
			}
		}
		
		dbWriter.deleteFacts(deletions);
		dbWriter.createFacts(creations);
		dbWriter.updateFacts(updates);
		
		loadFacts();
	}
}
