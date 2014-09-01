package genericdwh.gui.mainwindow.querypane;

import genericdwh.dataobjects.DataObject;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class DataObjectTableCell extends TableCell<DataObject, DataObject> {
	
	private DataObject obj;
	
	public DataObjectTableCell() {
        setOnDragDetected(new EventHandler<MouseEvent>() {
        	public void handle(MouseEvent event) {
                if (obj == null) {
                    return;
                }
                
                Dragboard dragboard = getTableView().startDragAndDrop(TransferMode.ANY);
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
        
        this.obj = obj = (DataObject)getTableRow().getItem();
        
        setText((obj == null) ? null : obj.toString());
    }
}
