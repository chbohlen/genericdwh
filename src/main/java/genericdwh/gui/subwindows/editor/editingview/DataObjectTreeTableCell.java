package genericdwh.gui.subwindows.editor.editingview;

import genericdwh.dataobjects.DataObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import lombok.Getter;

public class DataObjectTreeTableCell<T> extends ComboBoxTreeTableCell<DataObject, T> {
	@Getter private StringProperty title = new SimpleStringProperty("");
	
	public DataObjectTreeTableCell(ObservableList<T> items) {
		super(items);
	}
	
    @Override
	public void updateItem(T obj, boolean empty) {
        super.updateItem(obj, empty);

        if (obj != null) {
        	setText(obj.toString());
            title.set(obj.toString());
        } else {
        	setText(null);
        }
    }
}
