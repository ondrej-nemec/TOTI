package toti;

import ji.socketCommunication.http.StatusCode;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import ji.translator.Translator;
import toti.answers.response.Response;

public interface CustomExceptionResponse {
	
	Response catchException(
		Request request,
		StatusCode status, Identity identity, Translator translator, Throwable t,
		boolean isDevelopResponseAllowed, boolean isAsyncRequest
	);
	
}
