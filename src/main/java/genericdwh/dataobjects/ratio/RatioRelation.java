package genericdwh.dataobjects.ratio;

import java.util.LinkedList;

import genericdwh.dataobjects.DataObjectHierarchy;
import genericdwh.main.Main;

public class RatioRelation extends DataObjectHierarchy<Ratio> {
		
	public RatioRelation() {
		super();
	}
	
	public RatioRelation(Ratio ratio) {
		super(ratio);
	}
	
	public RatioRelation(LinkedList<Ratio> levels) {
		super(levels);
	}
	
	@Override
	public void addLevel(Ratio level) {		
		if (levels.isEmpty()) {
			categoryId = level.getCategoryId();
		}
		levels.add(level);
		
		name = generateName(levels);
	}
	
	@Override
	public RatioRelation clone() {
		return new RatioRelation(levels);
	}
	
	@Override
	public void initProperties() {
		super.initProperties();
		setCategoryProperty(Main.getContext().getBean(RatioManager.class).getCategories().get(categoryId));
	}
}
