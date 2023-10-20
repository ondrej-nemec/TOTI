package toti.answers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.Request;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.socketCommunication.http.structures.Response;
import toti.ServerException;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.application.register.MappedAction;

public class AnswerTest {
	
	@Test
	public void testAcceptWithToti() throws Exception {
		ExceptionAnswer exceptionAnswer = mock(ExceptionAnswer.class);
		ControllerAnswer controllerAnswer = mock(ControllerAnswer.class);
		FileSystemAnswer fileSystemAnswer = mock(FileSystemAnswer.class);
		TotiAnswer totiAnswer = mock(TotiAnswer.class);
		when(totiAnswer.answer(any(), any(), any(), any(), any())).thenReturn(getResponse("TOTI response"));
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		Identity identity = mock(Identity.class);
		when(identityFactory.createIdentity(any(), any(), any(), any())).thenReturn(identity);
		
		Answer answer = new Answer(
			exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer,
			identityFactory, getHeadersRes(), "charset"
		);
		
		Request request = getRequest("/toti");
		assertEquals(getResponse("TOTI response"), answer.accept(request, "ip:ad:dr:ess", Optional.empty()));
		
		Headers expectedHeaders = new Headers(getHeaders());
		
		verify(identityFactory, times(1))
			.createIdentity(expectedHeaders, getQueryParams(), getBodyParams(), "ip:ad:dr:ess");
		
		verify(totiAnswer, times(1)).answer(request, expectedHeaders, identity, new Headers(getHeadersRes()), "charset");
		
		verifyNoMoreInteractions(exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer, identityFactory);
	}
	
	@Test
	public void testAcceptWithController() throws Exception {
		ExceptionAnswer exceptionAnswer = mock(ExceptionAnswer.class);
		ControllerAnswer controllerAnswer = mock(ControllerAnswer.class);
		when(controllerAnswer.answer(any(), any(), any(), any(), any(), any()))
			.thenReturn(getResponse("Controller response"));
		FileSystemAnswer fileSystemAnswer = mock(FileSystemAnswer.class);
		TotiAnswer totiAnswer = mock(TotiAnswer.class);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		Identity identity = mock(Identity.class);
		when(identityFactory.createIdentity(any(), any(), any(), any())).thenReturn(identity);
		
		Answer answer = new Answer(
			exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer,
			identityFactory, getHeadersRes(), "charset"
		);
		
		Request request = getRequest("/some/url");
		assertEquals(getResponse("Controller response"), answer.accept(request, "ip:ad:dr:ess", Optional.empty()));
		
		Headers expectedHeaders = new Headers(getHeaders());
		
		verify(identityFactory, times(1))
			.createIdentity(expectedHeaders, getQueryParams(), getBodyParams(), "ip:ad:dr:ess");
		
		verify(controllerAnswer, times(1))
			.answer(request, identity, expectedHeaders, Optional.empty(), new Headers(getHeadersRes()), "charset");
		
		verifyNoMoreInteractions(exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer, identityFactory);
	}
	
	@Test
	public void testAcceptWithDir() throws Exception {
		ExceptionAnswer exceptionAnswer = mock(ExceptionAnswer.class);
		ControllerAnswer controllerAnswer = mock(ControllerAnswer.class);
		when(controllerAnswer.answer(any(), any(), any(), any(), any(), any())).thenReturn(null);
		FileSystemAnswer fileSystemAnswer = mock(FileSystemAnswer.class);
		when(fileSystemAnswer.answer(any(), any(), any())).thenReturn(getResponse("File response"));
		TotiAnswer totiAnswer = mock(TotiAnswer.class);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		Identity identity = mock(Identity.class);
		when(identityFactory.createIdentity(any(), any(), any(), any())).thenReturn(identity);
		
		Answer answer = new Answer(
			exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer,
			identityFactory, getHeadersRes(), "charset"
		);
		
		Request request = getRequest("/some/url");
		assertEquals(getResponse("File response"), answer.accept(request, "ip:ad:dr:ess", Optional.empty()));
		
		Headers expectedHeaders = new Headers(getHeaders());
		
		verify(identityFactory, times(1))
			.createIdentity(expectedHeaders, getQueryParams(), getBodyParams(), "ip:ad:dr:ess");
		
		verify(controllerAnswer, times(1))
			.answer(request, identity, expectedHeaders, Optional.empty(), new Headers(getHeadersRes()), "charset");
		verify(fileSystemAnswer, times(1)).answer(request, new Headers(getHeadersRes()), "charset");
		
		verifyNoMoreInteractions(exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer, identityFactory);
	}
	
	@Test
	public void testAcceptThrowsServerException() throws Exception {
		ExceptionAnswer exceptionAnswer = mock(ExceptionAnswer.class);
		when(exceptionAnswer.answer(any(), any(), any(), any(), any(), any(), any(), any()))
			.thenReturn(getResponse("Server exception response"));
		ControllerAnswer controllerAnswer = mock(ControllerAnswer.class);
		MappedAction mappedAction = mock(MappedAction.class);
		Exception cause = mock(Exception.class);
		ServerException exception = new ServerException(StatusCode.BAD_REQUEST, mappedAction, cause); 
		when(controllerAnswer.answer(any(), any(), any(), any(), any(), any())).thenThrow(exception);
		FileSystemAnswer fileSystemAnswer = mock(FileSystemAnswer.class);
		TotiAnswer totiAnswer = mock(TotiAnswer.class);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		Identity identity = mock(Identity.class);
		when(identityFactory.createIdentity(any(), any(), any(), any())).thenReturn(identity);
		
		Answer answer = new Answer(
			exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer,
			identityFactory, getHeadersRes(), "charset"
		);
		
		Request request = getRequest("/something");
		assertEquals(getResponse("Server exception response"), answer.accept(request, "ip:ad:dr:ess", Optional.empty()));
		
		Headers expectedHeaders = new Headers(getHeaders());
		
		verify(identityFactory, times(1))
			.createIdentity(expectedHeaders, getQueryParams(), getBodyParams(), "ip:ad:dr:ess");
		
		verify(exceptionAnswer, times(1)).answer(
			request, expectedHeaders, StatusCode.BAD_REQUEST,
			cause, identity, mappedAction, 
			new Headers(getHeadersRes()), "charset"
		);
		verify(controllerAnswer, times(1)).answer(any(), any(), any(), any(), any(), any());
		
		verifyNoMoreInteractions(exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer, identityFactory);
	}
	
