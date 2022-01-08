package toti.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ji.common.structures.DictionaryValue;
import toti.security.permissions.Permissions;

public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private final Object id;
	private Collection<Object> allowedIds;
	private final Permissions permissions;
	private final Map<String, Object> customData;
	
	public User(Object id, Permissions permissions) {
		this.permissions = permissions;
		this.id = id;
		this.customData = new HashMap<>();
	}
	
	protected Permissions getPermissions() {
		return permissions;
	}
	
	protected void setAllowedIds(Collection<Object> allowedIds) {
		this.allowedIds = allowedIds;
	}
	
	public Object getId() {
		return id;
	}
	
	public DictionaryValue getUnique() {
		return new DictionaryValue(id);
	}
	
	public Collection<Object> getAllowedIds() {
		return allowedIds;
	}

	@Override
	public String toString() {
		return String.format("User[%s]: %s", id, allowedIds);
	}
	
	public void setProperty(String key, Object value) {
		customData.put(key, value);
	}
	
	public DictionaryValue getProperty(String key) {
		return new DictionaryValue(customData.get(key));
	}

	public Map<String, Object> getContent() {
		return customData;
	}

}
