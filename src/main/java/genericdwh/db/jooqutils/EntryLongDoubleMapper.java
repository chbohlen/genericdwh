package genericdwh.db.jooqutils;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.jooq.Record2;
import org.jooq.RecordMapper;

public class EntryLongDoubleMapper implements RecordMapper<Record2<Long, Double>, Entry<Long, Double>> {

	@Override
	public Entry<Long, Double> map(Record2<Long, Double> record) {
		return new AbstractMap.SimpleEntry<Long, Double>((Long)record.getValue(0), (Double)record.getValue(1));
	}
}
