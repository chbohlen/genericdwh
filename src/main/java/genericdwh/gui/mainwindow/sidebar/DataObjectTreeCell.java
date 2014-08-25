package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class DataObjectTreeCell extends TreeCell<DataObject>{
	
	private DataObject obj;
	
	public DataObjectTreeCell() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent event) {
                if (obj == null || obj instanceof SidebarHeader
                		|| obj instanceof DimensionCategory
                		|| obj instanceof RatioCategory) {
                	
                    return;
                }
                
                Dragboard dragboard = getTreeView().startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(obj.getName());
                dragboard.setContent(content);
                
                Main.getContext().getBean(MainWindowController.class).setDraggedDataObject(obj);
                
                event.consume();
            }
        });
	}
	
    @Override
    protected void updateItem(DataObject obj, boolean empty) {
        super.updateItem(obj, empty);
        
        this.obj = obj;
        
        setText((obj == null) ? null : obj.toString());
    }
}
