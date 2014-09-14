package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.gui.general.sidebar.SidebarHeader;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import lombok.Getter;

public class DataObjectTreeCell extends TreeCell<DataObject>{
		
	@Getter private StringProperty title = new SimpleStringProperty("");
		
	public DataObjectTreeCell() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent event) {
        		DataObject item = getItem();
                if (item == null
                		|| getTreeItem() instanceof SidebarHeader
                		|| item instanceof DimensionCategory
                		|| item instanceof RatioCategory) {
                	
                    return;
                }
                
                Dragboard dragboard = getTreeView().startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(getItem().getName());
                dragboard.setContent(content);
                
                Main.getContext().getBean(MainWindowController.class).setDraggedDataObject(getItem());
                
                event.consume();
            }
        });        
	}
	
    @Override
    protected void updateItem(DataObject obj, boolean empty) {
        super.updateItem(obj, empty);

        if (obj != null) {
        	setText(obj.toString());
            title.set(obj.toString());
        } else {
        	setText(null);
        }
    }
}
