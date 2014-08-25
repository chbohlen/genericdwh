package genericdwh.dataobjects.ratio;

import genericdwh.dataobjects.DataObject;

import java.util.LinkedList;

import lombok.Getter;

public class RatioRelation extends DataObject {
	
	@Getter private String category;
	
	@Getter private LinkedList<Ratio> levels;
	
	public RatioRelation() {
		super();
		
		levels = new LinkedList<Ratio>();
	}
	
	public RatioRelation(Ratio root) {
		this();
		
		addLevel(root);
	}
	
	public RatioRelation(LinkedList<Ratio> levels) {
		this();

		for (Ratio level : levels) {
			addLevel(level);
		}
	}
	
	public void addLevel(Ratio level) {
		if (!name.isEmpty()) {
			name += "-";
		}
		name += level.getName();
		
		if (levels.isEmpty()) {
			category = level.getCategory();
		}
		
		levels.add(level);
	}
	
	@Override
	public RatioRelation clone()  {
		return new RatioRelation(levels);
	}
}
