package genericdwh.db;

public interface DatabaseController {
	
	public void connect(String ip, String port, String dbName, String user, String password);
	public void disconnect();
}
