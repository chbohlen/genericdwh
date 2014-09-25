package genericdwh.configfiles;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigFileReader {
		
	private final String configFilePath;
	
	public ConfigFileReader(String configFilePath) {
		this.configFilePath = configFilePath;
	}
	
	public Properties load(String fileName) {
		Properties props = new Properties();
		InputStream is = null;
	 
		try {
			is = new FileInputStream(configFilePath + fileName);
			props.load(is);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return props;
	}
	
	public void store(Properties props, String fileName) {
		store(props, fileName, null);
	}
	
	public void store(Properties props, String fileName, String comment) {
		OutputStream os = null;
	 
		try {
			os = new FileOutputStream(configFilePath + fileName);
			props.store(os, comment);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
