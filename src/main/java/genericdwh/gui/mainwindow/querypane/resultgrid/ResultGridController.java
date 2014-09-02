package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.ResultObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.TreeMap;

public class ResultGridController {
	
	private LinkedList<ResultGrid> resultGrids = new LinkedList<>();
			
	public ResultGridController() {
	}
	
	public void initializeGrid(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, String ratioName, String unitSymbol) {
		resultGrids.getLast().initialize(rowRefObjs, colRefObjs, ratioName, unitSymbol);
	}
	
	public void initializeGridWithTotals(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, String ratioName, String unitSymbol) {		
		resultGrids.getLast().initializeWithTotals(rowRefObjs, colRefObjs, ratioName, unitSymbol);
	}

	public void initializeGridNoData(String ratioName) {
		resultGrids.getLast().initializeNoData(ratioName);
	}

	public void fillReferenceObject(ResultObject factForRefObj) {
		resultGrids.getLast().postResult(factForRefObj.getId(), factForRefObj.getComponentIds(), factForRefObj.getValue());
	}
	
	public void fillDimension(ArrayList<ResultObject> factsForDimensions) {
		for (ResultObject currFact : factsForDimensions) {
			resultGrids.getLast().postResult(currFact.getId(), currFact.getComponentIds(), currFact.getValue());
		}
		resultGrids.getLast().calculateAndPostTotals();
	}
	
	public void fillRatio(ArrayList<ResultObject> factsForRatio) {
		for (ResultObject currFact : factsForRatio) {
			resultGrids.getLast().postResult(currFact.getId(), currFact.getComponentIds(), currFact.getValue());
		}
		// TODO
	}
	
	public void addResultGrid(ResultGrid newResultGrid) {
		resultGrids.add(newResultGrid);	
	}
	
	public void reset() {
		resultGrids.clear();
	}
}
