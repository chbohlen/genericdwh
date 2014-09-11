package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.Unit;
import genericdwh.dataobjects.unit.UnitManager;
import genericdwh.db.DatabaseReader;
import genericdwh.db.ResultObject;
import genericdwh.gui.mainwindow.querypane.QueryPaneController.QueryType;
import genericdwh.main.Main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.ObjectArrays;

public class ResultGridController {
	
	private LinkedList<ResultGrid> resultGrids = new LinkedList<>();

	public ResultGridController() {
	}

	
	public void initializeGrid(Ratio ratio, ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		resultGrids.getLast().initializeDefault(ratio, rowRefObjs, colRefObjs);
	}
	
	public void initializeGridWTotals(Ratio ratio, ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		resultGrids.getLast().initializeWTotals(ratio, rowRefObjs, colRefObjs);
	}
	
	public void initializeGridWHierarchiesWTotals(Ratio ratio, List<DataObject> rowDims, List<DataObject> colDims,
			ArrayList<DataObject> combinedDims, List<DataObject> filter, List<DimensionHierarchy> hierarchies, QueryType queryType) {
		
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		ResultGrid grid = resultGrids.getLast();
		grid.setRowDims(rowDims);
		grid.setColDims(colDims);
		grid.setCombinedDims(combinedDims);
		grid.setQueryType(queryType);
		grid.setFilter(filter);
		
		grid.initializeWHierarchiesWTotals(ratio, refObjManager.loadRefObjs(rowDims, filter), refObjManager.loadRefObjs(colDims, filter), filter, hierarchies);
	}

	
	public void fillReferenceObject(ResultObject fact) {
		if (fact == null) {
			return;
		}
		
		Unit unit = Main.getContext().getBean(UnitManager.class).getUnit(fact.getUnitId());
		resultGrids.getLast().setUnitSymbol(unit.getSymbol());
		resultGrids.getLast().postResult(fact.getId(), fact.getComponentIds(), fact.getValue());
	}
	
	public void fillDimension(List<ResultObject> facts) {
		if (facts == null) {
			return;
		}
		
		Unit unit = Main.getContext().getBean(UnitManager.class).getUnit(facts.get(0).getUnitId());
		resultGrids.getLast().setUnitSymbol(unit.getSymbol());
		for (ResultObject currFact : facts) {
			resultGrids.getLast().postResult(currFact.getId(), currFact.getComponentIds(), currFact.getValue());
		}
		resultGrids.getLast().calculateAndPostTotals();
	}
	
	public void fillDimensionWHierarchy(int gridId, List<ResultObject> facts, Long[] hierarchyComponentIds) {
		if (facts == null) {
			return;
		}
		
		ResultGrid grid = resultGrids.get(gridId);
		
		Unit unit = Main.getContext().getBean(UnitManager.class).getUnit(facts.get(0).getUnitId());
		grid.setUnitSymbol(unit.getSymbol());
		for (ResultObject currFact : facts) {
			Long[] componentIds = ObjectArrays.concat(hierarchyComponentIds, currFact.getComponentIds(), Long.class);
			grid.postResult(currFact.getId(), componentIds, currFact.getValue());
		}
		grid.calculateAndPostTotals();
	}
	
