package genericdwh.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySQLDatabaseController implements DatabaseController {
	
	private final Logger logger = LogManager.getLogger(this.getClass());
	
	private Connection dbConnection;
	
	@Getter private BooleanProperty isConnected = new SimpleBooleanProperty(false);

	@Getter private MySQLDatabaseReader dbReader;
	@Getter private MySQLDatabaseWriter dbWriter;
			
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
		dbReader.setDslContext(context);
		dbWriter.setDslContext(context);
		
		isConnected.set(true);
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
		
		isConnected.set(false);
	}
}
