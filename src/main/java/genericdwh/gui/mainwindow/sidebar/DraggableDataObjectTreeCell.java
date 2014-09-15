package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.gui.general.sidebar.DataObjectTreeCell;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.event.EventHandler;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class DraggableDataObjectTreeCell extends DataObjectTreeCell {
			
	public DraggableDataObjectTreeCell() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent event) {
        		DataObject item = getItem();
                if (item == null
                		|| getTreeItem() instanceof HeaderItem
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
}
