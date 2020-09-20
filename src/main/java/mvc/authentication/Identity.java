package mvc.authentication;

public class Identity {

	private final String content;
	private final String id;
	private final String token;
	private final long expired;
	
	public Identity(String content, String id, long expired, String token) {
		this.content = content;
		this.id = id;
		this.expired = expired;
		this.token = token;
	}

	public String getContent() {
		return content;
	}

	protected String getId() {
		return id;
	}

	protected long getExpired() {
		return expired;
	}
	
	@Override
	public String toString() {
		return String.format("%s(%s): %s - %s", id, expired, content, token);
	}

	public String getToken() {
		return token;
	}
	
}
