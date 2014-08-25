package genericdwh.gui.mainwindow.sidebar;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionCategory;
import genericdwh.dataobjects.ratio.Ratio;
import genericdwh.dataobjects.ratio.RatioCategory;
import genericdwh.dataobjects.referenceobject.ReferenceObject;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.main.Main;

import java.util.TreeMap;
import java.util.Map.Entry;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public 	class LazyLoadOnExpandListener implements ChangeListener<Boolean> {

	public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
    	if (newValue) {
			DataObjectTreeItem tiObj = (DataObjectTreeItem)((BooleanProperty)observable).getBean();
			
			if (tiObj.isLoaded() || tiObj.getValue() instanceof SidebarHeader
					|| tiObj.getValue() instanceof DimensionCategory || tiObj.getValue() instanceof RatioCategory
					|| tiObj.getValue() instanceof Ratio) {
				return;
			}

    		ReferenceObjectManager refObjManager = Main.getContext().getBean(ReferenceObjectManager.class);
    		
    		TreeMap<Long, ReferenceObject> refObjs;
    		if (tiObj.getValue() instanceof ReferenceObject) {
    			refObjs = refObjManager.loadRefObjsForDimAndRefObjParent((Dimension)tiObj.getFirstChild().getValue(), (ReferenceObject)tiObj.getValue());
    		} else {
    			refObjs = refObjManager.loadRefObjsForDim((Dimension)tiObj.getFirstChild().getValue());
    		}
    		
    		DataObjectTreeItem nextLvl = tiObj.getFirstChild().getFirstChild();
    		
    		tiObj.getChildren().clear();
    		
    		for (Entry<Long, ReferenceObject> currEntry : refObjs.entrySet()) {
    			DataObjectTreeItem tiRefObjNode = new DataObjectTreeItem(currEntry.getValue());
    			if (nextLvl != null && refObjManager.dimensionAndRefObjParentHaveRecords((Dimension)nextLvl.getValue(), currEntry.getValue())) {
	    			tiRefObjNode.addChild(nextLvl);
    			}
    			tiObj.addChild(tiRefObjNode);
    		}
    		
    		tiObj.setLoaded(true);
    	}
	}
}
