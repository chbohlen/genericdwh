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
	
	public boolean connect(String ip, String port, String dbName, String user, String password) {
		String dbDriver = com.mysql.jdbc.Driver.class.getName();
			try {
				Class.forName(dbDriver);
				dbConnection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+dbName, user, password);
				logger.info("Database connection established.");
			} catch (ClassNotFoundException e) {
				logger.error(dbDriver+" not found!");
				return false;
			} catch (SQLException e) {
				logger.error("Could not establish database connection!");
				return false;
			}
			
			return true;
	}
	
	public void disconnect() {
		try {
			if (dbConnection != null) {
				dbConnection.close();
				logger.info("Database connection closed.");
			}
			else {
				logger.info("No open database connection.");
			}
		} catch (SQLException e) {
			logger.error("Could not close database connection!");
		}
	}

	public void createDimension() {
		// TODO Auto-generated method stub
	}

	public void createDimensionHierarchy() {
		// TODO Auto-generated method stub		
	}

	public void createDimensionCombination() {
		// TODO Auto-generated method stub
	}
}
