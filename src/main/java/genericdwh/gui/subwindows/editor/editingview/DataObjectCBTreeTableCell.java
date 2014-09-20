package genericdwh.gui.subwindows.editor.editingview;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.gui.general.sidebar.HeaderItem;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import lombok.Getter;

public class DataObjectCBTreeTableCell<T extends DataObject> extends ComboBoxTreeTableCell<DataObject, T> {
	
	@Getter private StringProperty title = new SimpleStringProperty("");
	
	public DataObjectCBTreeTableCell(ObservableList<T> items) {
		super(items);
	}
	
    @Override
	public void updateItem(T obj, boolean empty) {
        super.updateItem(obj, empty);

        if (obj != null) {
        	setText(obj.toString());
            title.set(obj.toString());
            
            TreeItem<DataObject> item = getTreeTableView().getTreeItem(getTreeTableRow().getIndex());
            if (item instanceof HeaderItem || (item != null && item.getValue() instanceof DimensionHierarchy)) {
            	setEditable(false);
            }
        } else {
        	setText(null);
        }
    }
}
