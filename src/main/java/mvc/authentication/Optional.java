package mvc.authentication;

public class Optional {
	
	public static Optional of(Identity t) {
		return new Optional(t);
	}
	
	public static Optional empty() {
		return new Optional(null);
	}
	
	private Identity t;
	
	private Optional(Identity t) {
		this.t = t;
	}
	
	public boolean isPresent() {
		return t != null;
	}
	
	public Identity get() {
		return t;
	}
	
	public void set(Identity t) {
		this.t = t;
	}
	
	public void clear() {
		if (t != null) {
			this.t = new Identity("", "", 0, "");
		}
	}
	
	@Override
	public String toString() {
		if (t == null) {
			return "Optional.empty";
		}
		return String.format("Optional[%s]", t);
	}

}
