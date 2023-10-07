package toti.answers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Request;
import ji.socketCommunication.http.structures.Response;
import ji.socketCommunication.http.structures.WebSocket;
import toti.Headers;
import toti.ServerException;
import toti.security.Authenticator;
import toti.security.Identity;
import toti.security.IdentityFactory;

public class Answer implements ji.socketCommunication.http.ResponseFactory {

	private final IdentityFactory identityFactory;
	private final Authenticator authenticator;
	
	private final String charset;
	private final Map<String, List<Object>> responseHeaders;
	
	private final ExceptionAnswer exceptionAnswer;
	private final ControllerAnswer controllerAnswer;
	private final FileSystemAnswer fileSystemAnswer;
	private final TotiAnswer totiAnswer;
	
	public Answer(
			ExceptionAnswer exceptionAnswer,
			ControllerAnswer controllerAnswer,
			FileSystemAnswer fileSystemAnswer,
			TotiAnswer totiAnswer,
			IdentityFactory identityFactory,
			Authenticator authenticator,
			Map<String, List<Object>>  responseHeaders,
			String charset) {
		this.identityFactory = identityFactory;
		this.authenticator = authenticator;
		this.exceptionAnswer = exceptionAnswer;
		this.totiAnswer = totiAnswer;
		this.fileSystemAnswer = fileSystemAnswer;
		this.controllerAnswer = controllerAnswer;
		this.charset = charset;
		this.responseHeaders = responseHeaders;
	}

	@Override
	public Response accept(
			Request request, String ipAddress, Optional<WebSocket> webSocket
		) throws IOException {
		Headers requestHeaders = new Headers(request.getHeaders());
		Identity identity = identityFactory.createIdentity(requestHeaders, ipAddress, null); // TODO profiler
		authenticator.authenticate(identity, request.getBodyInParameters());
		Headers responseHeaders = new Headers(this.responseHeaders);
		try {
			if (request.getUri().toLowerCase().startsWith("/toti")) {
				return totiAnswer.answer(request, requestHeaders, identity, responseHeaders, charset);
			}
			Response response = controllerAnswer.answer(
				request, identity, requestHeaders, webSocket, responseHeaders, charset
			);
			if (response != null) {
				return response;
			}
			return fileSystemAnswer.answer(request, responseHeaders, charset);
		} catch (ServerException e) {
			return exceptionAnswer.answer(
				request, requestHeaders,
				e.getStatusCode(), e.getCause() == null ? e : e.getCause(),
				identity, e.getMappedAction(), responseHeaders, charset
			);
		} catch (Throwable t) {
			return exceptionAnswer.answer(
				request, requestHeaders,
				StatusCode.INTERNAL_SERVER_ERROR, t,
				identity, null, responseHeaders, charset
			);
		}
	}

}
