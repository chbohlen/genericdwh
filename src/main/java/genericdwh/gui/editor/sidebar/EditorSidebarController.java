package genericdwh.gui.editor.sidebar;

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
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import genericdwh.dataobjects.DataObject;
import genericdwh.gui.editor.EditorController;
import genericdwh.gui.general.Icons;
import genericdwh.gui.general.sidebar.DataObjectTreeCell;
import genericdwh.gui.general.sidebar.HeaderItem;
import genericdwh.gui.general.sidebar.TreeCellRightClickHandler;
import genericdwh.main.Main;

public class EditorSidebarController implements Initializable {
	
	@FXML private TreeView<DataObject> sidebar;
	
	public void createSidebar() {
		HeaderItem tiRoot = new HeaderItem("", -1, true, false);
		sidebar.setRoot(tiRoot);
		sidebar.setShowRoot(false);
		
		
		HeaderItem tiDims = new HeaderItem("Dimensions", 0, true, true, new ImageView(Icons.UNGROUPED));
		tiDims.setExpanded(false);
		tiRoot.addChild(tiDims);
		
		HeaderItem tiDimsByCat = new HeaderItem("By Category", 1, true, true, new ImageView(Icons.GROUPED));
		tiDims.addChild(tiDimsByCat);
		
		
		HeaderItem tiDimHierarchies = new HeaderItem("Dimension Hierarchies", 2, true, true, new ImageView(Icons.UNGROUPED));
		tiDimHierarchies.setExpanded(false);
		tiRoot.addChild(tiDimHierarchies);
		
		HeaderItem tiDimHierarchiesByCat = new HeaderItem("By Category", 3, true, true, new ImageView(Icons.GROUPED));
		tiDimHierarchies.addChild(tiDimHierarchiesByCat);
		
		
		HeaderItem tiDimCombinations = new HeaderItem("Dimension Combinations", 4, true, true, new ImageView(Icons.UNGROUPED));
		tiDimCombinations.setExpanded(false);
		tiRoot.addChild(tiDimCombinations);
		
		
		HeaderItem tiRefObjs = new HeaderItem("Reference Objects", 5, true, true, new ImageView(Icons.UNGROUPED));
		tiRefObjs.setExpanded(false);
		tiRoot.addChild(tiRefObjs);
		
		HeaderItem tiRefObjsByDim = new HeaderItem("By Dimension", 6, true, true, new ImageView(Icons.GROUPED));
		tiRefObjs.addChild(tiRefObjsByDim);
		
		
		HeaderItem tiRefObjHierarchies = new HeaderItem("Reference Object Hierarchies", 7, true, true, new ImageView(Icons.UNGROUPED));
		tiRefObjHierarchies.setExpanded(false);
		tiRoot.addChild(tiRefObjHierarchies);
		
		HeaderItem tiRefObjHierarchiesByCat = new HeaderItem("By Category", 8, true, true, new ImageView(Icons.GROUPED));
		tiRefObjHierarchies.addChild(tiRefObjHierarchiesByCat);
		
		
		HeaderItem tiRefObjCombinations = new HeaderItem("Reference Object Combinations", 9, true, true, new ImageView(Icons.UNGROUPED));
		tiRefObjCombinations.setExpanded(false);
		tiRoot.addChild(tiRefObjCombinations);
		
		HeaderItem tiRefObjCombinationsByDim = new HeaderItem("By Dimension", 10, true, true, new ImageView(Icons.GROUPED));
		tiRefObjCombinations.addChild(tiRefObjCombinationsByDim);
		
		
		HeaderItem tiRatios = new HeaderItem("Ratios", 11, true, true, new ImageView(Icons.UNGROUPED));
		tiRatios.setExpanded(false);
		tiRoot.addChild(tiRatios);
		
		HeaderItem tiRatiosByCat = new HeaderItem("By Category", 12, true, true, new ImageView(Icons.GROUPED));
		tiRatios.addChild(tiRatiosByCat);
		
		
		HeaderItem tiRatioRelations = new HeaderItem("Ratio Relations", 13, true, true, new ImageView(Icons.UNGROUPED));
		tiRatioRelations.setExpanded(false);
		tiRoot.addChild(tiRatioRelations);
		
		HeaderItem tiRatioRelationsByCat = new HeaderItem("By Category", 14, true, true, new ImageView(Icons.GROUPED));
		tiRatioRelations.addChild(tiRatioRelationsByCat);
		
		
		HeaderItem tiFacts = new HeaderItem("Facts", 15, true, true, new ImageView(Icons.UNGROUPED));
		tiFacts.setExpanded(false);
		tiRoot.addChild(tiFacts);
		
		HeaderItem tiFactsByRatio = new HeaderItem("By Ratio", 16, true, true, new ImageView(Icons.GROUPED));
		tiFacts.addChild(tiFactsByRatio);
		
		HeaderItem tiFactsByReferenceObject = new HeaderItem("By Reference Object", 17, true, true, new ImageView(Icons.GROUPED));
		tiFacts.addChild(tiFactsByReferenceObject);
		
		
		HeaderItem tiDimCats = new HeaderItem("Dimension Categories", 18, true, true, new ImageView(Icons.UNGROUPED));
		tiRoot.addChild(tiDimCats);
		
		HeaderItem tiRefObjCats = new HeaderItem("Ratio Categories", 19, true, true, new ImageView(Icons.UNGROUPED));
		tiRoot.addChild(tiRefObjCats);
		
		HeaderItem tiUnits = new HeaderItem ("Units", 20, true, true, new ImageView(Icons.UNGROUPED));
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
				Main.getContext().getBean(EditorController.class).loadEditingView(
						(int)treeCell.getTreeView().getSelectionModel().getSelectedItem().getValue().getId());
            }
        });
		
		contextMenu.getItems().add(load);
		return contextMenu;
	}
}
