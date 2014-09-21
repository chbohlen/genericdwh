package genericdwh.gui.general.sidebar;

import genericdwh.dataobjects.DataObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TreeCell;
import lombok.Getter;

public class DataObjectTreeCell extends TreeCell<DataObject> {
		
	@Getter private StringProperty title = new SimpleStringProperty("");
	
    @Override
    public void updateItem(DataObject obj, boolean empty) {
        super.updateItem(obj, empty);

        if (obj != null) {
        	setText(obj.toString());
        	setGraphic(getTreeItem().getGraphic());
            title.set(obj.toString());
        } else {
        	setText(null);
        	setGraphic(null);
        }
    }
}
