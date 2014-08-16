package genericdwh.dataobjects;

import genericdwh.db.DatabaseControllerConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DatabaseControllerConfig.class)
public class DataObjectManagerConfig {
	
	@Autowired
	private DatabaseControllerConfig dbControllerConfig;
	
	@Bean
	public DimensionManager dimensionManager() {
		return new DimensionManager(dbControllerConfig.databaseController());
	}

	@Bean
	public ReferenceObjectManager referenceObjectManager() {
		return new ReferenceObjectManager(dbControllerConfig.databaseController());
	}
}
