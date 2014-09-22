package genericdwh.gui;

import genericdwh.dataobjects.DataObjectManagerConfig;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGridController;
import genericdwh.gui.mainwindow.sidebar.MainWindowSidebarController;
import genericdwh.gui.statusbar.StatusBarController;
import genericdwh.gui.subwindows.connectwindow.ConnectWindowController;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.editingview.searchbox.SearchBoxController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.DeletePopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.DiscardPopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.SavePopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.SaveOrDiscardPopupDialogController;
import genericdwh.gui.subwindows.editor.subwindows.dialogpopup.ValidationFailurePopupDialogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
@Import(DataObjectManagerConfig.class)
public class GUIConfig {
	
	@Autowired
	private DataObjectManagerConfig objManagerConfig;

	@Bean
	public MainWindowController mainWindowController() {
		return new MainWindowController(mainStatusBarController(),
				mainWindowSidebarController(), queryPaneController(),
				connectWindowController(), editorWindowController());
	}
	
	@Bean
	public StatusBarController mainStatusBarController() {
		return new StatusBarController();
	}
	
	@Bean
	public MainWindowSidebarController mainWindowSidebarController() {
		return new MainWindowSidebarController();
	}
	
	@Bean
	public QueryPaneController queryPaneController() {
		return new QueryPaneController(resultGridController());
	}
	
	@Bean
	public ResultGridController resultGridController() {
		return new ResultGridController();
	}
	
	@Bean
	public ConnectWindowController connectWindowController() {
		return new ConnectWindowController();
	}
	
	@Bean
	public EditorController editorWindowController() {
		return new EditorController(objManagerConfig.changeManager(),
				editorStatusBarController(),
				editorSidebarController(), editingViewController(),
				savePopupDialogController(),
				validationFailurePopupDialogController(),
				discardChangesPopupDialogController(),
				saveOrDiscardOnLoadPopupDialogController(),
				deleteObjectDialogPopupController());
	}
	
	@Bean
	public StatusBarController editorStatusBarController() {
		return new StatusBarController();
	}
	
	@Bean
	public EditorSidebarController editorSidebarController() {
		return new EditorSidebarController();
	}
	
	@Bean
	public EditingViewController editingViewController() {
		return new EditingViewController(searchBoxController());
	}
	
	@Bean
	public SearchBoxController searchBoxController() {
		return new SearchBoxController();
	}
	
	
	/* PopupDialogController */
	
	@Bean
	public SavePopupDialogController savePopupDialogController() {
		return new SavePopupDialogController();
	}
	
	@Bean
	public ValidationFailurePopupDialogController validationFailurePopupDialogController() {
		return new ValidationFailurePopupDialogController();
	}
	
	@Bean
	public DiscardPopupDialogController discardChangesPopupDialogController() {
		return new DiscardPopupDialogController();
	}
	
	@Bean
	public SaveOrDiscardPopupDialogController saveOrDiscardOnLoadPopupDialogController() {
		return new SaveOrDiscardPopupDialogController();
	}
	
	@Bean
	public DeletePopupDialogController deleteObjectDialogPopupController() {
		return new DeletePopupDialogController();
	}
}
