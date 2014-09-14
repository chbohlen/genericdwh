package genericdwh.db;

import javafx.beans.property.BooleanProperty;

public interface DatabaseController {
	public boolean connect(String ip, String port, String dbName, String userName, String password);
	public void disconnect();
	
	public BooleanProperty getIsConnected();
	
	public DatabaseReader getDbReader();
	public DatabaseWriter getDbWriter();
}
