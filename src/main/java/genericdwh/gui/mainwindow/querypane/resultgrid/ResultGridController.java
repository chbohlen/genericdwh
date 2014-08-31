package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.ResultObject;

import java.util.ArrayList;
import java.util.TreeMap;

import lombok.Setter;

public class ResultGridController {
	
	@Setter private ResultGrid resultGrid;
		
	private ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs;
	private ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs;
	
	public ResultGridController() {
	}
	
	public void initializeGrid(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		this.rowRefObjs = rowRefObjs;
		this.colRefObjs = colRefObjs;
		
		reset();
		
		resultGrid.initialize(rowRefObjs, colRefObjs);
	}

	public void fillSingleRefObj(ResultObject factForRefObj) {
		resultGrid.postResult(factForRefObj.getId(), new Long[] { factForRefObj.getId() }, factForRefObj.getValue());
	}
	
	public void fillRefObjCombination(ResultObject factForRefObj) {
		resultGrid.postResult(factForRefObj.getId(), factForRefObj.getComponentIds(), factForRefObj.getValue());
	}
	
	public void fillSingleDim(ArrayList<ResultObject> factsForDimensions) {
		for (ResultObject currFact : factsForDimensions) {
			resultGrid.postResult(currFact.getId(), new Long[] { currFact.getId() }, currFact.getValue());
		}
		resultGrid.setupTotals(rowRefObjs, colRefObjs);
		resultGrid.calculateAndPostTotals();
	}
	
	public void fillDimCombination(ArrayList<ResultObject> factsForDimensions) {
		for (ResultObject currFact : factsForDimensions) {
			resultGrid.postResult(currFact.getId(), currFact.getComponentIds(), currFact.getValue());
		}
		resultGrid.setupTotals(rowRefObjs, colRefObjs);
		resultGrid.calculateAndPostTotals();
	}
	
	public void reset() {
		resultGrid.cleanUp();
	}
}
