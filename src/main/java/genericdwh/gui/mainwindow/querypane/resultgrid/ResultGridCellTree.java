package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.main.Main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ResultGridCellTree {
	
	private ResultGrid resultGrid = Main.getContext().getBean(ResultGrid.class);
	
	private TreeMap<Long, ResultGridCellTreeNode> root;

	public ResultGridCellTree(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		generateResultGridCellTree(rowRefObjs, colRefObjs);
	}

	private void generateResultGridCellTree(ArrayList<TreeMap<Long, ReferenceObject>> rowRefObjs, ArrayList<TreeMap<Long, ReferenceObject>> colRefObjs) {
		LinkedList<ResultGridCellTreeNode> queue = new LinkedList<ResultGridCellTreeNode>();
		LinkedList<ResultGridCellTreeNode> tmpQueue = new LinkedList<ResultGridCellTreeNode>();
		
		root = new TreeMap<Long, ResultGridCellTreeNode>();
		for (Entry<Long, ReferenceObject> rootEntry : rowRefObjs.get(0).entrySet()) {
			ResultGridCellTreeNode newRootNode = new ResultGridCellTreeNode(rootEntry.getKey(), null);
			root.put(newRootNode.getId(), newRootNode);
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
		
		int rowOffset = colRefObjs.size();
		int colOffset = rowRefObjs.size();
		
		int rowIndex = rowOffset;
		int colIndex = colOffset;

		int maxColCount = 1;
		for (int j = 0; j < colRefObjs.size(); j++) {
			boolean lastDim = (j == colRefObjs.size()-1);
			maxColCount *= colRefObjs.get(j).size();
			while (!queue.isEmpty()) {
				ResultGridCellTreeNode currNode = queue.pop();
				for (Entry<Long, ReferenceObject> currCol : colRefObjs.get(j).entrySet()) {
					ResultGridCell newCell = null;
					if (lastDim) {
						newCell = new ResultGridCell("no data");
						resultGrid.add(newCell, colIndex, rowIndex);
					}
					ResultGridCellTreeNode newNode = new ResultGridCellTreeNode(currCol.getKey(), newCell);
					currNode.addChild(newNode);
					tmpQueue.add(newNode);

					if (lastDim && ++colIndex == maxColCount + colOffset) {
						colIndex = colOffset;
					}
				}
				
				if (lastDim && colIndex == colOffset) {
					rowIndex++;
				}
			}
			
			queue.addAll(tmpQueue);
			tmpQueue.clear();
		}
	}
	
	public ResultGridCell getResultGridCell(Long[] combination) {
		TreeMap<Long, ResultGridCellTreeNode> currNodes = root;
		ResultGridCell cell = null;	
		while (cell == null) {
			for (long componentId : combination) {
				ResultGridCellTreeNode foundNode = currNodes.get(componentId);
				if (foundNode != null) {
					if (foundNode.isLeaf()) {
						cell = foundNode.getCell();
					} else {
						currNodes = foundNode.getChildren();
					}
					break;
				}
			}
		}
		
		return cell;
	}
}
