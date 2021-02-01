package toti.authentication;

import java.util.Collection;
import java.util.function.Function;

import acl.structures.AclUser;

public class Identity {
	
	public static Function<Identity, AclUser> TO_USER;

	class Ident {
		private final String content;
		private final String id;
		private final String token;
		private final long expired;
		private final boolean apiAllowed;
		
		public Ident(String content, String id, long expired, String token, boolean apiAllowed) {
			this.content = content;
			this.id = id;
			this.expired = expired;
			this.token = token;
			this.apiAllowed = apiAllowed;
		}
	}
	
	public static Identity empty() {
		return new Identity();
	}
	
	public static Identity get(String content, String id, long expired, String token, boolean apiAllowed) {
		return new Identity(content, id, expired, token, apiAllowed);
	}
	
	private Ident identity;
	
	private Collection<Object> ids;
	
	private Identity() {
		this.identity = null;
	}
	
	private Identity(String content, String id, long expired, String token, boolean apiAllowed) {
		this.identity = new Ident(content, id, expired, token, apiAllowed);
	}
	
	protected void set(String content, String id, long expired, String token, boolean apiAllowed) {
		this.identity = new Ident(content, id, expired, token, apiAllowed);
	}

	public String getContent() {
		return identity.content;
	}
	
	public boolean isApiAllowed() {
		if (identity == null) {
			return false;
		}
		return identity.apiAllowed;
	}
	
	public AclUser getUser() {
		return TO_USER.apply(this);
	}

	protected String getId() {
		return identity.id;
	}

	protected long getExpired() {
		return identity.expired;
	}
	
	public void setIds(Collection<Object> ids) {
		this.ids = ids;
	}
	
	public Collection<Object> getAllowedIds() {
		return ids;
	}
	
	@Override
	public String toString() {
		if (isPresent()) {
			return String.format("Identity %s(%s): %s - %s", identity.id, identity.expired, identity.content, identity.token);
		}
		return "Identity.empty";
	}

	protected String getToken() {
		return identity.token;
	}
	
	public boolean isPresent() {
		return identity != null;
	}
	
	public void clear() {
		this.identity = null;
	}
	
}
