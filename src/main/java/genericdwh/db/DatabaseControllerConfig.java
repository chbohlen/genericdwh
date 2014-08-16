package genericdwh.db;

import org.springframework.context.annotation.*;

@Configuration
public class DatabaseControllerConfig {
	
	@Bean
	public DatabaseController databaseController() {
		return new MySQLDatabaseController((MySQLDatabaseReader)databaseReader(), (MySQLDatabaseWriter)databaseWriter());
	}
	
	@Bean
	public DatabaseReader databaseReader() {
		return new MySQLDatabaseReader();
	}
	
	@Bean
	public DatabaseWriter databaseWriter() {
		return new MySQLDatabaseWriter();
	}
}
