package genericdwh.db;

public interface DatabaseController {
	public boolean connect(String ip, String port, String dbName, String userName, String password);
	public void disconnect();
	
	public DatabaseReader getReader();
	public DatabaseWriter getWriter();
}
