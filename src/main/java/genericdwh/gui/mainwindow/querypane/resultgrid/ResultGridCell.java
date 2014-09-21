package genericdwh.gui.mainwindow.querypane.resultgrid;

import genericdwh.dataobjects.dimension.Dimension;
import genericdwh.dataobjects.dimension.DimensionHierarchy;
import genericdwh.main.Main;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.Setter;

public class ResultGridCell extends BorderPane {
	
	@Getter private double value;
	
	@Getter private int colIndex;
	@Getter private int rowIndex;
	
	@Getter @Setter private int colSpan = 1;
	@Getter @Setter private int rowSpan = 1;
	
	@Getter private boolean hasChanged = false;
	
	private Label label;
	private Button button;
	
	private DimensionHierarchy hierarchy;
	private Dimension currLevel;

	@Getter private boolean collapsed;
		
	public ResultGridCell(String text, int colIndex, int rowIndex, int cellType) {
		this.colIndex = colIndex;
		this.rowIndex = rowIndex;
		
		getStylesheets().add("/css/ResultGridCellStyles.css");
		getStyleClass().add("border");
		getStyleClass().add("cell-" + cellType);
		
		if (button != null) {
			button.getStyleClass().add("button");
		}
		
		setPadding(new Insets(1));
		
		label = new Label();
		setCenter(label);
		
		setText(text);
	}
		
	public ResultGridCell(String text, int colIndex, int rowIndex, int cellType, int colSpan, int rowSpan) {
		this(text, colIndex, rowIndex, cellType);
		
		this.colSpan = colSpan;
		this.rowSpan = rowSpan;
	}
	
	public ResultGridCell(String text, int colIndex, int rowIndex, int cellType, int colSpan, int rowSpan, DimensionHierarchy hierarchy, Dimension currLevel, boolean collapsed) {
		this(text, colIndex, rowIndex, cellType, colSpan, rowSpan);
		
		this.hierarchy = hierarchy;
		this.currLevel = currLevel;
		
		button = new Button();
		
		if (collapsed) {
			button.setText("+");
			button.setOnAction(new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent e) {
			    	expand();
			    }
			});
		} else {
			button.setText("-");
			button.setOnAction(new EventHandler<ActionEvent>() {
			    @Override
			    public void handle(ActionEvent e) {
			    	collapse();
			    }
			});
		}
		
		this.collapsed = collapsed;
		
		ResultGridCell thisCell = this;
		ContextMenu contextMenu = new ResultGridCellContextMenu(thisCell);
		setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {  
			  @Override  
			  public void handle(ContextMenuEvent event) {  
				  contextMenu.show(thisCell, event.getScreenX(), event.getScreenY());
			  }  
		}); 
		
		setRight(button);
		ResultGridCell.setMargin(button, new Insets(0, 0, 0, 3));
	}

	public void setValue(Double value, String symbol) {
		this.value = value;
		
		if (value == null) {
			value = (double)0;
			setText("no data");
		} else {
			setText(value.toString() + symbol);
		}		
	}		
	
	public void setText(String text) {
		label.setText(text);
	}
	
	public void expand() {
    	Main.getContext().getBean(ResultGridController.class)
			.expandCollapseGridHandler(((ResultGrid)getParent()).getGridId(), hierarchy, currLevel, true);
	}
	
	public void collapse() {
    	Main.getContext().getBean(ResultGridController.class)
			.expandCollapseGridHandler(((ResultGrid)getParent()).getGridId(), hierarchy, currLevel, false);
	}
}
