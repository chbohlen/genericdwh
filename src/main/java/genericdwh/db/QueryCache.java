package genericdwh.db;

import java.util.ArrayList;
import java.util.List;

public class QueryCache {
	
	private ArrayList<List<Long>> cachedParams = new ArrayList<>();
	private ArrayList<List<ResultObject>> cachedResults = new ArrayList<>();
		
	public void cache(List<Long> params, List<ResultObject> results) {
		for (List<Long> currParams : cachedParams) {
			if (currParams.containsAll(params)) {
				break;
			}
		}
		
		if (cachedParams.size() == 10) {
			cachedParams.remove(0);
			cachedResults.remove(0);
		}
		
		cachedParams.add(params);
		cachedResults.add(results);
	}
	
	public List<ResultObject> fetch(List<Long> params) {
		for (List<Long> currParams : cachedParams) {
			if (currParams.size() == params.size() && currParams.containsAll(params)) {
				return cachedResults.get(cachedParams.indexOf(currParams));
			}
		}
		
		return null;
	}
	
	public void clear() {
		cachedParams.clear();
		cachedResults.clear();
	}
}
