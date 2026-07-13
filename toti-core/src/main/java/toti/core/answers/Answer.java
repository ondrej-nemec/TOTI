package toti.core.answers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import toti.core.ServerException;
import toti.core.answers.request.Identity;
import toti.core.answers.request.IdentityFactory;
import toti.core.answers.request.Request;
import toti.core.answers.response.FinalResponse;
import toti.lib.tcpip.enums.StatusCode;

public class Answer {

	private final IdentityFactory identityFactory;
	
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
			Map<String, List<Object>>  responseHeaders,
			String charset) {
		this.identityFactory = identityFactory;
		this.exceptionAnswer = exceptionAnswer;
		this.totiAnswer = totiAnswer;
		this.fileSystemAnswer = fileSystemAnswer;
		this.controllerAnswer = controllerAnswer;
		this.charset = charset;
		this.responseHeaders = responseHeaders;
	}

	public FinalResponse accept(Request request, String ipAddress) throws IOException {
		Identity identity = identityFactory.createIdentity(
			request.getHeaders(),
			request.getQueryParams(),
			request.getBodyParams(),
			ipAddress
		);
		
		Headers responseHeaders = new Headers(this.responseHeaders);
		try {
			FinalResponse response;
			if (request.getUri().toLowerCase().startsWith("/toti")) {
				response = totiAnswer.answer(request, identity, responseHeaders, charset);
			} else {
				response = controllerAnswer.answer(
					request, identity, responseHeaders, charset
				);
			}
			if (response != null) {
				identityFactory.finalizeIdentity(identity, responseHeaders);
				return response;
			}
			return fileSystemAnswer.answer(request, responseHeaders, charset);
		} catch (ServerException e) {
			return exceptionAnswer.answer(
				request,
				e.getStatusCode(), e.getCause() == null ? e : e.getCause(),
				identity, e.getMappedAction(), responseHeaders, charset
			);
		} catch (Throwable t) {
			return exceptionAnswer.answer(
				request,
				StatusCode.INTERNAL_SERVER_ERROR, t,
				identity, null, responseHeaders, charset
			);
		}
	}
	
}
