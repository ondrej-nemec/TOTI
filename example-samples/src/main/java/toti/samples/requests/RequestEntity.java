package toti.samples.requests;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ext.Entity;

public class RequestEntity implements Entity {

	private String name;
	private int age;
	private List<Object> list = new LinkedList<>();
	private Map<String, Object> map = new HashMap<>();
	
	public void putMap(String key, Object value) {
		map.put(key, value);
	}
	
	public void putList(Object value) {
		list.add(value);
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Entity:");
		builder.append("\tname: " + name);
		builder.append("\tage:" + age);
		builder.append("\tlist: " + list);
		builder.append("\tmap: " + map);
		return builder.toString();
	}
	
}