	public void fillRatio(List<ResultObject> facts) {
		if (facts == null) {
			return;
		}
		
		Unit unit = Main.getContext().getBean(UnitManager.class).getUnit(facts.get(0).getUnitId());
		resultGrids.getLast().setUnitSymbol(unit.getSymbol());
		for (ResultObject currFact : facts) {
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

	
	public void expandCollapseGridHandler(int gridId, DimensionHierarchy hierarchy, Dimension currLevel, boolean expand) {		
		DatabaseReader dbReader = Main.getContext().getBean(DatabaseReader.class);
		DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		ResultGrid grid = resultGrids.get(gridId);
		
		List<DataObject> rowDims = grid.getRowDims();
		List<DataObject> colDims = grid.getColDims();
				
		int currIndex = hierarchy.getLevels().indexOf(currLevel);
		Dimension newLevel;
		int inserted = 0;
		int removed = 0;
		if (expand) {
			newLevel = hierarchy.getLevels().get(currIndex + 1);	
			inserted = insertHierarchyLevel(rowDims, hierarchy, currLevel, newLevel);
			if (inserted < 1) {
				inserted = insertHierarchyLevel(colDims, hierarchy, currLevel, newLevel);
			}
		} else {
			newLevel = currLevel;

			removed = removeHierarchyLevel(rowDims, hierarchy, currLevel);
			if (removed < 1) {
				removed = removeHierarchyLevel(colDims, hierarchy, currLevel);
			}
		}
		
		ArrayList<DataObject> combinedDims = grid.getCombinedDims();
		
		for (Dimension level : hierarchy.getLevels()) {
			combinedDims.remove(level);
		}
		if (newLevel != hierarchy.getTopLevel()) {
			combinedDims.remove(hierarchy);
		}
		combinedDims.add(newLevel);
		
		ArrayList<Entry<DimensionHierarchy, Integer>> allHierarchies = grid.getHierarchies();
		
		Long[] hierarchyComponentIds = new Long[0];
		for (Entry<DimensionHierarchy, Integer> currHierarchy : allHierarchies) {
			if (currHierarchy.getKey() == hierarchy) {
				if (expand) {
					currHierarchy.setValue(currHierarchy.getValue() + inserted);
				} else {
					currHierarchy.setValue(currHierarchy.getValue() - removed);
				}
			}
			for (int i = 0; i < currHierarchy.getValue(); i++) {
				hierarchyComponentIds = ObjectArrays.concat(hierarchyComponentIds,
						refObjManager.loadRefObjsForDim(currHierarchy.getKey().getLevels().get(i).getId()).keySet().toArray(new Long[0]), Long.class);
			}
		}
		
		grid.reinitializeWHierarchiesWTotals(refObjManager.loadRefObjs(rowDims, grid.getFilter()), refObjManager.loadRefObjs(colDims, grid.getFilter()), hierarchy, newLevel);
		
		Long[] filterRefObjIds = refObjManager.readRefObjIds(grid.getFilter());
		long dimId = dimManager.findDimAggregateId(combinedDims);
		
		List<ResultObject> facts = null;
		switch (grid.getQueryType()) {
			case SINGLE_DIMENSION_W_HIERARCHY: {
				facts = dbReader.loadFactsForSingleDim(grid.getRatio().getId(), dimId, filterRefObjIds);
				break;
			}
			case DIMENSION_COMBINATION_W_HIERARCHY: {
				facts = dbReader.loadFactsForDimCombination(grid.getRatio().getId(), dimId, filterRefObjIds);
				break;
			}
			case MIXED_W_HIERARCHY: {
				Long[] refObjIds = refObjManager.readRefObjIds(combinedDims);
				facts = dbReader.loadFactsForDimCombinationAndRefObjs(grid.getRatio().getId(), dimId, refObjIds, filterRefObjIds);
				break;
			}
			default: {
				break;
			}
		}
		
		if (facts != null) {
			fillDimensionWHierarchy(gridId, facts, hierarchyComponentIds);
		}
	}
	
	private int insertHierarchyLevel(List<DataObject> dims, DimensionHierarchy hierarchy, Dimension currLevel, Dimension newLevel) {
		int currIndex;
		if (currLevel != hierarchy.getTopLevel()) {
			currIndex = dims.indexOf(currLevel);
		} else {
			currIndex = dims.indexOf(hierarchy);
		}
		
		if (currIndex != -1) {
			dims.add(currIndex + 1, newLevel);
			return 1;
		}
		
		return 0;
	}
	
	private int removeHierarchyLevel(List<DataObject> dims, DimensionHierarchy hierarchy, Dimension currLevel) {
		LinkedList<Dimension> levels = hierarchy.getLevels();
		int removed = 0;
		for (int i = levels.indexOf(currLevel) + 1; i < levels.size(); i++) {
			if (dims.remove(levels.get(i))) {
				removed++;
			}
		}
		
		return removed;
	}
}
