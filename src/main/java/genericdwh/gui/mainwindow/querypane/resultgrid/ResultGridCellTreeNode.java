package genericdwh.gui.mainwindow.querypane.resultgrid;

import java.util.ArrayList;

import lombok.Getter;

public class ResultGridCellTreeNode {
	
	@Getter private long id;
	@Getter private ResultGridCell cell; 
	
	@Getter private boolean isLeaf;
	
	@Getter ArrayList<ResultGridCellTreeNode> children;
	
	public ResultGridCellTreeNode(long id, ResultGridCell cell) {
		this.id = id;
		this.cell = cell;

		isLeaf = (cell != null);
		
		children = new ArrayList<ResultGridCellTreeNode>();
	}
	
	public void addChild(ResultGridCellTreeNode newChild) {
		children.add(newChild);
	}
}
