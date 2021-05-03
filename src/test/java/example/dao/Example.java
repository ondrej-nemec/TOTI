package example.dao;

import java.util.HashMap;
import java.util.Map;

import common.structures.MapDictionary;
import toti.application.Entity;

public class Example implements Entity {

	private Map<String, Object> map = new HashMap<String, Object>();
	
	public Example(MapDictionary<String, Object> map) {
		this.map.putAll(map.toMap());
	}
	
	@Override
	public Map<String, Object> toMap() {
		return map;
	}
	
	public void put(String key, Object value) {
		map.put(key, value);
	}

	@Override
	public String toString() {
		return map.toString();
	}
	
}
