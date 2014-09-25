package genericdwh.gui;

import genericdwh.dataobjects.DataObjectManagerConfig;
import genericdwh.gui.connectwindow.ConnectWindowController;
import genericdwh.gui.editor.EditorController;
import genericdwh.gui.editor.dialogpopups.DeleteDialogPopupController;
import genericdwh.gui.editor.dialogpopups.DiscardDialogPopupController;
import genericdwh.gui.editor.dialogpopups.SaveOrDiscardDialogPopupController;
import genericdwh.gui.editor.dialogpopups.SaveDialogPopupController;
import genericdwh.gui.editor.dialogpopups.ValidationFailureDialogPopupController;
import genericdwh.gui.editor.editingview.EditingViewController;
import genericdwh.gui.editor.editingview.searchbox.SearchBoxController;
import genericdwh.gui.editor.sidebar.EditorSidebarController;
import genericdwh.gui.general.statusbar.StatusBarController;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.querypane.dialogpopups.ExecutionFailurePopupDialogController;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGridController;
import genericdwh.gui.mainwindow.sidebar.MainWindowSidebarController;

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
		return new QueryPaneController(resultGridController(), executionFailurePopupDialogController());
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
				saveDialogPopupController(),
				validationFailureDialogPopupController(),
				discardDialogPopupController(),
				saveOrDiscardDialogPopupController(),
				deleteDialogPopupController());
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
	
	/* DialogPopup-Controller */
	
	/* QueryPane */
	@Bean
	public ExecutionFailurePopupDialogController executionFailurePopupDialogController() {
		return new ExecutionFailurePopupDialogController();
	}
	
	/* Editor */
	@Bean
	public SaveDialogPopupController saveDialogPopupController() {
		return new SaveDialogPopupController();
	}
	
	@Bean
	public ValidationFailureDialogPopupController validationFailureDialogPopupController() {
		return new ValidationFailureDialogPopupController();
	}
	
	@Bean
	public DiscardDialogPopupController discardDialogPopupController() {
		return new DiscardDialogPopupController();
	}
	
	@Bean
	public SaveOrDiscardDialogPopupController saveOrDiscardDialogPopupController() {
		return new SaveOrDiscardDialogPopupController();
	}
	
	@Bean
	public DeleteDialogPopupController deleteDialogPopupController() {
		return new DeleteDialogPopupController();
	}
}
