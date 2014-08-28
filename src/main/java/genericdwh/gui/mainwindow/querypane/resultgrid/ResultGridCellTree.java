package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.referenceobject.ReferenceObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ResultGridCellTree {
	
	private ResultGrid resultGrid;
	private ArrayList<ResultGridCellTreeNode> root;
	
	// TODO: DI
	public ResultGridCellTree(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs, ResultGrid resultGrid) {
		this.resultGrid = resultGrid;
		generateResultGridCellTree(rowRefObjs, colRefObjs);
	}

	private void generateResultGridCellTree(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		LinkedList<ResultGridCellTreeNode> queue = new LinkedList<ResultGridCellTreeNode>();
		LinkedList<ResultGridCellTreeNode> tmpQueue = new LinkedList<ResultGridCellTreeNode>();
		
		root = new ArrayList<ResultGridCellTreeNode>();
		for (Entry<Long, ReferenceObject> rootEntry : rowRefObjs.get(0).entrySet()) {
			ResultGridCellTreeNode newRootNode = new ResultGridCellTreeNode(rootEntry.getKey(), null);
			root.add(newRootNode);
			queue.add(newRootNode);
		}
		
		for (int i = 1; i < rowRefObjs.size(); i++) {			
			while (!queue.isEmpty()) {
				ResultGridCellTreeNode currNode = queue.pop();
				for (Entry<Long, ReferenceObject> currRow : rowRefObjs.get(i).entrySet()) {
					ResultGridCellTreeNode newNode = new ResultGridCellTreeNode(currRow.getKey(), null);
					currNode.addChild(newNode);
					tmpQueue.add(newNode);
				}	
			}
			queue.addAll(tmpQueue);
			tmpQueue.clear();
		}
		
		int baseOffsetRow = colRefObjs.size();
		
		int offsetCol = rowRefObjs.size();
		int offsetRow = baseOffsetRow;
		
		int rowCount = queue.size();
		int currMainRow = 0;
		for (int j = 0; j < colRefObjs.size(); j++) {
			while (!queue.isEmpty()) {
				ResultGridCellTreeNode currNode = queue.pop();
				for (Entry<Long, ReferenceObject> currCol : colRefObjs.get(j).entrySet()) {
					ResultGridCell newCell = null;
					if (j == colRefObjs.size()-1) {
						newCell = new ResultGridCell(offsetCol + "," + offsetRow);
						resultGrid.add(newCell, offsetCol, offsetRow);
					}
					ResultGridCellTreeNode newNode = new ResultGridCellTreeNode(currCol.getKey(), newCell);
					currNode.addChild(newNode);
					tmpQueue.add(newNode);

					if (++offsetRow == rowCount) {
						offsetRow = baseOffsetRow;
					}
				}
				if (++currMainRow == root.size()) {
					offsetCol++;
					currMainRow = 0;
				}
			}
			queue.addAll(tmpQueue);
			tmpQueue.clear();
		}
	}
}
