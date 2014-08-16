package genericdwh.gui;

import java.util.Map.Entry;

import genericdwh.dataobjects.DataObject;
import genericdwh.dataobjects.Dimension;
import genericdwh.dataobjects.DimensionManager;
import genericdwh.dataobjects.ReferenceObject;
import genericdwh.dataobjects.ReferenceObjectManager;
import genericdwh.main.Main;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class SidebarController {
	
	private TreeView<DataObject> sidebar;
	
	public SidebarController() {
	}
		
	public void buildSidebar() {
		final DimensionManager dimManager = Main.getContext().getBean(DimensionManager.class);
		final ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
		TreeItem<DataObject> tiDimRoot = new TreeItem<DataObject>(new Dimension(-1, "Dimensions", null));
		tiDimRoot.setExpanded(true);
		
		for (Entry<Long, Dimension> dim : dimManager.loadDimensions().entrySet()) {
			final TreeItem<DataObject> tiDimNode = new TreeItem<DataObject>(dim.getValue());
			tiDimRoot.getChildren().add(tiDimNode);
			
			if (dimManager.dimensionHasRecords(dim.getValue())) {
				tiDimNode.getChildren().add(new TreeItem<DataObject>(new Dimension(-1, "DummyEntry", null)));
				tiDimNode.expandedProperty().addListener(new ChangeListener<Boolean>() {
				    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				    	if (newValue) {
							@SuppressWarnings("unchecked")
							TreeItem<Dimension> tiDim = (TreeItem<Dimension>)((BooleanProperty)observable).getBean();
				    		
				    		tiDim.getChildren().clear();
				    		
				    		for (Entry<Long, ReferenceObject> refObj : refObjManager.loadReferenceObjectsForDimension(tiDim.getValue()).entrySet()) {
				    			TreeItem<DataObject> tiRefObjNode = new TreeItem<DataObject>(refObj.getValue());
				    			tiDimNode.getChildren().add(tiRefObjNode);
				    		}
				    	}				        
				    }
				});
			}
		}
		
		sidebar.setRoot(tiDimRoot);
	}

	public void showSidebar() {
		sidebar.setVisible(true);
	}
	
	public void hideSidebar() {
		sidebar.setVisible(false);
	}
	
	public void setSidebar(TreeView<DataObject> sidebar) {
		this.sidebar = sidebar;
	}
}
