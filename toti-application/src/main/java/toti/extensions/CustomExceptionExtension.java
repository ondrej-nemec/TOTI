package toti.extensions;

import ji.socketCommunication.http.StatusCode;
import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;

public interface CustomExceptionExtension {
	
	Response catchException(
		Request request,
		StatusCode status, Identity identity, TranslatorExtension translator, Throwable t,
		boolean isDevelopResponseAllowed, boolean isAsyncRequest
	);
	
}
