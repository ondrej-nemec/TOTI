package toti.answers;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapDictionary;
import ji.files.text.Text;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.Request;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.answers.request.Identity;
import toti.answers.response.Response;
import toti.answers.response.TemplateResponse;
import toti.answers.response.TextResponse;
import toti.application.register.MappedAction;
import toti.application.register.Register;
import toti.extensions.CustomExceptionExtension;
import toti.extensions.Translator;
import toti.extensions.TranslatorExtension;
import toti.logging.FileName;

@RunWith(JUnitParamsRunner.class)
public class ExceptionAnswerTest {
	
	// TODO test exception/error templates - after testing it in samples
	// TODO test create real log file - need mock file
	
	@Test
	public void testAnswer() {
		Request request = new Request(HttpMethod.GET, "/wrong", Protocol.HTTP_2);
		request.setUriParams("/wrong", MapDictionary.hashMap());
		
		Identity identity = mock(Identity.class);
		
		Translator translator = mock(Translator.class);
		TranslatorExtension translatorExtension = mock(TranslatorExtension.class);
		
		
		Headers reqHeaders = mock(Headers.class);
		when(reqHeaders.isAsyncRequest()).thenReturn(true);
		
		Headers resHeaders = new Headers();
		resHeaders.addHeader("test", "header");
		
		ExceptionAnswer answer = spy(new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList("localhost"),
			null,
			translatorExtension,
			mock(Logger.class)
		));
		
		ji.socketCommunication.http.structures.Response expected
		= new ji.socketCommunication.http.structures.Response(StatusCode.I_AM_A_TEAPORT, Protocol.HTTP_2);
		expected.addHeader("test", "header");
		expected.addHeader("content-type", "text/plain");
		expected.setBody("I'm a teapot".getBytes());
		
