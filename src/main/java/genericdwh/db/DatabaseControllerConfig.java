package genericdwh.db;

import org.springframework.context.annotation.*;

@Configuration
public class DatabaseControllerConfig {
	
	@Bean
	public DatabaseController mySQLdatabaseController() {
		return new MySQLDatabaseController((MySQLDatabaseReader)mySQLdatabaseReader(), (MySQLDatabaseWriter)mySQLdatabaseWriter());
	}
	
	@Bean
	public DatabaseReader mySQLdatabaseReader() {
		return new MySQLDatabaseReader();
	}

	@Bean
	public DatabaseWriter mySQLdatabaseWriter() {
		return new MySQLDatabaseWriter();
	}
}
