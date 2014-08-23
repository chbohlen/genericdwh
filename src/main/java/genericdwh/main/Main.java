package genericdwh.main;

import genericdwh.gui.GUIConfig;
import genericdwh.configfiles.ConfigFileReaderConfig;
import genericdwh.db.DatabaseControllerConfig;
import genericdwh.dataobjects.DataObjectManagerConfig;
import genericdwh.gui.mainwindow.MainWindowController;
import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {
	
	private static AnnotationConfigApplicationContext context = null;
	
	public static void main(String[] args) {
		context = new AnnotationConfigApplicationContext();
		context.register(ConfigFileReaderConfig.class);
		context.register(GUIConfig.class);
		context.register(DatabaseControllerConfig.class);
		context.register(DataObjectManagerConfig.class);
		context.refresh();

		launch(args);
		
		context.close();
	}
	
	@Override
	public void start(Stage stage) {
		context.getBean(MainWindowController.class).createWindow(stage);
	}
	
	public static AnnotationConfigApplicationContext getContext() {
		return context;
	}
}