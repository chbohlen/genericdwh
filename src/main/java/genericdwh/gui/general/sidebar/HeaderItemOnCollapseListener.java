package genericdwh.gui.general.sidebar;

import genericdwh.gui.mainwindow.sidebar.LazyLoadDataObjectTreeItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public class HeaderItemOnCollapseListener implements ChangeListener<Boolean> {

	@Override
	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		if (!newValue) {
			((LazyLoadDataObjectTreeItem)((BooleanProperty)observable).getBean()).setExpanded(true);
		}
	}
}