		assertEquals(expected, answer.answer(
			request, reqHeaders, StatusCode.I_AM_A_TEAPORT, new Throwable(), identity, null, resHeaders, "charset"
		));
		verify(translatorExtension, times(1)).getTranslator(identity);
		verifyNoMoreInteractions(translator);
	}
	
	@Test
	public void testCustomExceptionResponse() {
		Logger logger = mock(Logger.class);
		
		CustomExceptionExtension custom = new CustomExceptionExtension() {
			@Override
			public Response catchException(toti.answers.request.Request request, StatusCode status, Identity identity,
				TranslatorExtension translator, Throwable t, boolean isDevelopResponseAllowed, boolean isAsyncRequest) {
				return new TextResponse(StatusCode.ACCEPTED, new Headers(), "catched");
			}
		};
		
		Register register = mock(Register.class);
		when(register.getCustomExceptionResponse()).thenReturn(custom);
		
		ExceptionAnswer answer = spy(new ExceptionAnswer(
			register,
			Arrays.asList("localhost"),
			null,
			mock(TranslatorExtension.class),
			logger
		));
		
		Response response = answer.getResponse(
			mock(Request.class),
			mock(Headers.class), 
			StatusCode.I_AM_A_TEAPORT, 
			mock(Throwable.class),
			mock(Identity.class),
			mock(MappedAction.class),
			""
		);
		assertEquals(new TextResponse(StatusCode.ACCEPTED, new Headers(), "catched"), response);
		verify(logger, times(1)).error(anyString(), any(Throwable.class));
		
	}
	
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
		
		ExceptionAnswer answer = spy(new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList("localhost"),
			null,
			mock(TranslatorExtension.class),
			logger
		));
		doReturn(new TemplateResponse(StatusCode.OK, new Headers(), "/detail", new HashMap<>())).when(answer)
			.getExceptionDetail(any(), any(), any(), any(), any(), any());
		doReturn(new TemplateResponse(StatusCode.OK, new Headers(), "/info", new HashMap<>())).when(answer)
			.getExceptionInfo(any());
		doReturn(0).when(answer).saveToFile(any(), any(), any(), any());
		doReturn(new FileName(null, false)).when(answer).getFileName(any(), anyInt(), any(), any(), any());
		
		
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
		verify(answer, times(saveToFile)).saveToFile(any(), any(), any(), any());
		verify(answer, times(1)).getFileName(any(), anyInt(), any(), any(), any());
	}
	
	public Object[] dataGetResponse() {
		return new Object[] {
			new Object[] {
				"Sync request, dev ip",
				false, "localhost", 0, new TemplateResponse(StatusCode.OK, new Headers(), "/detail", new HashMap<>())
			},
			new Object[] {
				"Sync request, not dev ip",
				false, "42.42.42.42", 1, new TemplateResponse(StatusCode.OK, new Headers(), "/info", new HashMap<>())
			},

			new Object[] {
				"Async request, dev ip",
				true, "localhost", 1, new TextResponse(StatusCode.I_AM_A_TEAPORT, new Headers(), "class java.lang.Exception: Some Exception")
			},
			new Object[] {
				"Async request, not dev ip",
				true, "42.42.42.42", 1, new TextResponse(StatusCode.I_AM_A_TEAPORT, new Headers(), "I'm a teapot")
			},
		};
	}
	
	@Test
	@Parameters(method="dataGetFileNameReturnsCorrectFilename")
	public void testGetFileNameReturnsCorrectFilename(
			String logsPath,
			MappedAction action, StatusCode code, Throwable t,
			FileName expected
		) {
		LocalDateTime now = LocalDateTime.of(2023, 10, 9, 21, 33, 45, 876);
		int random = 123456789;
		ExceptionAnswer answer = new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList(),
			logsPath,
			mock(TranslatorExtension.class),
			mock(Logger.class)
		);
		assertEquals("First run", expected, answer.getFileName(now, random, action, code, t));
		
		FileName secondExpected = new FileName(expected.getName(), false);
		assertEquals("Second run", secondExpected, answer.getFileName(now.plusHours(1), random*2, action, code, t));
	}
	
	public Object[] dataGetFileNameReturnsCorrectFilename() {
		return new Object[] {
			new Object[] {
				null,
				MappedAction.test("a", "b", "c"), StatusCode.ACCEPTED, new Throwable(),
				new FileName(null, false)
			},
			new Object[] {
				"/logs",
				MappedAction.test("a", "b", "c"), StatusCode.ACCEPTED, new Throwable(),
				new FileName("/logs/exception-2023-10-09_21-33-45__123456789.html", true)
			}
		};
	}
	
	@Test
	@Parameters(method="dataSaveToFileDoNothingIfFileIsNotCreateAndNotUsed")
	public void testSaveToFileDoNothingIfFileIsNotCreateAndNotUsed(FileName filename) {
		Text text = mock(Text.class);
		TemplateResponse templateResponse = mock(TemplateResponse.class);
		
		ExceptionAnswer answer = new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList(),
			null,
			mock(TranslatorExtension.class),
			mock(Logger.class)
		);
		assertEquals(-1, answer.saveToFile(filename, templateResponse, "charset", text));
		
		verifyNoMoreInteractions(text, templateResponse);
	}
	
	public Object[] dataSaveToFileDoNothingIfFileIsNotCreateAndNotUsed() {
		return new Object[] {
			new Object[] {
				new FileName(null, false)
			},
			new Object[] {
				new FileName("something", false)
			},
			new Object[] {
				new FileName(null, true)
			}
		};
	}

	@Test
	public void testSaveToFileSavefile() throws Exception {
		Text text = mock(Text.class);
		TemplateResponse templateResponse = mock(TemplateResponse.class);
	//	when(templateResponse.createResponse(any())).thenReturn("templateContent");
		
		ExceptionAnswer answer = new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList(),
			null,
			mock(TranslatorExtension.class),
			mock(Logger.class)
		);
		assertEquals(0, answer.saveToFile(new FileName("/path/to/file", true), templateResponse, "charset", text));
		
		verify(text, times(1)).write(any(), eq("/path/to/file"), eq("charset"), eq(false));
		// verify(templateResponse, times(1)).createResponse(any());
		verifyNoMoreInteractions(text, templateResponse);
	}
}
