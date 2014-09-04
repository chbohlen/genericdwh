package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.db.ResultObject;
import genericdwh.main.Main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

public class ResultGridController {
	
	private LinkedList<ResultGrid> resultGrids = new LinkedList<>();
			
	public ResultGridController() {
	}
	
	public void initializeGrid(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, Ratio ratio) {
		resultGrids.getLast().initialize(rowRefObjs, colRefObjs, ratio);
	}
	
	public void initializeGridWTotals(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, Ratio ratio) {		
		resultGrids.getLast().initializeWTotals(rowRefObjs, colRefObjs, ratio);
	}
	
	public void initializeGridWHierarchiesWTotals(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, ArrayList<DimensionHierarchy> hierarchies, Ratio ratio) {		
		resultGrids.getLast().initializeWHierarchiesWTotals(rowRefObjs, colRefObjs, hierarchies, ratio);
	}

	public void initializeGridNoData(Ratio ratio) {
		resultGrids.getLast().initializeNoData(ratio);
	}

	public void fillReferenceObject(ResultObject factForRefObj) {
		if (factForRefObj == null) {
			return;
		}
		
		Unit unit = Main.getContext().getBean(UnitManager.class).getUnit(factForRefObj.getUnitId());
		resultGrids.getLast().setUnitSymbol(unit.getSymbol());
		resultGrids.getLast().postResult(factForRefObj.getId(), factForRefObj.getComponentIds(), factForRefObj.getValue());
	}
	
	public void fillDimension(ArrayList<ResultObject> factsForDimensions) {
		if (factsForDimensions == null) {
			return;
		}
		
		Unit unit = Main.getContext().getBean(UnitManager.class).getUnit(factsForDimensions.get(0).getUnitId());
		resultGrids.getLast().setUnitSymbol(unit.getSymbol());
		for (ResultObject currFact : factsForDimensions) {
			resultGrids.getLast().postResult(currFact.getId(), currFact.getComponentIds(), currFact.getValue());
		}
		resultGrids.getLast().calculateAndPostTotals();
	}
	
	public void fillRatio(ArrayList<ResultObject> factsForRatio) {
		if (factsForRatio == null) {
			return;
		}
		
		Unit unit = Main.getContext().getBean(UnitManager.class).getUnit(factsForRatio.get(0).getUnitId());
		resultGrids.getLast().setUnitSymbol(unit.getSymbol());
		for (ResultObject currFact : factsForRatio) {
			resultGrids.getLast().postResult(currFact.getId(), currFact.getComponentIds(), currFact.getValue());
		}
	}
	
	public int addResultGrid(ResultGrid newResultGrid) {
		resultGrids.add(newResultGrid);
		return resultGrids.indexOf(newResultGrid);
	}
	
	public void reset() {
		resultGrids.clear();
	}
}
