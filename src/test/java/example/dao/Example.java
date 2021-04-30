package example.dao;

import java.util.HashMap;
import java.util.Map;

import common.structures.MapDictionary;
import toti.application.Entity;

public class Example extends HashMap<String, Object> implements Entity{

	private static final long serialVersionUID = 1L;

	public Example(MapDictionary<String, Object> map) {
		putAll(map.toMap());
	}
	
	@Override
	public Map<String, Object> toMap() {
		return this;
	}

}
