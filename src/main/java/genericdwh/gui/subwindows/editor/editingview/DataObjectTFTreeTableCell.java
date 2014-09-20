package genericdwh.gui.subwindows.editor.editingview;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.DataObjectCombination;
import genericdwh.dataobjects.DataObjectHierarchy;
import genericdwh.gui.general.sidebar.HeaderItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.util.StringConverter;

public class DataObjectTFTreeTableCell extends TextFieldTreeTableCell<DataObject, String> {
		
	public DataObjectTFTreeTableCell(StringConverter<String> converter) {
		super(converter);
	}
	
    @Override
	public void updateItem(String obj, boolean empty) {
        super.updateItem(obj, empty);

        if (obj != null) {
        	setText(obj.toString());
            TreeItem<DataObject> item = getTreeTableView().getTreeItem(getTreeTableRow().getIndex());
            if (item instanceof HeaderItem || (item != null
    						            		&& (item.getValue() instanceof DataObjectHierarchy
    						            			|| item.getValue() instanceof DataObjectCombination))) {
            	setEditable(false);
            }
        } else {
        	setText(null);
        }
    }
}
