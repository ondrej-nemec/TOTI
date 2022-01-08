package toti;

import ji.socketCommunication.http.StatusCode;
import toti.url.MappedUrl;

public class ServerException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	private final StatusCode code;
	private MappedUrl url;

	public ServerException(StatusCode code, Throwable t) {
		this(code, null, t);
	}

	public ServerException(StatusCode code, MappedUrl url, Throwable t) {
		super(t);
		this.url = url;
		this.code = code;
	}

	public ServerException(StatusCode code, String message) {
		this(code, null, message);
	}

	public ServerException(StatusCode code, MappedUrl url, String message) {
		super(message);
		this.code = code;
		this.url = url;
	}

	public StatusCode getStatusCode() {
		return code;
	}

	public MappedUrl getUrl() {
		return url;
	}
	
}
