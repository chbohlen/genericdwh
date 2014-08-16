package genericdwh.gui;

import org.springframework.context.annotation.*;

@Configuration
public class GUIConfig {

	@Bean
	public MainWindowController mainWindowController() {
		return new MainWindowController(connectWindowController(), sidebarController());
	}
	
	@Bean
	public ConnectWindowController connectWindowController() {
		return new ConnectWindowController();
	}
	
	@Bean
	public SidebarController sidebarController() {
		return new SidebarController();
	}
}
