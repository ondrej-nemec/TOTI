package toti.core.answers;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import toti.core.answers.action.ResponseAction;
import toti.core.answers.request.Request;
import toti.core.answers.response.Response;
import toti.core.answers.response.TextResponse;
import toti.core.answers.session.Identity;
import toti.core.application.register.Register;
import toti.core.extensions.TemplateFactory;
import toti.core.extensions.TotiExtension;
import toti.core.logging.Page;
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
			public ResponseAction get(String uri, boolean isDeveloperRequest) {
				return (request, identity)->{
					return new TextResponse(StatusCode.ACCEPTED, new Headers(), "extensionResponse");
				};
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
			ip->ip != null && ip.equals("localhost"),
			mock(TemplateFactory.class),
			Arrays.asList(extension)
		);
		assertEquals(expected, answer.getResponse(
			url, mock(Request.class), mock(Identity.class), mock(Headers.class), new ObjectBuilder<>()
		));
	}

	public static Object[] dataRoutingWithExtension() {
		return new Object[] {
			new Object[] {
				"/not-existing", new TextResponse(StatusCode.NOT_FOUND, new Headers(), "Not found")
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
				"notLocalHost", url, new TextResponse(StatusCode.NOT_FOUND, new Headers(), "Not found")
			});
			// index request, develop
			result.add(new Object[] {
				"localhost", url, new TextResponse(StatusCode.OK, new Headers().addHeader("content-type", "text/html"), Page.primary(
					"Welcome",
					b->{
						b.addH1("Welcome");
						b.addH2("Hello and welcome in TOTI framework");
						b.addParagraph("Your application is running successfully");
					}
				).create())
			});
		}
		String[] wrong = new String[] {
			"/toti/x", "/totia"
		};
		for (String url : wrong) {
			result.add(new Object[] {
				"localhost", url, new TextResponse(StatusCode.NOT_FOUND, new Headers(), "Not found")
			});
		}
		return result;
	}
	
}
