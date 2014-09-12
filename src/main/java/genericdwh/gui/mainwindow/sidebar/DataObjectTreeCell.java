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
import lombok.Getter;

public class DataObjectTreeCell extends TreeCell<DataObject>{
	
	@Getter private DataObject dataObj;
		
	public DataObjectTreeCell() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent event) {
                if (dataObj == null
                		|| dataObj instanceof SidebarHeader
                		|| dataObj instanceof DimensionCategory
                		|| dataObj instanceof RatioCategory) {
                	
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
        
        this.dataObj = obj;
        
        setText((dataObj == null) ? null : dataObj.toString());
    }
}
