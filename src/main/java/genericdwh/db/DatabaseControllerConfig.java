package genericdwh.db;

import org.springframework.context.annotation.*;

@Configuration
public class DatabaseControllerConfig {
	
	@Bean
	public DatabaseController databaseController() {
		return new MySQLDatabaseController();
	}
}
