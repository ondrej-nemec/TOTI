package toti;

import ji.socketCommunication.http.StatusCode;
import toti.application.register.MappedAction;

public class ServerException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private final StatusCode code;
	private MappedAction url;

	public ServerException(StatusCode code, Throwable t) {
		this(code, null, t);
	}

	public ServerException(StatusCode code, MappedAction url, Throwable t) {
		super(t);
		this.url = url;
		this.code = code;
	}

	public ServerException(StatusCode code, String message) {
		this(code, null, message);
	}

	public ServerException(StatusCode code, MappedAction url, String message) {
		super(message);
		this.code = code;
		this.url = url;
	}

	public StatusCode getStatusCode() {
		return code;
	}

	public MappedAction getUrl() {
		return url;
	}
	
}
