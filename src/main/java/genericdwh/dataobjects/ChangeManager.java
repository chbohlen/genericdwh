package genericdwh.dataobjects;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;

import java.util.TreeMap;

public class ChangeManager {

	private DimensionManager dimManager;
	private ReferenceObjectManager refObjManager;
	private RatioManager ratioManager;
	private UnitManager unitManager;
	
	private TreeMap<Long, Dimension> stagedDimensions = new TreeMap<>();
	private TreeMap<Long, ReferenceObject> stagedReferenceObjects = new TreeMap<>();
	private TreeMap<Long, Ratio> stagedRatios = new TreeMap<>();
	private TreeMap<Long, Unit> stagedUnits = new TreeMap<>();
	private TreeMap<Long, DimensionCategory> stagedDimCategories = new TreeMap<>();
	private TreeMap<Long, RatioCategory> stagedRatioCategories = new TreeMap<>();
	
	public ChangeManager(DimensionManager dimManager, ReferenceObjectManager refObjManager, RatioManager ratioManager, UnitManager unitManager) {
		this.dimManager = dimManager;
		this.refObjManager = refObjManager;
		this.ratioManager = ratioManager;
		this.unitManager = unitManager;
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

	public DataObject changeDimension(DataObject obj, long newDimensionId) {
		DataObject clone = getObjectIfStaged(obj);
		if (clone == null) {
			clone = getClone(obj);
		}
		if (clone instanceof ReferenceObject) {
			((ReferenceObject)clone).setDimensionId(newDimensionId);
		}
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
		}
		
		return null;
	}
}
