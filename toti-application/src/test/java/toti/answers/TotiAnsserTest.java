package toti.answers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.Headers;
import toti.answers.request.Request;
import toti.answers.response.EmptyResponse;
import toti.answers.response.FileResponse;
import toti.answers.response.Response;
import toti.security.Identity;
import toti.templating.TemplateFactory;

@RunWith(JUnitParamsRunner.class)
public class TotiAnsserTest {

	@Test
	@Parameters(method="dataGetResponseWithEmptyAndNotExistingUrl")
	public void testGetResponseWithEmptyAndNotExistingUrl(String ip, String url, Response expected) {
		Identity identity = mock(Identity.class);
		when(identity.getIP()).thenReturn(ip);
		
		TotiAnswer answer = new TotiAnswer(
			Arrays.asList("localhost"),
			mock(TemplateFactory.class),
			mock(Translator.class)
		);
		assertEquals(expected, answer.getResponse(url, mock(Request.class), identity, mock(Headers.class)));
	}
	
	public Collection<Object[]> dataGetResponseWithEmptyAndNotExistingUrl() {
		Collection<Object[]> result = new LinkedList<>();
		String[] both = new String[] {
			"/toti", "/toti/index", "/toti/index.html", "/toti/"
		};
		for (String url : both) {
			// index request, not develop
			result.add(new Object[] {
				"notLocalHost", url, new EmptyResponse(StatusCode.NOT_FOUND, new Headers())	
			});
			// index request, develop
			result.add(new Object[] {
				"localhost", url, new FileResponse(StatusCode.OK, new Headers(), "toti/assets/index.html")
			});
		}
		String[] wrong = new String[] {
			"/toti/x", "/totia"
		};
		for (String url : wrong) {
			result.add(new Object[] {
				"localhost", url, new EmptyResponse(StatusCode.NOT_FOUND, new Headers())
			});
		}
		return result;
	}
	
}
