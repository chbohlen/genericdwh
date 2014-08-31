package genericdwh.dataobjects.ratio;

import java.util.ArrayList;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;
public class Ratio extends DataObject {
	
	@Getter private long categoryId;
	
	@Getter private ArrayList<Ratio> dependencies = new ArrayList<Ratio>();
	
	public Ratio(long id, String name, long categoryId) {
		super(id, name);

		this.categoryId = categoryId;
	}
	
	public void addDependency(Ratio newChildren) {
		dependencies.add(newChildren);
	}
	
	public boolean isRelation() {
		return !dependencies.isEmpty();
	}
	
	public int getDependencyCount() {
		return dependencies.size();
	}
}
