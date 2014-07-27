package genericdwh.gui;

import org.springframework.context.annotation.*;

@Configuration
public class MainWindowConfig {
	
	@Bean
	public MainWindow mainWindow() {
		return new MainWindow();
	}
}
