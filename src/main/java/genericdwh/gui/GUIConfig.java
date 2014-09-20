package genericdwh.gui;

import genericdwh.dataobjects.DataObjectManagerConfig;
import genericdwh.gui.general.StatusBarController;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGridController;
import genericdwh.gui.mainwindow.sidebar.MainWindowSidebarController;
import genericdwh.gui.subwindows.connectdialog.ConnectDialogController;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.editingview.SearchBoxController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.DeleteDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.DiscardDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.SaveDialogController;
import genericdwh.gui.subwindows.editor.subwindows.confirmationdialog.SaveOrDiscardOnLoadDialogController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

@Configuration
@Import(DataObjectManagerConfig.class)
public class GUIConfig {
	
	@Autowired
	private DataObjectManagerConfig objManagerConfig;

	@Bean
	public MainWindowController mainWindowController() {
		return new MainWindowController(statusBarController(),
				mainWindowSidebarController(), queryPaneController(),
				connectWindowController(), editorWindowController());
	}
	
	@Bean
	public StatusBarController statusBarController() {
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
	public ConnectDialogController connectWindowController() {
		return new ConnectDialogController();
	}
	
	@Bean
	public EditorController editorWindowController() {
		return new EditorController(objManagerConfig.changeManager(),
				statusBarController(),
				editorSidebarController(), editingViewController(),
				saveChangesDialogController(), discardChangesDialogController(),
				saveOrDiscardOnLoadDialogController(),
				deleteObjectDialogController());
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
	
	@Bean
	public SaveDialogController saveChangesDialogController() {
		return new SaveDialogController();
	}
	
	@Bean
	public DiscardDialogController discardChangesDialogController() {
		return new DiscardDialogController();
	}
	
	@Bean
	public SaveOrDiscardOnLoadDialogController saveOrDiscardOnLoadDialogController() {
		return new SaveOrDiscardOnLoadDialogController();
	}
	
	@Bean
	public DeleteDialogController deleteObjectDialogController() {
		return new DeleteDialogController();
	}
}
