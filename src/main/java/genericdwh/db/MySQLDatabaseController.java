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
			
	public MySQLDatabaseController(MySQLDatabaseReader dbReader, MySQLDatabaseWriter dbWriter) {
		this.dbReader = dbReader;
		this.dbWriter = dbWriter;
	}
	
	public boolean connect(String ip, String port, String dbName, String userName, String password) {
		MainWindowController mainWindowController = Main.getContext().getBean(MainWindowController.class);
		
		String dbDriver = com.mysql.jdbc.Driver.class.getName();
		try {
			Class.forName(dbDriver);
			dbConnection = DriverManager.getConnection("jdbc:mysql://"+ip+":"+port+"/"+dbName, userName, password);
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();
			if (errorCode == 1045) {
				mainWindowController.postStatus(StatusMessages.CONNECTION_INVALID_USERNAME_PW, Icons.WARNING);
				return false;
			}
			if (errorCode == 1049) {
				mainWindowController.postStatus(StatusMessages.CONNECTION_INVALID_DATABASE_SCHEMA, Icons.WARNING);
				return false;
			}
			mainWindowController.postStatus(StatusMessages.CONNECTION_FAILED, Icons.WARNING);
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
		return true;
	}
	
	public void disconnect() {
		try {
			if (dbConnection != null) {
				dbConnection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		isConnected.set(false);
	}
}
