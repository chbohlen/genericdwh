package genericdwh.dataobjects;

import genericdwh.dataobjects.dimension.DimensionManager;
import genericdwh.dataobjects.fact.FactManager;
import genericdwh.dataobjects.ratio.RatioManager;
import genericdwh.dataobjects.referenceobject.ReferenceObjectManager;
import genericdwh.dataobjects.unit.UnitManager;
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
		return new DimensionManager(dbControllerConfig.mySQLdatabaseController());
	}

	@Bean
	public ReferenceObjectManager referenceObjectManager() {
		return new ReferenceObjectManager(dbControllerConfig.mySQLdatabaseController());
	}
	
	@Bean
	public RatioManager ratioManager() {
		return new RatioManager(dbControllerConfig.mySQLdatabaseController());
	}
	
	@Bean
	public UnitManager unitManager() {
		return new UnitManager(dbControllerConfig.mySQLdatabaseController());
	}
	
	@Bean
	public FactManager factManager() {
		return new FactManager(dbControllerConfig.mySQLdatabaseController());
	}
	
	@Bean
	public ChangeManager changeManager() {
		return new ChangeManager(dimensionManager(), referenceObjectManager(), ratioManager(), unitManager(), factManager());
	}
}
