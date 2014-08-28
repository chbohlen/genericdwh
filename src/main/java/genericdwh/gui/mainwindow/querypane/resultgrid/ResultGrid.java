package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.db.DatabaseController;
import genericdwh.db.DatabaseReader;
import genericdwh.main.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javafx.scene.layout.GridPane;

public class ResultGrid extends GridPane {
	
	private DatabaseReader dbReader;
	
	private ResultGridCellTree resultGridTree;

	public ResultGrid(List<DataObject> rowDims, List<DataObject> colDims) {
		super();
		
		setPrefHeight(300);
		setPrefWidth(600);
		
		dbReader = Main.getContext().getBean(DatabaseController.class).getDbReader();
		
		ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs = loadRefObjsForDims(rowDims);
		ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs = loadRefObjsForDims(colDims);
		
		generateHeadersForRefObjs(rowRefObjs, colRefObjs.size(), true);
		generateHeadersForRefObjs(colRefObjs, rowRefObjs.size(), false);
		
		resultGridTree = new ResultGridCellTree(rowRefObjs, colRefObjs, this);
	}

	private void generateHeadersForRefObjs(ArrayList<TreeMap<Long, ReferenceObject>> refObjs, int baseOffset, boolean isRowHeader) {
		for (int i = 0; i < refObjs.size(); i++) {
			int offset = baseOffset;
			for (Entry<Long, ReferenceObject> currEntry : refObjs.get(i).entrySet()) {
				int max = 1;
				if (i > 0) {
					max = refObjs.get(i-1).size();
				}
				
				int addOffset = offset;
				for (int j = 0; j < max; j++) {
					if (isRowHeader) {
						add(new ResultGridCell(currEntry.getValue().getName()), i, addOffset);
					} else {
						add(new ResultGridCell(currEntry.getValue().getName()), addOffset, i);
					}

					addOffset += refObjs.get(i).size();
				}
				
				offset++;
			}
		}
	}

	private ArrayList<TreeMap<Long, ReferenceObject>> loadRefObjsForDims(List<DataObject> dims) {
		ArrayList<TreeMap<Long, ReferenceObject>> result = new ArrayList<TreeMap<Long, ReferenceObject>>();
		for (DataObject dim : dims) {
			if (dim instanceof DimensionHierarchy) {
				dim = ((DimensionHierarchy)dim).getTop();
			}
			result.add(dbReader.loadRefObjsForDim(((Dimension)dim).getId()));
		}
		
		return result;
	}
}
