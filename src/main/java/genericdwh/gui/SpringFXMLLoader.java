package genericdwh.gui;

import java.net.URL;

import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.gui.statusbar.StatusBarController;
import genericdwh.gui.subwindows.editor.EditorController;
import genericdwh.main.Main;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

public class SpringFXMLLoader extends FXMLLoader {
	
	public SpringFXMLLoader(URL location, final Object controller) {
		super(location);
		
		setControllerFactory(new Callback<Class<?>, Object>() {
		    public Object call(Class<?> clazz) {
		    	if (clazz == StatusBarController.class) {
		    		if (controller instanceof MainWindowController) {
		    			return Main.getContext().getBean("mainStatusBarController");
		    		} else if (controller instanceof EditorController) {
		    			return Main.getContext().getBean("editorStatusBarController");
		    		}
		    	}
		    	return Main.getContext().getBean(clazz);
		    }
		});
		
		setController(controller);
	}
}
