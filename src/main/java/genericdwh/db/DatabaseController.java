package genericdwh.db;

public interface DatabaseController {
	
	public boolean connect(String ip, String port, String dbName, String user, String password);
	public void disconnect();
	
	public void createDimension();
	public void createDimensionHierarchy();
	public void createDimensionCombination();
}
