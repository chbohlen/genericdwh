package genericdwh.main;

import genericdwh.gui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainApp extends Application {
	
	private static AnnotationConfigApplicationContext ctx = null;
	
	public static void main(String[] args) {
		ctx = new AnnotationConfigApplicationContext(MainAppConfig.class);
		
		launch(args);
		
		ctx.close();
	}
	
	@Override
	public void start(Stage stage) {
		ctx.getBean(MainWindow.class).createWindow(stage);
	}
	
	public static AnnotationConfigApplicationContext getContext() {
		return ctx;
	}
}