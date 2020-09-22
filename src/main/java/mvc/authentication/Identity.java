package mvc.authentication;

public class Identity {

	class Ident {
		private final String content;
		private final String id;
		private final String token;
		private final long expired;
		
		public Ident(String content, String id, long expired, String token) {
			this.content = content;
			this.id = id;
			this.expired = expired;
			this.token = token;
		}
	}
	
	public static Identity empty() {
		return new Identity();
	}
	
	public static Identity get(String content, String id, long expired, String token) {
		return new Identity(content, id, expired, token);
	}
	
	private Ident identity;
	
	private Identity() {
		this.identity = null;
	}
	
	private Identity(String content, String id, long expired, String token) {
		this.identity = new Ident(content, id, expired, token);
	}
	
	protected void set(String content, String id, long expired, String token) {
		this.identity = new Ident(content, id, expired, token);
	}

	public String getContent() {
		return identity.content;
	}

	protected String getId() {
		return identity.id;
	}

	protected long getExpired() {
		return identity.expired;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s): %s - %s", identity.id, identity.expired, identity.content, identity.token);
	}

	public String getToken() {
		return identity.token;
	}
	
	public boolean isPresent() {
		return identity != null;
	}
	
	public void clear() {
		this.identity = null;
	}
	
}
