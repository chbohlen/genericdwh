package genericdwh.main;

import genericdwh.gui.MainWindowController;
import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main extends Application {
	
	private static AnnotationConfigApplicationContext context = null;
	
	public static void main(String[] args) {
		context = new AnnotationConfigApplicationContext(MainConfig.class);
		
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