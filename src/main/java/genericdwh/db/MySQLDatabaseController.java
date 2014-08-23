package genericdwh.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySQLDatabaseController implements DatabaseController {
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	
	private Connection dbConnection;

	private MySQLDatabaseReader dbReader;
	private MySQLDatabaseWriter dbWriter;
		
	public MySQLDatabaseController(MySQLDatabaseReader dbReader, MySQLDatabaseWriter dbWriter) {
		this.dbReader = dbReader;
		this.dbWriter = dbWriter;
	}
	
	public boolean connect(String ip, String port, String dbName, String userName, String password) {
		String dbDriver = com.mysql.jdbc.Driver.class.getName();
		
		try {
			Class.forName(dbDriver);
			dbConnection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+dbName, userName, password);
			logger.info("Database connection established.");
		} catch (ClassNotFoundException e) {
			logger.error(dbDriver+" not found!");
			return false;
		} catch (SQLException e) {
			logger.error("Could not establish database connection!");
			return false;
		}
			
		DSLContext context = DSL.using(dbConnection, SQLDialect.MYSQL);
		dbReader.setDSLContext(context);
		dbWriter.setDSLContext(context);
		
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
	
	public MySQLDatabaseReader getReader() {
		return dbReader;
	}
	
	public MySQLDatabaseWriter getWriter() {
		return dbWriter;
	}
}