package genericdwh.main;

import genericdwh.gui.GUIConfig;
import genericdwh.configfiles.ConfigFileReaderConfig;
import genericdwh.db.DatabaseControllerConfig;
import genericdwh.dataobjects.DataObjectManagerConfig;
import genericdwh.gui.mainwindow.MainWindowController;
import javafx.application.Application;
import javafx.stage.Stage;
import lombok.Getter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {
	
	@Getter private final static Logger logger = LogManager.getLogger(Main.class);

	@Getter private static AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		
	public static void main(String[] args) {
		logger.info("Application started.");
		
		context.register(ConfigFileReaderConfig.class);
		context.register(GUIConfig.class);
		context.register(DatabaseControllerConfig.class);
		context.register(DataObjectManagerConfig.class);
		context.refresh();
		
		launch(args);
				
		context.close();
		
		logger.info("Application exited.");
	}
	
	@Override
	public void start(Stage stage) {
		context.getBean(MainWindowController.class).createWindow(stage);
	}
}