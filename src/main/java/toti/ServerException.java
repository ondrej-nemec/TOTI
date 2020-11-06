package toti;

public class ServerException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private final int code;

	public ServerException(int code, Throwable t) {
		super(t);
		this.code = code;
	}

	public ServerException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
