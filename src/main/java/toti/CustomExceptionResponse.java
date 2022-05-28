package toti;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Request;
import ji.translator.Translator;
import toti.response.Response;
import toti.security.Identity;
import toti.url.MappedUrl;

public interface CustomExceptionResponse {

	Response catchException(
		StatusCode status,
		Request request,
		Identity identity,
		MappedUrl mappedUrl, // can be null !!
		Throwable t,
		Translator translator,
		boolean isDevelopResponseAllowed,
		boolean isAsyncRequest
	);
	
}
