package genericdwh.dataobjects.ratio;

import java.util.ArrayList;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.Setter;
import genericdwh.dataobjects.DataObject;
import genericdwh.main.Main;

public class Ratio extends DataObject {
	
	@Getter @Setter private Long categoryId;
	
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
	
	@Override
	public void initProperties() {
		super.initProperties();
		setCategoryProperty(Main.getContext().getBean(RatioManager.class).getCategories().get(categoryId));
	}
	
	@Getter private ObjectProperty<RatioCategory> categoryProperty = new SimpleObjectProperty<>();
	public void setCategoryProperty(RatioCategory cat) { categoryProperty.set(cat); };
}