	@Test
	public void testAcceptThrowsServerExceptionWithoutCause() throws Exception {
		ExceptionAnswer exceptionAnswer = mock(ExceptionAnswer.class);
		when(exceptionAnswer.answer(any(), any(), any(), any(), any(), any(), any(), any()))
			.thenReturn(getResponse("Server exception response"));
		ControllerAnswer controllerAnswer = mock(ControllerAnswer.class);
		MappedAction mappedAction = mock(MappedAction.class);
		ServerException cause = new ServerException(StatusCode.BAD_REQUEST, mappedAction, "test exception");
		when(controllerAnswer.answer(any(), any(), any(), any(), any(), any())).thenThrow(cause);
		
		FileSystemAnswer fileSystemAnswer = mock(FileSystemAnswer.class);
		TotiAnswer totiAnswer = mock(TotiAnswer.class);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		Identity identity = mock(Identity.class);
		when(identityFactory.createIdentity(any(), any(), any(), any())).thenReturn(identity);
		
		Answer answer = new Answer(
			exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer,
			identityFactory, getHeadersRes(), "charset"
		);
		
		Request request = getRequest("/something");
		assertEquals(getResponse("Server exception response"), answer.accept(request, "ip:ad:dr:ess", Optional.empty()));
		
		Headers expectedHeaders = new Headers(getHeaders());
		
		verify(identityFactory, times(1))
			.createIdentity(expectedHeaders, getQueryParams(), getBodyParams(), "ip:ad:dr:ess");
		
		verify(exceptionAnswer, times(1)).answer(
			request, expectedHeaders, StatusCode.BAD_REQUEST,
			cause, identity, mappedAction, 
			new Headers(getHeadersRes()), "charset"
		);
		verify(controllerAnswer, times(1)).answer(any(), any(), any(), any(), any(), any());
		verifyNoMoreInteractions(exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer, identityFactory);
	}
	
	@Test
	public void testAcceptThrowsThrowable() throws Exception {
		ExceptionAnswer exceptionAnswer = mock(ExceptionAnswer.class);
		when(exceptionAnswer.answer(any(), any(), any(), any(), any(), any(), any(), any()))
			.thenReturn(getResponse("Throwable response"));
		ControllerAnswer controllerAnswer = mock(ControllerAnswer.class);
		Exception throwable = new Exception();
		when(controllerAnswer.answer(any(), any(), any(), any(), any(), any())).thenThrow(throwable);
		FileSystemAnswer fileSystemAnswer = mock(FileSystemAnswer.class);
		TotiAnswer totiAnswer = mock(TotiAnswer.class);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		Identity identity = mock(Identity.class);
		when(identityFactory.createIdentity(any(), any(), any(), any())).thenReturn(identity);
		
		Answer answer = new Answer(
			exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer,
			identityFactory, getHeadersRes(), "charset"
		);
		
		Request request = getRequest("/something");
		assertEquals(getResponse("Throwable response"), answer.accept(request, "ip:ad:dr:ess", Optional.empty()));
		
		Headers expectedHeaders = new Headers(getHeaders());
		
		verify(identityFactory, times(1))
			.createIdentity(expectedHeaders, getQueryParams(), getBodyParams(), "ip:ad:dr:ess");
		
		verify(exceptionAnswer, times(1)).answer(
			request, expectedHeaders, StatusCode.INTERNAL_SERVER_ERROR,
			throwable, identity, null, 
			new Headers(getHeadersRes()), "charset"
		);
		verify(controllerAnswer, times(1)).answer(any(), any(), any(), any(), any(), any());
		
		verifyNoMoreInteractions(exceptionAnswer, controllerAnswer, fileSystemAnswer, totiAnswer, identityFactory);
	}
	
	private Response getResponse(String body) {
		Response response = new Response(StatusCode.ACCEPTED, Protocol.HTTP_1_1);
		response.setBody(body.getBytes());
		return response;
	}

	private Map<String, List<Object>> getHeadersRes() {
		Map<String, List<Object>> headers = new HashMap<>();
		headers.put("response", Arrays.asList("header"));
		return headers;
	}

	private Request getRequest(String uri) {
		Request request = new Request(HttpMethod.GET, uri, Protocol.HTTP_1_1);
		request.setUriParams(uri, getQueryParams());
		request.setHeaders(getHeaders());
		request.setBodyFormData(getBodyParams());
		return request;
	}
	
	private Map<String, List<Object>> getHeaders() {
		Map<String, List<Object>> headers = new HashMap<>();
		headers.put("some", Arrays.asList("header"));
		return headers;
	}

	private RequestParameters getBodyParams() {
		return new RequestParameters().put("request", "body");
	}

	private MapDictionary<String> getQueryParams() {
		MapDictionary<String> res = MapDictionary.hashMap();
		res.put("query", "parameter");
		return res;
	}
	
}
