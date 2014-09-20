package genericdwh.gui.mainwindow.querypane.resultgrid;

import java.util.List;
import java.util.TreeMap;

import lombok.Getter;
import lombok.Setter;

public class ResultGridNode {
	
	@Getter @Setter private long id;
	@Getter @Setter private String name;
	@Getter @Setter private long dimensionId;
	@Getter private List<Long> successorIds;
	
	@Getter @Setter private Long[] componentIds;
	
	@Getter @Setter private ResultGridCell cell;
	
	@Getter private boolean isLeaf;
		
	@Getter private TreeMap<Long, ResultGridNode> children = new TreeMap<>();
	
	public ResultGridNode(long id, ResultGridCell cell) {
		this.id = id;
		this.cell = cell;
				
		isLeaf = true;
	}
	
	public ResultGridNode(long id, String name, long dimensionId, List<Long> successorIds) {
		this.id = id;
		this.name = name;
		this.dimensionId = dimensionId;
		this.successorIds = successorIds;
		
		isLeaf = true;
	}

	
	public void addChild(long dimensionId, ResultGridNode newChild) {
		children.put(dimensionId, newChild);
		isLeaf = false;
	}
	
	public ResultGridNode cloneWOCell() {
		return new ResultGridNode(this.id, this.name, this.dimensionId, this.successorIds);
	}
}
