package genericdwh.dataobjects.dimension;

import genericdwh.dataobjects.DataObject;

import java.util.LinkedList;

import lombok.Getter;

public class DimensionHierarchy extends DataObject {
	
	@Getter private long categoryId;
	
	@Getter private LinkedList<Dimension> levels;
	
	public DimensionHierarchy() {
		super();
		
		levels = new LinkedList<>();
	}
	
	public DimensionHierarchy(Dimension top) {
		this();
		
		addLevel(top);
	}
	
	public DimensionHierarchy(LinkedList<Dimension> levels) {
		this();

		for (Dimension level : levels) {
			addLevel(level);
		}
	}
	
	public void addLevel(Dimension level) {
		if (!name.isEmpty()) {
			name += "-";
		}
		name += level.getName();
		
		if (levels.isEmpty()) {
			categoryId = level.getCategoryId();
		}
		
		levels.add(level);
	}
	
	public Dimension getTopLevel() {
		return levels.get(0);
	}
	
	@Override
	public DimensionHierarchy clone()  {
		return new DimensionHierarchy(levels);
	}
}
