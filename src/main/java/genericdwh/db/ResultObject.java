package genericdwh.db;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ResultObject {
	
	@Getter private long id;
	@Getter private Long[] componentIds;
	
	@Getter private double value;
	@Getter private long unitId;
}
