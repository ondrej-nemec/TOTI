package mvc.authentication;

public class Identity {

	private final String content;
	private final String id;
	private final long expired;
	
	public Identity(String content, String id, long expired) {
		this.content = content;
		this.id = id;
		this.expired = expired;
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
	
}
