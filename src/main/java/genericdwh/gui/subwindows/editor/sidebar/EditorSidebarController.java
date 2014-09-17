package genericdwh.gui.subwindows.editor.sidebar;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import genericdwh.dataobjects.DataObject;
import genericdwh.gui.general.sidebar.DataObjectTreeCell;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.gui.general.sidebar.TreeCellRightClickHandler;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;

public class EditorSidebarController implements Initializable {
	
	@FXML private TreeView<DataObject> sidebar;
	
	public void createSidebar() {
		HeaderItem tiRoot = new HeaderItem("", -1, true, false);
		sidebar.setRoot(tiRoot);
		sidebar.setShowRoot(false);
		
		
		HeaderItem tiDims = new HeaderItem("Dimensions", 0, true, true);
		tiDims.setExpanded(true);
		tiRoot.addChild(tiDims);
		
		HeaderItem tiDimsByCat = new HeaderItem("By Category", 1, true, true);
		tiDims.addChild(tiDimsByCat);
		
		
		HeaderItem tiRefObjs = new HeaderItem("Reference Objects", 2, true, true);
		tiRefObjs.setExpanded(true);		
		tiRoot.addChild(tiRefObjs);
		
		HeaderItem tiRefObjsByDim = new HeaderItem("By Dimension", 3, true, true);
		tiRefObjs.addChild(tiRefObjsByDim);
		
		
		HeaderItem tiRatios = new HeaderItem("Ratios", 4, true, true);
		tiRatios.setExpanded(true);		
		tiRoot.addChild(tiRatios);
		
		HeaderItem tiRatiosByCat = new HeaderItem("By Category", 5, true, true);
		tiRatios.addChild(tiRatiosByCat);
		
		
		HeaderItem tiFacts = new HeaderItem("Facts", 6, true, true);
		tiFacts.setExpanded(true);		
		tiRoot.addChild(tiFacts);
		
		HeaderItem tiFactsByRatio = new HeaderItem("By Ratio", 7, true, true);
		tiFacts.addChild(tiFactsByRatio);
		
		HeaderItem tiFactsByReferenceObject = new HeaderItem("By Reference Object", 8, true, true);
		tiFacts.addChild(tiFactsByReferenceObject);
		
		
		HeaderItem tiDimCats = new HeaderItem("Dimension Categories", 9, true, true);
		tiRoot.addChild(tiDimCats);
		
		HeaderItem tiRefObjCats = new HeaderItem("Ratio Categories", 10, true, true);
		tiRoot.addChild(tiRefObjCats);
		
		HeaderItem tiUnits = new HeaderItem ("Units", 11, true, true);
		tiRoot.addChild(tiUnits);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sidebar.setCellFactory(new Callback<TreeView<DataObject>, TreeCell<DataObject>>() {
            public TreeCell<DataObject> call(TreeView<DataObject> param) {
            	DataObjectTreeCell treeCell = new DataObjectTreeCell();
            	
            	treeCell.setOnMouseClicked(new TreeCellRightClickHandler(sidebar));
            	treeCell.setContextMenu(createDimensionContextMenu(treeCell));
            	
                return treeCell;
            }
        });
	}
	
	private ContextMenu createDimensionContextMenu(DataObjectTreeCell treeCell) {				
		ContextMenu contextMenu = new ContextMenu();
				
		MenuItem load = new MenuItem("Load");
		load.setOnAction(new EventHandler<ActionEvent>() {
			@Override
            public void handle(ActionEvent event) {
				Main.getContext().getBean(EditorController.class).createEditorTreeTable((int)treeCell.getItem().getId());
            }
        });
		
		contextMenu.getItems().add(load);
		return contextMenu;
	}
}
