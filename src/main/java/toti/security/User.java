package toti.security;

import java.util.Collection;

import ji.common.structures.DictionaryValue;
import ji.common.structures.MapDictionary;
import toti.security.permissions.Permissions;

public class User {
	
	private final Object id;
	private Collection<Object> allowedIds;
	private final Permissions permissions;
	private final MapDictionary<String, Object> customData;
	
	public User(Object id, Permissions permissions) {
		this.permissions = permissions;
		this.id = id;
		this.customData = MapDictionary.hashMap();
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
		return customData.getDictionaryValue(key);
	}
	
	public MapDictionary<String, Object> getContent() {
		return customData;
	}
	
}
