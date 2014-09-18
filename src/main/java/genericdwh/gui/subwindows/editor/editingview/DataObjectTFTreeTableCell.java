package genericdwh.gui.subwindows.editor.editingview;

import genericdwh.dataobjects.DataObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;
import lombok.Getter;

public class DataObjectTFTreeTableCell extends TextFieldTreeTableCell<DataObject, String> {
	
	@Getter private StringProperty title = new SimpleStringProperty("");
	
	public DataObjectTFTreeTableCell(StringConverter<String> converter) {
		super(converter);
	}
	
    @Override
	public void updateItem(String obj, boolean empty) {
        super.updateItem(obj, empty);

        if (obj != null) {
        	setText(obj.toString());
            title.set(obj.toString());
            
            if (!getTreeTableView().getTreeItem(getTreeTableRow().getIndex()).getChildren().isEmpty()) {
            	setEditable(false);
            }
        } else {
        	setText(null);
        }

    }
}
