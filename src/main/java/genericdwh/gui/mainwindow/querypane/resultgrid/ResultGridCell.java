package genericdwh.gui.mainwindow.querypane.resultgrid;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
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
		
	public ResultGridCell(int colIndex, int rowIndex) {
		this.colIndex = colIndex;
		this.rowIndex = rowIndex;
		
		getStylesheets().add("/css/ResultGridVisibleBorders.css");
		getStyleClass().add("border");
		
		setPadding(new Insets(1));
		
		label = new Label();
		setCenter(label);
		
		setText("no data");
	}
	
	public ResultGridCell(String text, int colIndex, int rowIndex) {
		this(colIndex, rowIndex);
			
		setText(text);
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
}
