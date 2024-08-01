package ji.database.support;

import java.util.HashMap;
import java.util.Map;

import ji.common.structures.MapDictionary;

public class DatabaseRow extends MapDictionary<String> {

	public DatabaseRow() {
		super(new HashMap<>());
	}
	
	public void addValue(String key, Object value) {
		// to lower case - some db change column name
		put(key.toLowerCase(), value);
	}
	
	@Override
	public DatabaseRow put(String key, Object value) {
		super.put(key, value);
		return this;
	}
	
	@Override
	public DatabaseRow putAll(Map<String, Object> values) {
		super.putAll(values);
		return this;
	}
	
	public Map<String, Object> getValues() {
		return toMap();
	}
	
}
