package toti.answers;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ji.common.functions.Env;
import ji.common.structures.MapDictionary;
import ji.common.structures.ObjectBuilder;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.request.Request;
import toti.answers.response.DownloadMode;
import toti.answers.response.EmptyResponse;
import toti.answers.response.FileResponse;
import toti.answers.response.Response;
import toti.answers.response.TextResponse;
import toti.application.register.Register;
import toti.extensions.TemplateExtension;
import toti.extensions.TotiExtension;
import toti.extensions.TranslatorExtension;
import toti.http.enums.StatusCode;
import toti.http.structures.RequestParameters;

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
			Arrays.asList("localhost"),
			mock(TemplateExtension.class),
			mock(TranslatorExtension.class),
			mock(IdentityFactory.class),
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
			Arrays.asList("localhost"),
			mock(TemplateExtension.class),
			mock(TranslatorExtension.class),
			mock(IdentityFactory.class),
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
