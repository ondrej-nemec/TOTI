package toti;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ji.common.structures.DictionaryValue;

public class Headers {
	
	private final Map<String, List<Object>> headers;
	
	public Headers() {
		this(new HashMap<>());
	}
	
	public Headers(Map<String, List<Object>> headers) {
		this.headers = headers;
	}
	
	public Headers addHeader(String name, Object value) {
		if (!headers.containsKey(name)) {
			headers.put(name, new LinkedList<>());
		}
		headers.get(name).add(value);
		return this;
	}
	
	public Map<String, List<Object>> getHeaders() {
		return headers;
	}
	
	public void setHeaders(Map<String, List<Object>> headers) {
		this.headers.putAll(headers);
	}
	
	public List<Object> getHeaders(String name) {
		return headers.get(name);
	}
	
	public Object getHeader(String name)  {
		if (headers.containsKey(name) && headers.get(name).size() > 0) {
			return headers.get(name).get(0);
		}
		return null;
	}
	
	public boolean containsHeader(String name) {
		return headers.containsKey(name) && headers.get(name).size() > 0;
	}
	
	public <T> T getHeader(String name, Class<T> clazz) {
		return new DictionaryValue(getHeader(name)).getValue(clazz);
	}

	public Headers clone() {
		Headers clone = new Headers(new HashMap<>());
		headers.forEach((name, list)->{
			list.forEach(value->clone.addHeader(name, value));
		});
		return clone;
	}
	
}
