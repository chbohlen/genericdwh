package genericdwh.dataobjects.ratio;

import java.util.ArrayList;

import lombok.Getter;
import genericdwh.dataobjects.DataObject;
public class Ratio extends DataObject {
	
	@Getter private long id;
	@Getter private String category;
	
	@Getter private ArrayList<Ratio> dependencies;
	
	public Ratio(long id, String name, String category) {
		super(name);
		
		this.id = id;
		this.category = category;
		
		dependencies = new ArrayList<Ratio>();
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