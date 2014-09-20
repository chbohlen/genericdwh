package genericdwh.gui.general.sidebar;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class HeaderItemOnCollapseListener implements ChangeListener<Boolean> {

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (!newValue) {
			((DataObjectTreeItem)((BooleanProperty)observable).getBean()).setExpanded(true);
		}
	}
}
