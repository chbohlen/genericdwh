package genericdwh.gui;

import java.net.URL;

import genericdwh.main.Main;
import javafx.fxml.FXMLLoader;
import javafx.util.Callback;

public class SpringFXMLLoader extends FXMLLoader {
	
	public SpringFXMLLoader(URL location, Object controller) {
		super(location);
		
		setControllerFactory(new Callback<Class<?>, Object>() {
		    public Object call(Class<?> clazz) {
		    	return Main.getContext().getBean(clazz);
		    }
		});
		
		setController(controller);
	}
}
