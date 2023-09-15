package toti.answers.action;

import toti.answers.response.Response;

public class RequestInterruptedException extends Exception {

	private static final long serialVersionUID = 1L;

	private final Response response;
	
	public RequestInterruptedException(Response response) {
		this.response = response;
	}
	
	public Response getResponse() {
		return response;
	}
	
}
