package toti.answers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import toti.files.text.Text;

import org.junit.jupiter.params.provider.MethodSource;

import toti.answers.request.Identity;
import toti.answers.request.Request;
import toti.answers.response.FinalResponse;
import toti.answers.response.Response;
import toti.answers.response.TextResponse;
import toti.application.register.MappedAction;
import toti.application.register.Register;
import toti.common.structures.MapDictionary;
import toti.extensions.CustomExceptionExtension;
import toti.extensions.Translator;
import toti.extensions.TranslatorExtension;
import toti.logging.FileName;
import toti.tcpip.enums.HttpMethod;
import toti.tcpip.enums.StatusCode;
import toti.tcpip.structures.RequestParameters;

public class ExceptionAnswerTest {
	
	// TODO test exception/error templates - after testing it in samples
	// TODO test create real log file - need mock file
	
	@Test
	public void testAnswer() {
		Request request = new Request(
			"/wrong",HttpMethod.GET, new Headers(),
			MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		);
		
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
		
		FinalResponse expected = new FinalResponse(
			StatusCode.I_AM_A_TEAPORT,
			new Headers()
			.addHeader("test", "header")
			.addHeader("content-type", "text/plain"),
			"I'm a teapot"
		);
		
		assertEquals(expected, answer.answer(
			request, StatusCode.I_AM_A_TEAPORT, new Throwable(), identity, null, resHeaders, "charset"
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
			StatusCode.I_AM_A_TEAPORT, 
			mock(Throwable.class),
			mock(Identity.class),
			mock(MappedAction.class),
			""
		);
		assertEquals(new TextResponse(StatusCode.ACCEPTED, new Headers(), "catched"), response);
		verify(logger, times(1)).error(anyString(), any(Throwable.class));
		
	}
	
	@ParameterizedTest
	@MethodSource("dataGetResponse")
	public void testGetResponse(
			String message,
			boolean isAsync, String ip, int saveToFile,
			Response expected) throws Exception {
		Logger logger = mock(Logger.class);
		Headers headers = mock(Headers.class);
		when(headers.isAsyncRequest()).thenReturn(isAsync);
		
		Request request = new Request(
			"/a/b/c", HttpMethod.GET, new Headers(),
			new MapDictionary<String>(new HashMap<>()).put("some", "param").put("another", "value"),
			new RequestParameters(),
			"some body".getBytes(),
			Optional.empty()
		);
		
		Identity identity = mock(Identity.class);
		when(identity.getIP()).thenReturn(ip);
		
		ExceptionAnswer answer = spy(new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList("localhost"),
			null,
			mock(TranslatorExtension.class),
			logger
		));
		doReturn("DetailedException").when(answer).getExceptionDetail(any(), any(), any(), any(), any());
		doReturn("ExceptionInfo").when(answer).getExceptionInfo(any());
		doReturn(0).when(answer).saveToFile(any(), any(), any(), any());
		doReturn(new FileName(null, false)).when(answer).getFileName(any(), anyInt(), any(), any(), any());
		
		
		assertEquals(expected, answer.getResponse(
			request,
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
	
	public static Object[] dataGetResponse() {
		return new Object[] {
			new Object[] {
				"Sync request, dev ip",
				false, "localhost", 0,
				new TextResponse(StatusCode.OK, new Headers().addHeader("content-type", "text/html"), "DetailedException")
			},
			new Object[] {
				"Sync request, not dev ip",
				false, "42.42.42.42", 1,
				new TextResponse(StatusCode.OK, new Headers().addHeader("content-type", "text/html"), "ExceptionInfo")
			},

			new Object[] {
				"Async request, dev ip",
				true, "localhost", 1,
				new TextResponse(StatusCode.I_AM_A_TEAPORT, new Headers(), "class java.lang.Exception: Some Exception")
			},
			new Object[] {
				"Async request, not dev ip",
				true, "42.42.42.42", 1,
				new TextResponse(StatusCode.I_AM_A_TEAPORT, new Headers(), "I'm a teapot")
			},
		};
	}
	
	@ParameterizedTest
	@MethodSource("dataGetFileNameReturnsCorrectFilename")
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
		assertEquals(expected, answer.getFileName(now, random, action, code, t), "First run");
		
		FileName secondExpected = new FileName(expected.getName(), false);
		assertEquals(secondExpected, answer.getFileName(now.plusHours(1), random*2, action, code, t), "Second run");
	}
	
	public static Object[] dataGetFileNameReturnsCorrectFilename() {
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
	
	@ParameterizedTest
	@MethodSource("dataSaveToFileDoNothingIfFileIsNotCreateAndNotUsed")
	public void testSaveToFileDoNothingIfFileIsNotCreateAndNotUsed(FileName filename) {
		Text text = mock(Text.class);
		
		ExceptionAnswer answer = new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList(),
			null,
			mock(TranslatorExtension.class),
			mock(Logger.class)
		);
		assertEquals(-1, answer.saveToFile(filename, "some text", "charset", text));
		
		verifyNoMoreInteractions(text);
	}
	
	public static Object[] dataSaveToFileDoNothingIfFileIsNotCreateAndNotUsed() {
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
	//	when(templateResponse.createResponse(any())).thenReturn("templateContent");
		
		ExceptionAnswer answer = new ExceptionAnswer(
			mock(Register.class),
			Arrays.asList(),
			null,
			mock(TranslatorExtension.class),
			mock(Logger.class)
		);
		assertEquals(0, answer.saveToFile(new FileName("/path/to/file", true), "some content", "charset", text));
		
		verify(text, times(1)).write(any(), eq("/path/to/file"), eq("charset"), eq(false));
		// verify(templateResponse, times(1)).createResponse(any());
		verifyNoMoreInteractions(text);
	}
}
