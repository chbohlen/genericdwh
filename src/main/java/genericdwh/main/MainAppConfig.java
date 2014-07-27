package genericdwh.main;

import genericdwh.db.DatabaseControllerConfig;
import genericdwh.gui.MainWindowConfig;

import org.springframework.context.annotation.*;

@Configuration
@Import({MainWindowConfig.class, DatabaseControllerConfig.class})
public class MainAppConfig {
	
}
