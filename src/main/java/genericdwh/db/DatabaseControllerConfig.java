package genericdwh.db;

import org.springframework.context.annotation.*;

@Configuration
public class DatabaseControllerConfig {
	
	private final static String DBDRIVER = com.mysql.jdbc.Driver.class.getName();
	private final static String DBMS = "mysql";
	
	@Bean
	public DatabaseController databaseController() {
		return new JDBCDatabaseController(DBDRIVER, DBMS);
	}
}
