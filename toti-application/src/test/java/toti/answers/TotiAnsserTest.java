package toti.answers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapDictionary;
import ji.socketCommunication.http.StatusCode;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.request.Request;
import toti.answers.response.EmptyResponse;
import toti.answers.response.FileResponse;
import toti.answers.response.Response;
import toti.answers.response.TextResponse;
import toti.extensions.OnToti;
import toti.templating.TemplateFactory;

@RunWith(JUnitParamsRunner.class)
public class TotiAnsserTest {
	
	@Test
	@Parameters(method="dataRoutingWithExtension")
	public void testRoutingWithExtension(String url, Response expected) {
		OnToti extension = new OnToti() {
			@Override
			public String getIdentifier() {
				return "testExtension";
			}
			@Override
			public Response getResponse(String uri, Request request, Identity identity, MapDictionary<String> space,
					Headers responseHeaders, boolean isDeveloperRequest) {
				return new TextResponse(StatusCode.ACCEPTED, new Headers(), "extensionResponse");
			}
			@Override
			public List<String> getListeningUri() {
				return Arrays.asList("/ext", "/test");
			}
		};
		
		TotiAnswer answer = new TotiAnswer(
			Arrays.asList("localhost"),
			mock(TemplateFactory.class),
			mock(Translator.class),
			mock(IdentityFactory.class),
			Arrays.asList(extension)
		);
		assertEquals(expected, answer.getResponse(url, mock(Request.class), mock(Identity.class), mock(Headers.class)));
	}

	public Object[] dataRoutingWithExtension() {
		return new Object[] {
			new Object[] {
				"/not-existing", new EmptyResponse(StatusCode.NOT_FOUND, new Headers())
			},
			new Object[] {
				"/ext", new TextResponse(StatusCode.ACCEPTED, new Headers(), "extensionResponse")
			},
			new Object[] {
				"/test", new TextResponse(StatusCode.ACCEPTED, new Headers(), "extensionResponse")
			}
		};
	}
	
	@Test
	@Parameters(method="dataGetResponseWithEmptyAndNotExistingUrl")
	public void testGetResponseWithEmptyAndNotExistingUrl(String ip, String url, Response expected) {
		Identity identity = mock(Identity.class);
		when(identity.getIP()).thenReturn(ip);
		
		TotiAnswer answer = new TotiAnswer(
			Arrays.asList("localhost"),
			mock(TemplateFactory.class),
			mock(Translator.class),
			mock(IdentityFactory.class),
			new LinkedList<>()
		);
		assertEquals(expected, answer.getResponse(url, mock(Request.class), identity, mock(Headers.class)));
	}
	
	public Collection<Object[]> dataGetResponseWithEmptyAndNotExistingUrl() {
		Collection<Object[]> result = new LinkedList<>();
		String[] both = new String[] {
			"", "/index", "/index.html", "/"
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
