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
	
	private boolean hasChanged = false;
	
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
	
	/**
	 * Returns user ID in origin type
	 * @return ID
	 */
	public Object getId() {
		return id;
	}
	
	/**
	 * Returns user ID as DictionaryValue
	 * @return ID
	 */
	public DictionaryValue getUnique() {
		return new DictionaryValue(id);
	}
	
	/**
	 * Returns list of allowed IDs / owners for current domain
	 * @return list of IDs
	 */
	public Collection<Object> getAllowedIds() {
		return allowedIds;
	}

	@Override
	public String toString() {
		return String.format("User[%s]: %s", id, allowedIds);
	}
	
	/**
	 * Add some value to storage
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, Object value) {
		customData.put(key, value);
		hasChanged = true;
	}
	
	/**
	 * Get some value from storage
	 * @param key
	 * @return value as DictionaryValue
	 */
	public DictionaryValue getProperty(String key) {
		return new DictionaryValue(customData.get(key));
	}

	/**
	 * Get storage
	 * @return
	 */
	public Map<String, Object> getContent() {
		return customData;
	}

	protected boolean hasChange() {
		return hasChanged;
	}
	
	protected void setChanged(boolean changed) {
		this.hasChanged = changed;
	}
	
}
