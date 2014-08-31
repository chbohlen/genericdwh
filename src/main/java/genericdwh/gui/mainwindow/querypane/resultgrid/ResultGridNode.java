package genericdwh.gui.mainwindow.querypane.resultgrid;

import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

public class ResultGridNode {
	
	@Getter @Setter private long id;
	@Getter @Setter private long dimensionId;
	@Getter @Setter private Long[] componentIds;
	
	@Getter private ResultGridCell cell;
	
	@Getter private boolean isLeaf;
		
	@Getter private TreeMap<Long, ResultGridNode> children = new TreeMap<>();
	
	public ResultGridNode(long id, ResultGridCell cell) {
		this.id = id;
		this.cell = cell;
				
		isLeaf = true;
	}
	
	public void addChild(long dimensionId, ResultGridNode newChild) {
		children.put(dimensionId, newChild);
		isLeaf = false;
	}
	
	public void setChildren(TreeMap<Long, ResultGridNode> newChildren) {
		children = newChildren;
		isLeaf = false;
	}
}
