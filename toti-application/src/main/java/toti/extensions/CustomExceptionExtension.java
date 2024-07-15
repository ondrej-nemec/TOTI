package toti.extensions;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.http.StatusCode;

public interface CustomExceptionExtension {
	
	Response catchException(
		Request request,
		StatusCode status, Identity identity, TranslatorExtension translator, Throwable t,
		boolean isDevelopResponseAllowed, boolean isAsyncRequest
	);
	
}
