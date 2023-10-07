package toti.answers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.Request;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.Headers;
import toti.answers.response.Response;
import toti.answers.response.TemplateResponse;
import toti.answers.response.TextResponse;
import toti.application.register.MappedAction;
import toti.application.register.Register;
import toti.security.Identity;
import toti.templating.TemplateFactory;

@RunWith(JUnitParamsRunner.class)
public class ExceptionAnswerTest {
	
	// TODO test custom exception catch
	
	@Test
	@Parameters(method="dataGetResponse")
	public void testGetResponse(
			String message,
			boolean isAsync, String ip, int saveToFile,
			Response expected) throws Exception {
		Logger logger = mock(Logger.class);
		Headers headers = mock(Headers.class);
		when(headers.isAsyncRequest()).thenReturn(isAsync);
		
		Request request = new Request(HttpMethod.GET, "/a/b/c", Protocol.HTTP_1_1);
		request.setUriParams(
			"/a/b/c",
			new MapDictionary<String>(new HashMap<>()).put("some", "param").put("another", "value")
		);
		request.setBody("some body".getBytes());
		
		Identity identity = mock(Identity.class);
		when(identity.getIP()).thenReturn(ip);
		
		TemplateFactory templateFactory = mock(TemplateFactory.class);
		
		ExceptionAnswer answer = spy(new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList("localhost"),
			templateFactory,
			null,
			mock(Translator.class),
			logger
		));
		doReturn(new TemplateResponse(StatusCode.OK, "/detail", new HashMap<>())).when(answer)
			.getExceptionDetail(any(), any(), any(), any(), any(), any());
		doReturn(new TemplateResponse(StatusCode.OK, "/info", new HashMap<>())).when(answer)
			.getExceptionInfo(any());
		doNothing().when(answer).saveToFile(any(), any(), any());
		
		
		assertEquals(expected, answer.getResponse(
			request,
			headers,
			StatusCode.I_AM_A_TEAPORT,
			new Exception("Some Exception", new RuntimeException("Another exception")),
			identity,
			MappedAction.test("a", "b", "c"),
			"charset"
		));
		verify(logger, times(1)).error(anyString(), any(Throwable.class));
		verify(answer, times(saveToFile)).saveToFile(any(), any(), any());
	}
	
	public Object[] dataGetResponse() {
		return new Object[] {
			new Object[] {
				"Sync request, dev ip",
				false, "localhost", 0, new TemplateResponse(StatusCode.OK, "/detail", new HashMap<>())
			},
			new Object[] {
				"Sync request, not dev ip",
				false, "42.42.42.42", 1, new TemplateResponse(StatusCode.OK, "/info", new HashMap<>())
			},

			new Object[] {
				"Async request, dev ip",
				true, "localhost", 1, new TextResponse(StatusCode.I_AM_A_TEAPORT, "class java.lang.Exception: Some Exception")
			},
			new Object[] {
				"Async request, not dev ip",
				true, "42.42.42.42", 1, new TextResponse(StatusCode.I_AM_A_TEAPORT, "I'm a teapot")
			},
		};
	}
	
}
