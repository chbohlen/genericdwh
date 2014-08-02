package genericdwh.gui;

import java.io.IOException;
import java.net.URL;

import genericdwh.main.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Callback;

public class SpringFXMLLoader {
	
	public static Parent load(URL location) {
		try {
			return FXMLLoader.load(location, null, null, new Callback<Class<?>, Object>() {
			    public Object call(Class<?> clazz) {
			    	return Main.getContext().getBean(clazz);
			    }
			});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
