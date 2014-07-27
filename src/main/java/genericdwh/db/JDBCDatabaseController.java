package genericdwh.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class JDBCDatabaseController implements DatabaseController {
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	
	private Connection dbConnection;
	
	private final String dbDriver;
	private final String dbms;
	
	public JDBCDatabaseController(String dbDriver, String dbms) {
		this.dbDriver = dbDriver;
		this.dbms = dbms;
	}
	
	public void connect(String ip, String port, String dbName, String user, String password) {
			try {
				Class.forName(dbDriver);
				dbConnection = DriverManager.getConnection("jdbc:"+dbms+"://"+ip+":"+port+"/"+dbName, user, password);
				logger.info("Database connection established.");
				
			} catch (ClassNotFoundException e) {
				logger.error(dbDriver+" not found!");
			} catch (SQLException e) {
				logger.error("Could not establish database connection!");
			}
	}
	
	public void disconnect() {
		try {
			if (dbConnection != null) {
				dbConnection.close();
				logger.info("Database connection closed.");
			}
		} catch (SQLException e) {
			logger.error("Could not close database connection!");
		}
	}
}
