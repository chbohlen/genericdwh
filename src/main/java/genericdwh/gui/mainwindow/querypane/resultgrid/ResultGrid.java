package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.referenceobject.ReferenceObject;

import java.util.ArrayList;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.scene.layout.GridPane;

public class ResultGrid extends GridPane {
	
	private ResultGridCellTree resultGridTree;

	public ResultGrid() {
		super();
		
		setLayoutY(150);
		setLayoutX(25);
		
		setPrefHeight(525);
		setPrefWidth(750);
	}
	
	public void create(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		reset();
		
		generateHeadersForRefObjs(rowRefObjs, colRefObjs.size(), true);
		generateHeadersForRefObjs(colRefObjs, rowRefObjs.size(), false);
		
		resultGridTree = new ResultGridCellTree(rowRefObjs, colRefObjs);
	}

	private void generateHeadersForRefObjs(ArrayList<TreeMap<Long, ReferenceObject>> refObjs, int baseOffset, boolean isRowHeader) {
		for (int i = 0; i < refObjs.size(); i++) {
			int offset = baseOffset;
			for (Entry<Long, ReferenceObject> currEntry : refObjs.get(i).entrySet()) {
				int max;
				if (i > 0) {
					max = refObjs.get(i-1).size();
				} else {
					max = 1;
				}
				
				int j = offset;
				for (int k = 0; k < max; k++) {
					if (isRowHeader) {
						add(new ResultGridCell(currEntry.getValue().getName()), i, j);
					} else {
						add(new ResultGridCell(currEntry.getValue().getName()), j, i);
					}

					j += refObjs.get(i).size();
				}
				
				if (i+1 < refObjs.size()) {
					offset += refObjs.get(i+1).size();
				} else {
					offset++;
				}
			}
		}
	}
	
	public void fillWith(SimpleEntry<Double, Long[]> factForRefObj) {
		ResultGridCell cell = resultGridTree.getResultGridCell(factForRefObj.getValue());
		cell.setValue(factForRefObj.getKey().toString());
	}
	
	public void fillWith(TreeMap<Long, SimpleEntry<Double, Long[]>> factsForDimensions) {
		ResultGridCell cell;
		for (Entry<Long, SimpleEntry<Double, Long[]>> currFact : factsForDimensions.entrySet()) {
			cell = resultGridTree.getResultGridCell(currFact.getValue().getValue());
			cell.setValue(currFact.getValue().getKey().toString());
		}
	}
	
	public void reset() {
		getChildren().clear();
	}
}
