package genericdwh.gui;

import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGridController;
import genericdwh.gui.mainwindow.sidebar.MainWindowSidebarController;
import genericdwh.gui.subwindows.connectdialog.ConnectDialogController;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.gui.subwindows.editor.editingview.EditingViewController;
import genericdwh.gui.subwindows.editor.sidebar.EditorSidebarController;

import org.springframework.context.annotation.*;

@Configuration
public class GUIConfig {

	@Bean
	public MainWindowController mainWindowController() {
		return new MainWindowController(mainWindowSidebarController(), queryPaneController(), connectWindowController(), editorWindowController());
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
		return new EditorController(editorSidebarController(), resultViewController());
	}
	
	@Bean
	public EditorSidebarController editorSidebarController() {
		return new EditorSidebarController();
	}
	
	@Bean
	public EditingViewController resultViewController() {
		return new EditingViewController();
	}
}
