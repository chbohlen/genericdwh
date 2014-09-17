package genericdwh.dataobjects;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.fact.Fact;
import genericdwh.dataobjects.fact.FactManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;

import java.util.TreeMap;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

public class ChangeManager {

	private DimensionManager dimManager;
	private ReferenceObjectManager refObjManager;
	private RatioManager ratioManager;
	private UnitManager unitManager;
	private FactManager factManager;
	
	private TreeMap<Long, Dimension> stagedDimensions = new TreeMap<>();
	private TreeMap<Long, ReferenceObject> stagedReferenceObjects = new TreeMap<>();
	private TreeMap<Long, Ratio> stagedRatios = new TreeMap<>();
	private TreeMap<Long, Unit> stagedUnits = new TreeMap<>();
	private TreeMap<Long, DimensionCategory> stagedDimCategories = new TreeMap<>();
	private TreeMap<Long, RatioCategory> stagedRatioCategories = new TreeMap<>();
	private Table<Long, Long, Fact> stagedFacts = TreeBasedTable.create();
	
	public ChangeManager(DimensionManager dimManager, ReferenceObjectManager refObjManager, RatioManager ratioManager, UnitManager unitManager, FactManager factManager) {
		this.dimManager = dimManager;
		this.refObjManager = refObjManager;
		this.ratioManager = ratioManager;
		this.unitManager = unitManager;
		this.factManager = factManager;
	}
	
	
	public DataObject changeName(DataObject obj, String newName) {
		DataObject clone = getObjectIfStaged(obj);
		if (clone == null) {
			clone = getClone(obj);
		}
		clone.setName(newName);
		stageObject(clone);
		return clone;
	}

	public DataObject changeCategory(DataObject obj, long newCategoryId) {
		DataObject clone = getObjectIfStaged(obj);
		if (clone == null) {
			clone = getClone(obj);
		}
		if (clone instanceof Dimension) {
			((Dimension)clone).setCategoryId(newCategoryId);
		} else if (clone instanceof Ratio) {
			((Ratio)clone).setCategoryId(newCategoryId);
		}
		stageObject(clone);
		return clone;
	}

	public DataObject changeDimension(ReferenceObject refObj, long newDimensionId) {
		DataObject clone = getObjectIfStaged(refObj);
		if (clone == null) {
			clone = getClone(refObj);
		}
		((ReferenceObject)clone).setDimensionId(newDimensionId);
		stageObject(clone);
		return clone;
	}
	
	public DataObject changeSymbol(Unit unit, String newSymbol) {
		DataObject clone = getObjectIfStaged(unit);
		if (clone == null) {
			clone = getClone(unit);
		}
		((Unit)clone).setSymbol(newSymbol);
		stageObject(clone);
		return clone;
	}
	
	public DataObject changeRatio(Fact fact, long newRatioId) {
		DataObject clone = getObjectIfStaged(fact);
		if (clone == null) {
			clone = getClone(fact);
		}
		((Fact)clone).setRatioId(newRatioId);
		stageObject(clone);
		return clone;
	}
	
	public DataObject changeReferenceObject(Fact fact, long newRefObjId) {
		DataObject clone = getObjectIfStaged(fact);
		if (clone == null) {
			clone = getClone(fact);
		}
		((Fact)clone).setReferenceObjectId(newRefObjId);
		stageObject(clone);
		return clone;
	}
	
	public DataObject changeValue(Fact fact, Double newValue) {
		DataObject clone = getObjectIfStaged(fact);
		if (clone == null) {
			clone = getClone(fact);
		}
		((Fact)clone).setValue(newValue);
		stageObject(clone);
		return clone;
	}
	
	private DataObject getClone(DataObject obj) {
		DataObject clone = null;
		if (obj instanceof Dimension) {
			clone = dimManager.getDimension(obj.getId()).clone();
		} else if (obj instanceof ReferenceObject) {
			clone = refObjManager.getReferenceObject(obj.getId()).clone();
		} else if (obj instanceof Ratio) {
			clone = ratioManager.getRatio(obj.getId()).clone();
		} else if (obj instanceof Unit) {
			clone = unitManager.getUnit(obj.getId()).clone();
		} else if (obj instanceof Fact) {
			clone = factManager.getFact(((Fact)obj).getRatioId(), ((Fact)obj).getReferenceObjectId()).clone();
		}
		
		return clone;
	}
	
	private void stageObject(DataObject obj) {
		if (obj instanceof Dimension) {
			stagedDimensions.put(obj.getId(), (Dimension)obj);
		} else if (obj instanceof ReferenceObject) {
			stagedReferenceObjects.put(obj.getId(), (ReferenceObject)obj);
		} else if (obj instanceof Ratio) {
			stagedRatios.put(obj.getId(), (Ratio)obj);
		} else if (obj instanceof Unit) {
			stagedUnits.put(obj.getId(), (Unit)obj);
		} else if (obj instanceof DimensionCategory) {
			stagedDimCategories.put(obj.getId(), (DimensionCategory)obj);
		} else if (obj instanceof RatioCategory) {
			stagedRatioCategories.put(obj.getId(), (RatioCategory)obj);
		} else if (obj instanceof Fact) {
			stagedFacts.put(((Fact)obj).getRatioId(), ((Fact)obj).getReferenceObjectId(), (Fact)obj);
		}
	}
	
	private DataObject getObjectIfStaged(DataObject obj) {
		if (obj instanceof Dimension) {
			return stagedDimensions.get(obj.getId());
		} else if (obj instanceof ReferenceObject) {
			return stagedReferenceObjects.get(obj.getId());
		} else if (obj instanceof Ratio) {
			return stagedRatios.get(obj.getId());
		} else if (obj instanceof Unit) {
			return stagedUnits.get(obj.getId());
		} else if (obj instanceof DimensionCategory) {
			return stagedDimCategories.get(obj.getId());
		} else if (obj instanceof RatioCategory) {
			return stagedRatioCategories.get(obj.getId());
		}  else if (obj instanceof Fact) {
			return stagedFacts.get(((Fact)obj).getRatioId(), ((Fact)obj).getReferenceObjectId());
		}
		
		return null;
	}
}
