package genericdwh.dataobjects.fact;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
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
}
