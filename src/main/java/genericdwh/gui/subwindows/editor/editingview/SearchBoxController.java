package genericdwh.gui.subwindows.editor.editingview;

import genericdwh.dataobjects.DataObject;
import genericdwh.main.Main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;

public class SearchBoxController implements Initializable {
	
	@FXML AnchorPane searchBox;
	@FXML private TextField tfSearchText;
	@FXML private Button btnSearch;
	@FXML private Button btnNext;
	@FXML private Button btnClear;
	
	private int prevIndex = 0;
	private int prevIndexChildren = 0;
	private String currSearchText = "";
	
	public SearchBoxController() {
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		tfSearchText.textProperty().addListener(new ChangeListener<String>() {
			@Override
			synchronized public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				resetSearchBox(false);
			}
		});
		tfSearchText.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			synchronized public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				if (newValue != oldValue) {
					btnSearch.setDefaultButton(newValue);
				}
			}
		});
	}
	
	@FXML public void buttonSearchOnClickHandler() {
		if (!tfSearchText.getText().isEmpty()) {
			currSearchText = tfSearchText.getText();
			search(currSearchText, 0, 0);
		}
	}

	@FXML public void buttonNextOnClickHandler() {
		search(currSearchText, prevIndex, prevIndexChildren);
	}

	@FXML public void buttonClearOnClickHandler() {
		resetSearchBox(true);
	}
	
	synchronized private void search(String searchText, int index, int indexChildren) {
		TreeTableView<DataObject> editingView = Main.getContext().getBean(EditingViewController.class).getEditingView();
		
		TreeItem<DataObject> searchedObj = null;
		for (; index < editingView.getRoot().getChildren().size(); index++) {
			TreeItem<DataObject> tiObj = editingView.getRoot().getChildren().get(index);
			if (!tiObj.getChildren().isEmpty()) {
				for (; indexChildren < tiObj.getChildren().size(); indexChildren++) {
					TreeItem<DataObject> tiObjChild = tiObj.getChildren().get(indexChildren);
					if (tiObjChild.getValue().getName().toLowerCase().contains(searchText.toLowerCase())) {
						searchedObj = tiObjChild;
						break;
					}
				}
			} else {
				if (tiObj.getValue().getName().toLowerCase().contains(searchText.toLowerCase())) {
					searchedObj = tiObj;
					break;
				}
			}
		}
		
		if (searchedObj == null) {
			if (prevIndex != 0 && prevIndexChildren != 0) {
				prevIndex = 0;
				prevIndexChildren = 0;
				search(currSearchText, 0, 0);
			}
			return;
		}
		
		int row = editingView.getRow(searchedObj);
		editingView.getSelectionModel().select(row);
		editingView.getFocusModel().focus(row);
		editingView.scrollTo(row);
		
		btnNext.setDisable(false);
		btnClear.setDisable(false);
		
		btnSearch.setDefaultButton(false);
		btnNext.setDefaultButton(true);
		
		prevIndex = ++index;
		prevIndexChildren = ++indexChildren;
	}
	
	
	synchronized public void resetSearchBox(boolean fullReset) {
		btnNext.setDisable(true);
		btnClear.setDisable(true);
		
		if (fullReset) {
			btnSearch.setDefaultButton(false);
		}
		btnNext.setDefaultButton(false);
		
		prevIndex = 0;
		prevIndexChildren = 0;
		
		currSearchText = "";
		if (fullReset) {
			tfSearchText.setText("");
		}
	}

	public void showSearchBox() {
		searchBox.setVisible(true);
	}	

	public void hideSearchBox() {
		searchBox.setVisible(false);
	}
}
