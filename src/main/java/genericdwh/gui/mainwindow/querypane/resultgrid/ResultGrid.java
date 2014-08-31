package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.referenceobject.ReferenceObject;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.scene.layout.GridPane;

public class ResultGrid extends GridPane {
		
	private ResultGridManager gridManager;

	public ResultGrid(ResultGridManager gridManager) {
		super();
		
		this.gridManager = gridManager;
		
		setLayoutY(150);
		setLayoutX(25);
		
		setPrefHeight(525);
		setPrefWidth(750);
	}
	
	public void create(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		reset();
		
		gridManager.initialize(rowRefObjs, colRefObjs);
	}

	public void fillSingleRefObj(Entry<Long, Double> factForRefObj) {
		gridManager.postResult(factForRefObj.getKey(), new Long[] { factForRefObj.getKey() }, factForRefObj.getValue());
		gridManager.calculateAndPostTotals();
	}
	
	public void fillRefObjCombination(Entry<Long, Entry<Long[], Double>> factForRefObj) {
		gridManager.postResult(factForRefObj.getKey(), factForRefObj.getValue().getKey(), factForRefObj.getValue().getValue());
		gridManager.calculateAndPostTotals();

	}
	
	public void fillSingleDim(TreeMap<Long, Double> factsForDimensions) {
		for (Entry<Long, Double> currFact : factsForDimensions.entrySet()) {
			gridManager.postResult(currFact.getKey(), new Long[] { currFact.getKey() }, currFact.getValue());
		}
		gridManager.calculateAndPostTotals();
	}
	
	public void fillDimCombination(TreeMap<Long, Entry<Long[], Double>> factsForDimensions) {
		for (Entry<Long, Entry<Long[], Double>> currFact : factsForDimensions.entrySet()) {
			gridManager.postResult(currFact.getKey(), currFact.getValue().getKey(), currFact.getValue().getValue());
		}
		gridManager.calculateAndPostTotals();
	}
	
	public void reset() {
		getChildren().clear();
	}
}
