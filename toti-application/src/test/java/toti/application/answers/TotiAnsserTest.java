package toti.application.answers;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import toti.application.answers.request.Identity;
import toti.application.answers.request.Request;
import toti.application.answers.response.DownloadMode;
import toti.application.answers.response.EmptyResponse;
import toti.application.answers.response.FileResponse;
import toti.application.answers.response.Response;
import toti.application.answers.response.TextResponse;
import toti.application.application.register.Register;
import toti.application.extensions.TemplateFactory;
import toti.application.extensions.TotiExtension;
import toti.application.extensions.TranslatorExtension;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.ObjectBuilder;
import toti.lib.files.env.Env;
import toti.lib.tcpip.enums.StatusCode;
import toti.lib.tcpip.structures.RequestParameters;

public class TotiAnsserTest {
	
	@ParameterizedTest
	@MethodSource("dataRoutingWithExtension")
	public void testRoutingWithExtension(String url, Response expected) {
		TotiExtension extension = new TotiExtension() {
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
			@Override
			public void init(Env appEnv, Register register) {}
			
			@Override public void onRequestStart(
				Identity identity, MapDictionary<String> sessionSpace, Headers requestHeaders,
				MapDictionary<String> queryParams, RequestParameters requestBody) {}
			@Override public void onRequestEnd(
				Identity identity, MapDictionary<String> sessionSpace, Headers responseHeaders) {}
			@Override public void onApplicationStart() throws Exception {}
			@Override public void onApplicationStop() throws Exception {}
		};
		
		TotiAnswer answer = new TotiAnswer(
			ip->ip.equals("localhost"),
			mock(TemplateFactory.class),
			mock(TranslatorExtension.class),
			Arrays.asList(extension)
		);
		assertEquals(expected, answer.getResponse(
			url, mock(Request.class), mock(Identity.class), mock(Headers.class), new ObjectBuilder<>()
		));
	}

	public static Object[] dataRoutingWithExtension() {
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
	
	@ParameterizedTest
	@MethodSource("dataGetResponseWithEmptyAndNotExistingUrl")
	public void testGetResponseWithEmptyAndNotExistingUrl(String ip, String url, Response expected) {
		Identity identity = mock(Identity.class);
		when(identity.getIP()).thenReturn(ip);
		
		TotiAnswer answer = new TotiAnswer(
			incomingIP->incomingIP.equals("localhost"),
			mock(TemplateFactory.class),
			mock(TranslatorExtension.class),
			new LinkedList<>()
		);
		assertEquals(expected, answer.getResponse(
			url, mock(Request.class), identity, mock(Headers.class), new ObjectBuilder<>()
		));
	}
	
	public static Collection<Object[]> dataGetResponseWithEmptyAndNotExistingUrl() {
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
				"localhost", url, new FileResponse(StatusCode.OK, new Headers(), "toti/assets/index.html", DownloadMode.RESPONSE)
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
