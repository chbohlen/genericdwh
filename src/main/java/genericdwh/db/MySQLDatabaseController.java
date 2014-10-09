package genericdwh.db;

import genericdwh.gui.general.Icons;
import genericdwh.gui.general.StatusMessages;
import genericdwh.gui.mainwindow.MainWindowController;
import genericdwh.main.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.Getter;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class MySQLDatabaseController implements DatabaseController {
		
	private Connection dbConnection;
	
	@Getter private BooleanProperty isConnected = new SimpleBooleanProperty(false);

	@Getter private MySQLDatabaseReader dbReader;
	@Getter private MySQLDatabaseWriter dbWriter;
	
	private String ip;
	private String port;
	private String dbName;
			
	public MySQLDatabaseController(MySQLDatabaseReader dbReader, MySQLDatabaseWriter dbWriter) {
		this.dbReader = dbReader;
		this.dbWriter = dbWriter;
	}
	
	@Override
	public boolean connect(String ip, String port, String dbName, String userName, String password) {
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		
		this.ip = ip;
		this.port = port;
		this.dbName = dbName;
		
		String dbDriver = com.mysql.jdbc.Driver.class.getName();
		try {
			Class.forName(dbDriver);
			dbConnection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+dbName, userName, password);
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();
			if (errorCode == 1045) {
				mainWindowController.postStatus(StatusMessages.CONNECTION_INVALID_USERNAME_PW, Icons.WARNING);
				Main.getLogger().error("Database connection failed (" + ip + ":" + port + "/" + dbName + ") :" + StatusMessages.CONNECTION_INVALID_USERNAME_PW);
				return false;
			}
			if (errorCode == 1049) {
				mainWindowController.postStatus(StatusMessages.CONNECTION_INVALID_DATABASE_SCHEMA, Icons.WARNING);
				Main.getLogger().error("Database connection failed (" + ip + ":" + port + "/" + dbName + ") :" + StatusMessages.CONNECTION_INVALID_DATABASE_SCHEMA);
				return false;
			}
			mainWindowController.postStatus(StatusMessages.CONNECTION_FAILED, Icons.WARNING);
			Main.getLogger().error("Database connection failed (" + ip + ":" + port + "/" + dbName + ") :" + StatusMessages.CONNECTION_FAILED);
			return false;
		} catch (Exception e) {
				e.printStackTrace();
			return false;
		}
			
		DSLContext context = DSL.using(dbConnection, SQLDialect.MYSQL);
		dbReader.setDslContext(context);
		dbWriter.setDslContext(context);
		
		isConnected.set(true);
		
		mainWindowController.postStatus(StatusMessages.CONNECTION_OK, Icons.NOTIFICATION);
		Main.getLogger().info("Database connection successful (" + ip + ":" + port + "/" + dbName + ").");
		return true;
	}
	
	@Override
	public void disconnect() {
		try {
			if (dbConnection != null) {
				dbConnection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Main.getLogger().error("Could not close database connection (" + ip + ":" + port + "/" + dbName + ").");
		}
		isConnected.set(false);
		Main.getLogger().info("Database connection closed (" + ip + ":" + port + "/" + dbName + ").");
	}
	
	@Override
	public String toString() {
		return "MySQL";
	}
}
