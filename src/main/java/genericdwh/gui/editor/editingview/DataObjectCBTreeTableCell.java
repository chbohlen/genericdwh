package genericdwh.gui.editor.editingview;

import genericdwh.dataobjects.DataObject;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.ComboBoxTreeTableCell;

public class DataObjectCBTreeTableCell<T extends DataObject> extends ComboBoxTreeTableCell<DataObject, T> {
	
	public DataObjectCBTreeTableCell(ObservableList<T> items) {
		super(items);
	}
	
    @Override
	public void updateItem(T obj, boolean empty) {
        super.updateItem(obj, empty);
        if (obj != null) {
        	setEditable(true);
        } else {
        	setText(null);
        	setEditable(false);
        }
    }
}
