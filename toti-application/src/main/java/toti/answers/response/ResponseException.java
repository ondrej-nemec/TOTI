package toti.answers.response;

public class ResponseException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ResponseException(Exception e) {
		super(e);
	}

}
