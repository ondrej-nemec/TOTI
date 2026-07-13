package toti.application.answers;

import java.util.Optional;

import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import toti.application.ServerException;
import toti.application.answers.request.Request;
import toti.application.answers.response.FinalResponse;
import toti.lib.common.structures.MapDictionary;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;
import toti.lib.tcpip.structures.RequestParameters;

public class FileSystemAnswerTest {
	
	private final String basePath = "test/FileSystemAnswerTest";
	
	 // neporarilo se zvalidovat url - asi pri chybe - tezko nasimulovat - nenexistující soubor?

	@Test
	public void testAnswerFileOutsideBasePath() throws ServerException {
		FileSystemAnswer answer = create("index.txt", true);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest("/../outside-file.txt");
		
		ServerException expected = assertThrows(ServerException.class, ()->{
			answer.answer(request, responseHeaders, "toti-charset");
		});
		assertNotNull(expected);
	}

	@Test
	public void testAnswerNotExistingFileNoBypass() throws ServerException {
		FileSystemAnswer answer = create(null, false);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest("/not-existing");
		
		ServerException expected = assertThrows(ServerException.class, ()->{
			answer.answer(request, responseHeaders, "toti-charset");
		});
		assertNotNull(expected);
	}

	@ParameterizedTest
	@MethodSource("dataAnswerWithDefaultFileThrowing")
	public void testAnswerWithDefaultFileThrowing(String message, String uri) throws ServerException {
		FileSystemAnswer answer = create("index.txt", false);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest(uri);
		
		ServerException expected = assertThrows(ServerException.class, ()->{
			answer.answer(request, responseHeaders, "toti-charset");
		});
		assertNotNull(expected);
	}
	
	public static Object[] dataAnswerWithDefaultFileThrowing() {
		return new Object[] {
			new Object[] {
				"Not existing file", "/some-not-existing-file"
			},
			new Object[] {
				"Subdir without existing default file", "/subdir"
			},
			new Object[] {
				"Not existing file without default file", "/subdir/c.txt"
			}
		};
	}

	@ParameterizedTest
	@MethodSource("dataAnswerWithDefaultFileWorking")
	public void testAnswerWithDefaultFileWorking(String message, String uri) throws ServerException {
		FileSystemAnswer answer = create("index.txt", false);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest(uri);
		
		FinalResponse actual = answer.answer(request, responseHeaders, "toti-charset");
		
		FinalResponse expected = new FinalResponse(StatusCode.OK, mock(Headers.class), "Index content");
		
		assertEquals(expected, actual, message);
		verify(responseHeaders, times(1)).addHeader("Content-Type", "text/plain; charset=toti-charset");
		verify(responseHeaders, times(1)).getHeaders();
		verifyNoMoreInteractions(responseHeaders);
	}
	
	public static Object[] dataAnswerWithDefaultFileWorking() {
		return new Object[] {
			new Object[] {
				"Root dir", "/"
			}
		};
	}

	@Test
	public void testAnswerCorrectResultWithDirectoryAndAllowedDirResponse() throws ServerException {
		FileSystemAnswer answer = create(null, true);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest("/subdir");
		
		FinalResponse actual = answer.answer(request, responseHeaders, "toti-charset");
		
		FinalResponse expected = new FinalResponse(
			StatusCode.OK,
			new Headers().addHeader("Content-Type", "text/plain"), // headers is default text header and is overrided later
			"Folder: <br>"
			+ "<a href='/subdir/..'>..</a><br>"
			+ "<a href='/subdir/a.txt'>a.txt</a><br>"
			+ "<a href='/subdir/b.txt'>b.txt</a><br>"
		);
		
		assertEquals(expected, actual);
		verify(responseHeaders, times(1)).addHeader("Content-Type", "text/html; charset=toti-charset");
		verify(responseHeaders, times(1)).getHeaders();
		verifyNoMoreInteractions(responseHeaders);
	}

	@Test
	public void testAnswerCorrectResultWithFile() throws ServerException {
		FileSystemAnswer answer = create(null, false);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest("/someFile.txt");
		
		FinalResponse actual = answer.answer(request, responseHeaders, "toti-charset");
		
		FinalResponse expected = new FinalResponse(StatusCode.OK, new Headers(), "Some file content");
		
		assertEquals(expected, actual);
		verify(responseHeaders, times(1)).addHeader("Content-Type", "text/plain; charset=toti-charset");
		verify(responseHeaders, times(1)).getHeaders();
		verifyNoMoreInteractions(responseHeaders);
	}
	
	private FileSystemAnswer create(String defaultFile, boolean allowedDir) {
		return new FileSystemAnswer(basePath, allowedDir, defaultFile, mock(Logger.class));
	}
	
	private Request createRequest(String uri) {
		return new Request(
			uri, HttpMethod.GET, new Headers(),
			MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		);
	}
	
}
