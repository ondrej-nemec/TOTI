package toti.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

import common.structures.Dictionary;

public class User implements Dictionary, Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Map<String, Object> params = new HashMap<>();
	
	private final Object id;
	
	public User(Object id) {
		this.id = id;
	}
	
	public Object getId() {
		return id;
	}
	
	public User addParam(String name, Object value) {
		params.put(name, value);
		return this;
	}
	
	public Collection<Object> getAllowedIds() {
		return null; // TODO
	}

	@Override
	public Object getValue(String name) {
		return params.get(name);
	}
	
	@Override
	public String toString() {
		return String.format("User[%s]: params", id, params);
	}
	
}
