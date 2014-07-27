package genericdwh.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MySQLDatabaseController implements DatabaseController {
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	private Connection dbConnection;
	
	public MySQLDatabaseController() {
		
	}
	
	public void connect(String ip, String port, String dbName, String user, String password) {
			try {
				Class.forName(com.mysql.jdbc.Driver.class.getName());
				dbConnection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+dbName, user, password);
				logger.info("Database connection established.");
				
			} catch (ClassNotFoundException e) {
				logger.error("com.mysql.jdbc.Driver not found!");
			} catch (SQLException e) {
				logger.error("Could not establish database connection!");
			}
	}
	
	public void disconnect() {
		try {
			dbConnection.close();
			logger.info("Database connection closed.");
		} catch (SQLException e) {
			logger.error("Could not close database connection!");
		}
	}
}
