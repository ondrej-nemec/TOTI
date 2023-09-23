package toti.answer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.Request;
import ji.socketCommunication.http.structures.Response;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.Headers;
import toti.ServerException;
import toti.answers.FileSystemAnswer;

@RunWith(JUnitParamsRunner.class)
public class FileSystemAnswerTest {
	
	private final String basePath = "test/FileSystemAnswerTest";
	
	 // neporarilo se zvalidovat url - asi pri chybe - tezko nasimulovat - nenexistující soubor?

	@Test(expected =  ServerException.class)
	public void testAnswerFileOutsideBasePath() throws ServerException {
		FileSystemAnswer answer = create("index.txt", true);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest("/../outside-file.txt");
		
		answer.answer(request, responseHeaders, "toti-charset");
	}

	@Test(expected =  ServerException.class)
	public void testAnswerNotExistingFileNoBypass() throws ServerException {
		FileSystemAnswer answer = create(null, false);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest("/not-existing");
		
		answer.answer(request, responseHeaders, "toti-charset");
	}

	@Test(expected =  ServerException.class)
	@Parameters(method="dataAnswerWithDefaultFileThrowing")
	public void testAnswerWithDefaultFileThrowing(String message, String uri) throws ServerException {
		FileSystemAnswer answer = create("index.txt", false);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest(uri);
		
		answer.answer(request, responseHeaders, "toti-charset");
	}
	
	public Object[] dataAnswerWithDefaultFileThrowing() {
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

	@Test
	@Parameters(method="dataAnswerWithDefaultFileWorking")
	public void testAnswerWithDefaultFileWorking(String message, String uri) throws ServerException {
		FileSystemAnswer answer = create("index.txt", false);
		
		Headers responseHeaders = mock(Headers.class);
		Request request = createRequest(uri);
		
		Response actual = answer.answer(request, responseHeaders, "toti-charset");
		
		Response expected = new Response(StatusCode.OK, Protocol.HTTP_2);
		expected.setBody("Index content".getBytes());
		
		assertEquals(message, expected, actual);
		verify(responseHeaders, times(1)).addHeader("Content-Type", "text/plain; charset=toti-charset");
		verify(responseHeaders, times(1)).getHeaders();
		verifyNoMoreInteractions(responseHeaders);
	}
	
	public Object[] dataAnswerWithDefaultFileWorking() {
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
		
		Response actual = answer.answer(request, responseHeaders, "toti-charset");
		
		Response expected = new Response(StatusCode.OK, Protocol.HTTP_2);
		expected.addHeader("Content-Type", "text/plain"); // headers is default text header and is overrided later
		expected.setBody((
			"Folder: <br>"
			+ "<a href='/subdir/..'>..</a><br>"
			+ "<a href='/subdir/a.txt'>a.txt</a><br>"
			+ "<a href='/subdir/b.txt'>b.txt</a><br>"
		).getBytes());
		
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
		
		Response actual = answer.answer(request, responseHeaders, "toti-charset");
		
		Response expected = new Response(StatusCode.OK, Protocol.HTTP_2);
		expected.setBody("Some file content".getBytes());
		
		assertEquals(expected, actual);
		verify(responseHeaders, times(1)).addHeader("Content-Type", "text/plain; charset=toti-charset");
		verify(responseHeaders, times(1)).getHeaders();
		verifyNoMoreInteractions(responseHeaders);
	}
	
	private FileSystemAnswer create(String defaultFile, boolean allowedDir) {
		return new FileSystemAnswer(basePath, allowedDir, defaultFile, mock(Logger.class));
	}
	
	private Request createRequest(String uri) {
		Request request = new Request(HttpMethod.GET, uri, Protocol.HTTP_2);
		request.setUriParams(uri, MapDictionary.hashMap());
		return request;
	}
	
}
