package genericdwh.db;

import java.util.AbstractMap;
import java.util.Map.Entry;

import org.jooq.Record2;
import org.jooq.RecordMapper;

public class EntryLongLongRecordMapper implements RecordMapper<Record2<Long, Long>, Entry<Long, Long>> {
	
	@Override
	public Entry<Long, Long> map(Record2<Long, Long> record) {
		return new AbstractMap.SimpleEntry<Long, Long>((Long)record.getValue(0), (Long)record.getValue(1));
	}
}
