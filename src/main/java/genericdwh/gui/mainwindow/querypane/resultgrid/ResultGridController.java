package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.referenceobject.ReferenceObject;

import java.util.ArrayList;
import java.util.Map.Entry;
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

	public void fillSingleRefObj(Entry<Long, Double> factForRefObj) {
		resultGrid.postResult(factForRefObj.getKey(), new Long[] { factForRefObj.getKey() }, factForRefObj.getValue());
	}
	
	public void fillRefObjCombination(Entry<Long, Entry<Long[], Double>> factForRefObj) {
		resultGrid.postResult(factForRefObj.getKey(), factForRefObj.getValue().getKey(), factForRefObj.getValue().getValue());
	}
	
	public void fillSingleDim(TreeMap<Long, Double> factsForDimensions) {
		for (Entry<Long, Double> currFact : factsForDimensions.entrySet()) {
			resultGrid.postResult(currFact.getKey(), new Long[] { currFact.getKey() }, currFact.getValue());
		}
		resultGrid.setupTotals(rowRefObjs, colRefObjs);
		resultGrid.calculateAndPostTotals();
	}
	
	public void fillDimCombination(TreeMap<Long, Entry<Long[], Double>> factsForDimensions) {
		for (Entry<Long, Entry<Long[], Double>> currFact : factsForDimensions.entrySet()) {
			resultGrid.postResult(currFact.getKey(), currFact.getValue().getKey(), currFact.getValue().getValue());
		}
		resultGrid.setupTotals(rowRefObjs, colRefObjs);
		resultGrid.calculateAndPostTotals();
	}
	
	public void reset() {
		resultGrid.cleanUp();
	}
}
