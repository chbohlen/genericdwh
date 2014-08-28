package genericdwh.gui;

import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.mainwindow.querypane.QueryPaneController;
import genericdwh.gui.mainwindow.querypane.resultgrid.ResultGrid;
import genericdwh.gui.mainwindow.sidebar.SidebarController;
import genericdwh.gui.subwindows.ConnectWindowController;

import org.springframework.context.annotation.*;

@Configuration
public class GUIConfig {

	@Bean
	public MainWindowController mainWindowController() {
		return new MainWindowController(connectWindowController(), sidebarController(), queryPaneController());
	}
	
	@Bean
	public ConnectWindowController connectWindowController() {
		return new ConnectWindowController();
	}
	
	@Bean
	public SidebarController sidebarController() {
		return new SidebarController();
	}
	
	@Bean
	public QueryPaneController queryPaneController() {
		return new QueryPaneController(resultGrid());
	}
	
	@Bean
	public ResultGrid resultGrid() {
		return new ResultGrid();
	}
}
