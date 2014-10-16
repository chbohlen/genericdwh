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
	
	public static Ratio SELECT_RATIO = new Ratio(-1, "Select Ratio ...", 0);
	static { SELECT_RATIO.initProperties();	}
	
	public Ratio(long id, String name, long categoryId) {
		super(id, name);

		this.categoryId = categoryId;
	}
	
	public void addDependency(Ratio newDependency) {
		dependencies.add(newDependency);
	}
	
	public void clearDependecies() {
		dependencies.clear();
	}
	
	public boolean isRelation() {
		return !dependencies.isEmpty();
	}

	@Override
	public void initProperties() {
		super.initProperties();
		if (categoryId == 0) {
			setCategoryProperty(RatioCategory.NO_RATIO_CATEGORY);
		} else {
			setCategoryProperty(Main.getContext().getBean(RatioManager.class).getCategories().get(categoryId));
		}
	}
	
	@Getter private ObjectProperty<RatioCategory> categoryProperty = new SimpleObjectProperty<>();
	public void setCategoryProperty(RatioCategory cat) { categoryProperty.set(cat); };
}
