package toti.application.extensions;

import toti.application.answers.request.Identity;
import toti.application.answers.request.Request;
import toti.application.answers.response.Response;
import toti.lib.tcpip.enums.StatusCode;

public interface CustomExceptionExtension {
	
	Response catchException(
		Request request,
		StatusCode status, Identity identity, TranslatorExtension translator, Throwable t,
		boolean isDevelopResponseAllowed, boolean isAsyncRequest
	);
	
}
