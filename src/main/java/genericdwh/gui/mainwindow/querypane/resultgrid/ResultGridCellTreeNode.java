package genericdwh.gui.mainwindow.querypane.resultgrid;

import java.util.TreeMap;

import lombok.Getter;

public class ResultGridCellTreeNode {
	
	@Getter private long id;
	@Getter private ResultGridCell cell; 
	
	@Getter private boolean isLeaf;
		
	@Getter private TreeMap<Long, ResultGridCellTreeNode> children = new TreeMap<Long, ResultGridCellTreeNode>();
	
	public ResultGridCellTreeNode(long id, ResultGridCell cell) {
		this.id = id;
		this.cell = cell;

		isLeaf = (cell != null);
	}
	
	public void addChild(ResultGridCellTreeNode newChild) {
		children.put(newChild.getId(), newChild);
	}
}
