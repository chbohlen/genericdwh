package genericdwh.main;

import genericdwh.db.DatabaseControllerConfig;
import genericdwh.gui.GUIConfig;

import org.springframework.context.annotation.*;

@Configuration
@Import({GUIConfig.class, DatabaseControllerConfig.class})
public class MainConfig {
	
}
