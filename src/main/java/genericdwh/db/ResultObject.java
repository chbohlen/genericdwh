package genericdwh.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ResultObject {
	
	@Getter private long id;
	@Getter private Long[] componentIds;
	
	@Getter private long unitId;
	@Getter private double value;
}
