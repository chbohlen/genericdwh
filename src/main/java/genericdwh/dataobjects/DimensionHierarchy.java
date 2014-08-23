package genericdwh.dataobjects;

import java.util.LinkedList;

public class DimensionHierarchy extends DataObject {
	
	private LinkedList<Dimension> levels;
	
	public DimensionHierarchy() {
		super();
		
		levels = new LinkedList<Dimension>();
	}
	
	public DimensionHierarchy(Dimension root) {
		this();
		
		addLevel(root);
	}
	
	public DimensionHierarchy(LinkedList<Dimension> levels) {
		this();

		for (Dimension level : levels) {
			addLevel(level);
		}
	}
	
	public void addLevel(Dimension level) {
		levels.add(level);
		
		if (!name.isEmpty()) {
			name += "-";
		}
		name += level.getName();
	}
	
	public LinkedList<Dimension> getLevels() {
		return levels;
	}
	
	@Override
	public DimensionHierarchy clone()  {
		return new DimensionHierarchy(levels);
	}
}
