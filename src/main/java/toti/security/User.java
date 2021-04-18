package toti.security;

import java.util.Collection;

import toti.security.permissions.Permissions;

public class User {
	
	private final Object id;
	private Collection<Object> allowedIds;
	private final Permissions permissions;
	
	public User(Object id, Permissions permissions) {
		this.permissions = permissions;
		this.id = id;
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
	
	public Collection<Object> getAllowedIds() {
		return allowedIds;
	}

	@Override
	public String toString() {
		return String.format("User[%s]: %s", id, allowedIds);
	}
	
}
