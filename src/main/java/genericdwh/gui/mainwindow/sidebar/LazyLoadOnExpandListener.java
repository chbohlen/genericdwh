package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.main.Main;

import java.util.TreeMap;
import java.util.Map.Entry;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.ImageView;

public 	class LazyLoadOnExpandListener implements ChangeListener<Boolean> {

	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
		
    	if (newValue) {
    		if (((BooleanProperty)observable).getBean() instanceof HeaderItem) {
    			return;
    		}
    		
			LazyLoadDataObjectTreeItem tiObj = (LazyLoadDataObjectTreeItem)((BooleanProperty)observable).getBean();
			
			if (tiObj.getFirstChild() == null) {
				return;
			}
			
			if (tiObj.isLoaded()
					|| tiObj.getValue() instanceof DimensionCategory || tiObj.getValue() instanceof RatioCategory
					|| tiObj.getValue() instanceof Ratio) {
				
				return;
			}
    		
    		TreeMap<Long, ReferenceObject> refObjs;
    		if (tiObj.getValue() instanceof ReferenceObject) {
    			refObjs = refObjManager.loadRefObjsForDimAndRefObjParent(tiObj.getFirstChild().getValue().getId(), tiObj.getValue().getId());
    		} else {
    			refObjs = refObjManager.loadRefObjsForDim(tiObj.getFirstChild().getValue().getId());
    		}
    		
    		LazyLoadDataObjectTreeItem nextLvl = tiObj.getFirstChild().getFirstChild();
    		
    		tiObj.getChildren().clear();
    		
    		for (Entry<Long, ReferenceObject> currEntry : refObjs.entrySet()) {
    			LazyLoadDataObjectTreeItem tiRefObjNode = new LazyLoadDataObjectTreeItem(currEntry.getValue(), new ImageView(Icons.REFERENCE_OBJECT));
    			if (nextLvl != null && refObjManager.dimensionAndRefObjParentHaveRecords((Dimension)nextLvl.getValue(), currEntry.getValue())) {
	    			tiRefObjNode.addChild(nextLvl);
    			}
    			tiObj.addChild(tiRefObjNode);
    		}
    		
    		tiObj.setLoaded(true);
    	}
	}
}
