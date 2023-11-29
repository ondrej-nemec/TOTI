package toti.answers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapDictionary;
import ji.common.structures.ThrowingFunction;
import ji.socketCommunication.http.HttpMethod;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.Protocol;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.testing.TestCase;
import ji.translator.Locale;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.ServerException;
import toti.answers.action.BodyType;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.request.AuthMode;
import toti.answers.request.Identity;
import toti.answers.request.IdentityFactory;
import toti.answers.request.Request;
import toti.answers.request.SessionUserProvider;
import toti.answers.response.EmptyResponse;
import toti.answers.response.RedirectResponse;
import toti.answers.response.Response;
import toti.answers.response.TextResponse;
import toti.answers.router.Link;
import toti.answers.router.Router;
import toti.application.register.MappedAction;
import toti.application.register.Param;

@RunWith(JUnitParamsRunner.class)
public class ControllerAnswerTest implements TestCase {

	@Test
	public void testAnswerNoMappedAction() throws Exception {
		Router router = mock(Router.class);
		// MappedAction.test("routered", "route", "method")
		when(router.getUrlMapping(any())).thenReturn(null);
		
		Translator translator = mock(Translator.class);
		when(translator.withLocale(any(Locale.class))).thenReturn(translator);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		SessionUserProvider sup = mock(SessionUserProvider.class);
		Param root = new Param(null);
		
		ControllerAnswer answer = spy(new ControllerAnswer(
			router, root, new HashMap<>(),
			sup, identityFactory,
			mock(Link.class), translator, mock(Logger.class)
		));
		// doReturn(null).when(answer).getMappedAction(any(), any(), any());
		
		ji.socketCommunication.http.structures.Request r = new ji.socketCommunication.http.structures.Request(HttpMethod.GET, "/a/b/c", Protocol.HTTP_1_1);
		r.setUriParams("/a/b/c", MapDictionary.hashMap());
		
		assertNull(answer.answer(r, mock(Identity.class), new Headers(), Optional.empty(), new Headers(), ""));
		
		verify(answer, times(1)).answer(any(), any(), any(), any(), any(), any());
		verify(answer, times(1)).getMappedAction(root, new LinkedList<>(Arrays.asList(
				"a", "b", "c"
			)),  HttpMethod.GET, new Request(
			HttpMethod.GET, new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		));
		verify(router, times(1)).getUrlMapping("/a/b/c");
		verify(answer, times(1)).getUrlParts("/a/b/c");
		verifyNoMoreInteractions(translator, identityFactory, sup, answer, router);
	}

	@Test
	public void testAnswer() throws Throwable {
		Router router = mock(Router.class);
		when(router.getUrlMapping("/routered-action")).thenReturn("/routered/route/method");
		//.thenReturn(MappedAction.test("routered", "route", "method"));
		
		Translator translator = mock(Translator.class);
		when(translator.withLocale(any(Locale.class))).thenReturn(translator);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		SessionUserProvider sup = mock(SessionUserProvider.class);
		Identity identity = mock(Identity.class);
		when(identity.getLocale()).thenReturn(mock(Locale.class));
		
		MappedAction mappedAction = MappedAction.test("a", "b", "c");
		
		Param root = new Param(null);
		ControllerAnswer answer = spy(new ControllerAnswer(
			router, root, new HashMap<>(),
			sup, identityFactory,
			mock(Link.class), translator, mock(Logger.class)
		));
		ji.socketCommunication.http.structures.Response rs = mock(ji.socketCommunication.http.structures.Response.class);
		Response response = mock(Response.class);
		when(response.getResponse(any(), any(), any(), any(), any())).thenReturn(rs);
		doReturn(mappedAction).when(answer).getMappedAction(any(), any(), any(), any());
		doReturn(response).when(answer).run(any(), any(), any(), any());

		Headers responseHeaders = new Headers();
		
		ji.socketCommunication.http.structures.Request r = new ji.socketCommunication.http.structures.Request(HttpMethod.GET, "/a/b/c", Protocol.HTTP_1_1);
		r.setUriParams("/a/b/c", MapDictionary.hashMap());
		
		assertEquals(rs, answer.answer(r, identity, new Headers(), Optional.empty(), responseHeaders, ""));

		Request request = new Request(
			HttpMethod.GET, new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		);

		verify(answer, times(1)).answer(any(), any(), any(), any(), any(), any());
		verify(answer, times(1)).getMappedAction(root, new LinkedList<>(
			Arrays.asList("a", "b", "c")
		), HttpMethod.GET, request);
			// .getMappedAction("/a/b/c", HttpMethod.GET, request);
		verify(answer, times(1)).run("/a/b/c", mappedAction, request, identity);
		verify(identityFactory, times(1)).finalizeIdentity(identity, responseHeaders);
		verify(translator, times(1)).withLocale(any(Locale.class));
		verify(router, times(1)).getUrlMapping("/a/b/c");
		verify(answer, times(1)).getUrlParts("/a/b/c");
		verifyNoMoreInteractions(translator, identityFactory, sup, answer, router);
	}
	
	@Test
	@Parameters(method="dataGetUrlParts")
	public void testGetUrlParts(String url, List<String> expected) {
		ControllerAnswer answer = new ControllerAnswer(null, null, null, null, null, null, null, null);
		assertEquals(expected, answer.getUrlParts(url));
	}
	
	public Object[] dataGetUrlParts() {
		return new Object[] {
			new Object[] { "", Arrays.asList() },
			new Object[] { "/", Arrays.asList() },
			new Object[] { "/a", Arrays.asList("a") },
			new Object[] { "/a/b/c", Arrays.asList("a", "b", "c") },
			new Object[] { "/a/a", Arrays.asList("a", "a") }
		};
	}

	@Test
	@Parameters(method="dataGetMappedAction")
	public void testGetMappedAction(String url, HttpMethod method, Param root, MappedAction expected, List<Object> params) {
		Router router = mock(Router.class);
	//	when(router.getUrlMapping("/routered")).thenReturn("/routered-method");
		
		Translator translator = mock(Translator.class);
		when(translator.withLocale(any(Locale.class))).thenReturn(translator);
		
		ControllerAnswer answer = new ControllerAnswer(
			router, root, new HashMap<>(),
			mock(SessionUserProvider.class), mock(IdentityFactory.class),
			mock(Link.class), translator, mock(Logger.class)
		);
		Request request = new Request(HttpMethod.GET, new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty());

		LinkedList<String> urls = new LinkedList<>();
		if (url.length() > 0) {
			urls.addAll(Arrays.asList(url.substring(1).split("/")));
		}
		
		MappedAction actual = answer.getMappedAction(root, urls, method, request);
		if (expected == null) {
			assertNull(actual);
		} else {
			assertTrue(
				String.format(
					"Expected: %s, Actual: %s",
					expected.simpleString(),
					actual == null ? "NULL" : actual.simpleString()
				),
				expected.assertForTest(actual)
			);
		}
		assertEquals(params, request.getPathParams().toList());
	//	verify(router, times(1)).getUrlMapping(url);
		verifyNoMoreInteractions(router);
	}

	public Object[] dataGetMappedAction() {
		return new Object[] {
			// no mapping
			new Object[] {
				"", HttpMethod.GET, new Param(null), null, Arrays.asList()
			},
			// root
			new Object[] {
				"", HttpMethod.GET, param((r)->{
					r.addAction(HttpMethod.GET, MappedAction.test("ro", "ot", "GET"));
				}),
				MappedAction.test("ro", "ot", "GET"),
				Arrays.asList()
			},
			// this case should never happend
			/*new Object[] {
				"/", HttpMethod.GET, param((r)->{
					r.addAction(HttpMethod.GET, MappedAction.test("ro", "ot", "GET"));
				}),
				MappedAction.test("ro", "ot", "GET"),
				Arrays.asList()
			},*/
			// routered
			/*new Object[] {
				"/routered", HttpMethod.GET, new Param(null),
				MappedAction.test("routered", "route", "method"),
				Arrays.asList()
			},*/
			// no route
			new Object[] {
				"/b", HttpMethod.GET, param((r)->{
					Param a = r.addChild("a");
					a.addAction(HttpMethod.GET, MappedAction.test("a", "a", "a"));
				}),
				null,
				Arrays.asList("b")
			},
			// missing action
			new Object[] {
				"/b", HttpMethod.GET, param((r)->{
					r.addChild("b");
				}),
				null,
				Arrays.asList()
			},
			// wrong method
			new Object[] {
				"/a", HttpMethod.POST, param((r)->{
					r.addChild("a").addAction(HttpMethod.GET, MappedAction.test("root", "a", "GET"));
				}),
				null,
				Arrays.asList()
			},
			// ---------------
			new Object[] {
				"/module/controller", HttpMethod.GET, mapping(),
				MappedAction.test("module", "list", "GET"),
				Arrays.asList()
			},
			new Object[] {
				// same as previous with / at the end
				"/module/controller/", HttpMethod.GET, mapping(),
				MappedAction.test("module", "list", "GET"),
				Arrays.asList()
			},
			new Object[] {
				"/module/controller/12", HttpMethod.GET, mapping(),
				MappedAction.test("module", "list", "GET"),
				Arrays.asList("12")
			},
			new Object[] {
				"/module/controller/method2", HttpMethod.POST, mapping(),
				MappedAction.test("module", "method2", "POST"),
				Arrays.asList()
			},
			new Object[] {
				"/module/controller/method2", HttpMethod.DELETE, mapping(),
				MappedAction.test("module", "method2", "DELETE"),
				Arrays.asList()
			},
			new Object[] {
				"/module/controller/method2/42", HttpMethod.DELETE, mapping(),
				MappedAction.test("module", "method2-param", "DELETE"),
				Arrays.asList("42")
			},
			new Object[] {
				"/extra/controller/generate/42", HttpMethod.GET, mapping(),
				null,
				Arrays.asList("controller", "generate", "42")
			},
			new Object[] {
				"/extra/generate/42", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "generate", "GET"),
				Arrays.asList("42")
			},
			new Object[] {
				"/extra/something/string", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "string", "GET"),
				Arrays.asList("something")
			},
			new Object[] {
				"/extra/something/string/other", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "string", "GET"),
				Arrays.asList("something", "other")
			},
			new Object[] {
				"/extra/string/42", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "42", "GET"),
				Arrays.asList("string")
			},
			new Object[] {
				"/extra/some/42", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "42", "GET"),
				Arrays.asList("some")
			},
			new Object[] {
				"/extra/some/42/25", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "42", "GET"),
				Arrays.asList("some", "25")
			},
			new Object[] {
				"/extra/12/42/15", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "42", "GET"),
				Arrays.asList("12", "15")
			},
			new Object[] {
				"/extra/12/32/15", HttpMethod.POST, mapping(),
				MappedAction.test("extra", "x", "GET"),
				Arrays.asList("12", "32", "15")
			},
			new Object[] {
				"/extra/12/32/15", HttpMethod.DELETE, mapping(),
				null,
				Arrays.asList("12", "32", "15")
			},
			new Object[] {
				"/extra/index", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "index", "GET"),
				Arrays.asList()
			},
			new Object[] {
				"/extra/index/45", HttpMethod.GET, mapping(),
				MappedAction.test("extra", "indexParam", "GET"),
				Arrays.asList("45")
			},
		};
	}
	
	private Param mapping() {
		Param root = new Param(null);
		root.addAction(HttpMethod.GET, MappedAction.test("ro", "ot", "GET"));
		root.addAction(HttpMethod.POST, MappedAction.test("ro", "ot", "POST"));
		
		Param module = root.addChild("module");
		Param controller = module.addChild("controller");
		controller.addAction(HttpMethod.GET, MappedAction.test("module", "list", "GET"));
		
		Param method1 = controller.addChild("method1");
		method1.addAction(HttpMethod.POST, MappedAction.test("module", "method1", "POST"));
		method1.addAction(HttpMethod.DELETE, MappedAction.test("module", "method1", "DELETE"));
		Param method2 = controller.addChild("method2");
		method2.addAction(HttpMethod.POST, MappedAction.test("module", "method2", "POST"));
		method2.addAction(HttpMethod.DELETE, MappedAction.test("module", "method2", "DELETE"));
		Param deleteChild = method2.addChild(null);
        deleteChild.addAction(HttpMethod.DELETE, MappedAction.test("module", "method2-param", "DELETE"));
		
		Param extra = root.addChild("extra");
		Param generate = extra.addChild("generate");
		generate.addAction(HttpMethod.GET, MappedAction.test("extra", "generate", "GET"));
		
		Param param = extra.addChild(null);
		param.addAction(HttpMethod.POST, MappedAction.test("extra", "x", "GET"));
		
		Param param1 = param.addChild("string");
		param1.addAction(HttpMethod.GET, MappedAction.test("extra", "string", "GET"));
		
		Param param2 = param.addChild("int");
		param2.addAction(HttpMethod.GET, MappedAction.test("extra", "int", "GET"));
		
		Param param3 = param.addChild("42");
		param3.addAction(HttpMethod.GET, MappedAction.test("extra", "42", "GET"));
		
		Param index = extra.addChild("index");
		index.addAction(HttpMethod.GET, MappedAction.test("extra", "index", "GET"));
		
		Param indexParam = index.addChild(null);
		indexParam.addAction(HttpMethod.GET, MappedAction.test("extra", "indexParam", "GET"));
		indexParam.addAction(HttpMethod.POST, MappedAction.test("extra", "indexParam", "POST"));
		
		return root;
	}
	
	private Param param(Consumer<Param> create) {
		Param root = new Param(null);
		create.accept(root);
		return root;
	}
	
	
	// TODO checkSecured throws serverException
	
	@Test
	@Parameters(method="dataRun")
	public void testRun(
			String uri, List<Object> pathParams, AuthMode authMode, String redirect,
			Object controller, ThrowingFunction<Object, Method, Exception> getMethod,
			Response expected) throws Throwable {
		Translator translator = mock(Translator.class);
		when(translator.withLocale(any(Locale.class))).thenReturn(translator);
		Identity identity = mock(Identity.class);
		when(identity.getLoginMode()).thenReturn(AuthMode.HEADER);
		
		MappedAction action = new MappedAction(
			null, null, null,
			getMethod.apply(controller), ()->controller, authMode,
			null
		);
		
		Router router = mock(Router.class);
		when(router.getRedirectOnNotLoggedInUser()).thenReturn(redirect);
		
		ControllerAnswer answer = new ControllerAnswer(
			router, mock(Param.class), new HashMap<>(),
			mock(SessionUserProvider.class), mock(IdentityFactory.class),
			mock(Link.class), translator, mock(Logger.class)
		);
		Request request = new Request(HttpMethod.GET, new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty());
		request.getPathParams().addAll(pathParams);
		
		Response actual = answer.run(uri, action, request, identity);
		assertEquals(expected, actual);
	}
	
	public Object[] dataRun() {
		return new Object[] {
			// interruption in prevalidate
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.prevalidate((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, translator, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize - authMode is header, redirect is null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.HEADER, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, translator, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize - authMode is header, redirect is not null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.HEADER, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, translator, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize - authMode is not header, redirect is null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, translator, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize - authMode is not header, redirect is not null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, translator, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, new Headers(), "/redirect?backlink=%2Furi", false)
			},
			// interrupted in authorize - authMode is not header, redirect is not null, url is root
			new Object[] {
				"/", Arrays.asList(), AuthMode.COOKIE, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, translator, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, new Headers(), "/redirect", false)
			},
			// interrupted in validate
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.validate((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, translator, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in create
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.createResponse((request, translator, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// create return response
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.createResponse((request, translator, identity)->{
								return new TextResponse(StatusCode.OK, new Headers(), "response");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "response")
			},
			// create with params
			new Object[] {
				"/uri", Arrays.asList(10, "aaa"), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
							.createResponse((request, translator, identity)->{
								return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class)),
				new TextResponse(StatusCode.OK, new Headers(), "Response 10: aaa")
			},
			// create with params - cast
			new Object[] {
				"/uri", Arrays.asList("10", "aaa"), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
							.createResponse((request, translator, identity)->{
								return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class)),
				new TextResponse(StatusCode.OK, new Headers(), "Response 10: aaa")
			},
			// create with params - wrong type
			new Object[] {
				"/uri", Arrays.asList("not a number", "aaa"), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
							.createResponse((request, translator, identity)->{
								return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class)),
				new EmptyResponse(StatusCode.BAD_REQUEST, new Headers())
			},
			// create with params - less that expected
			new Object[] {
				"/uri", Arrays.asList(10), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
							.createResponse((request, translator, identity)->{
								return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class)),
				new EmptyResponse(StatusCode.BAD_REQUEST, new Headers())
			},
			// create with params - more than expected
			new Object[] {
				"/uri", Arrays.asList(10, "aaa", "bb"), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
							.createResponse((request, translator, identity)->{
								return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class)),
				new EmptyResponse(StatusCode.BAD_REQUEST, new Headers())
			}
		};
	}
/*
	@Test
	@Parameters(method="dataRunCreateParametersNotMatch")
	public void testRunCreateParametersNotMatch(
			List<Object> pathParams,
			Object controller, ThrowingFunction<Object, Method, Exception> getMethod) throws Throwable {
		Translator translator = mock(Translator.class);
		when(translator.withLocale(any(Locale.class))).thenReturn(translator);
		Identity identity = mock(Identity.class);
		when(identity.getLoginMode()).thenReturn(AuthMode.HEADER);
		
		MappedAction action = new MappedAction(
			null, null, null,
			getMethod.apply(controller), ()->controller, AuthMode.HEADER,
			null
		);
		
		Router router = mock(Router.class);
		// when(router.getRedirectOnNotLoggedInUser()).thenReturn(null);
		
		ControllerAnswer answer = new ControllerAnswer(
			router, mock(Param.class), new HashMap<>(),
			mock(SessionUserProvider.class), mock(IdentityFactory.class),
			mock(Link.class), translator, mock(Logger.class)
		);
		Request request = new Request(
			HttpMethod.GET, new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		);
		request.getPathParams().addAll(pathParams);
		
		try {
			 answer.run("/uri", action, request, identity);
			 fail("Expected exception");
		} catch (IllegalArgumentException e) {
			// expected
		} catch (Exception e) {
			fail("Unexpected exception " + e.getMessage());
		}
	}
	
	public Object[] dataRunCreateParametersNotMatch() {
		return new Object[] {
			// wrong class
			new Object[] {
				Arrays.asList("aaa"), new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id) {
						return ResponseBuilder.get()
						.createResponse((request, translator, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id);
						});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class))
			},
			// request contains more parameters
			new Object[] {
				Arrays.asList("aaa", 10), new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id) {
						return ResponseBuilder.get()
						.createResponse((request, translator, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id);
						});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class))
			},
			// request contains less parameters
			new Object[] {
				Arrays.asList(10), new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
						.createResponse((request, translator, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
						});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class))
			},
			// request contains another method with same name
			new Object[] {
				Arrays.asList(10), new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
						.createResponse((request, translator, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
						});
					}
					@SuppressWarnings("unused")
					public ResponseAction index(int id) {
						return ResponseBuilder.get()
						.createResponse((request, translator, identity)->{
							fail();
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id);
						});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class))
			}
		};
	}
*/
	@Test
	@Parameters(method="dataCheckSecured")
	public void testCheckSecured(Supplier<MappedAction> mapped, Supplier<Identity> identity, StatusCode expectedCode) {
		ControllerAnswer answer = new ControllerAnswer(
			mock(Router.class), mock(Param.class), new HashMap<>(),
			mock(SessionUserProvider.class), mock(IdentityFactory.class),
			mock(Link.class), mock(Translator.class), mock(Logger.class)
		);
		try {
			answer.checkSecured(mapped.get(), identity.get());
			if (expectedCode != null) {
				fail("Method checkSecured not throws exception");
			}
		} catch (ServerException e) {
			assertEquals(expectedCode, e.getStatusCode());
		}
	}
	
	public Object[] dataCheckSecured() {
		return new Object[] {
			// secured annotation - not secured - nothing
			new Object[] {
				supplier(()->{
					MappedAction mapped = mock(MappedAction.class);
					when(mapped.isSecured()).thenReturn(false);
					return mapped;
				}),
				supplier(()->{
					return null;
				}),
				null // status code
			},
			// secured annotation - is secured - identity is anonymous 401
			new Object[] {
				supplier(()->{
					MappedAction mapped = mock(MappedAction.class);
					when(mapped.isSecured()).thenReturn(true);
					return mapped;
				}),
				supplier(()->{
					Identity identity = mock(Identity.class);
					when(identity.isAnonymous()).thenReturn(true);
					return identity;
				}),
				StatusCode.UNAUTHORIZED
			},
			// 
			new Object[] {
				supplier(()->{
					MappedAction mapped = mock(MappedAction.class);
					when(mapped.isSecured()).thenReturn(true);
					return mapped;
				}),
				supplier(()->{
					Identity identity = mock(Identity.class);
					when(identity.isAnonymous()).thenReturn(false);
					return identity;
				}),
				null // status code
			},
			authModeCombination(AuthMode.HEADER, AuthMode.HEADER, null),
			authModeCombination(AuthMode.HEADER, AuthMode.COOKIE, StatusCode.FORBIDDEN),
			authModeCombination(AuthMode.HEADER, AuthMode.COOKIE_AND_CSRF, StatusCode.FORBIDDEN),
			authModeCombination(AuthMode.HEADER, AuthMode.NO_TOKEN, StatusCode.FORBIDDEN),
			authModeCombination(AuthMode.COOKIE_AND_CSRF, AuthMode.HEADER, null),
			authModeCombination(AuthMode.COOKIE_AND_CSRF, AuthMode.COOKIE_AND_CSRF, null),
			authModeCombination(AuthMode.COOKIE_AND_CSRF, AuthMode.COOKIE, StatusCode.FORBIDDEN),
			authModeCombination(AuthMode.COOKIE_AND_CSRF, AuthMode.NO_TOKEN, StatusCode.FORBIDDEN),
			authModeCombination(AuthMode.COOKIE, AuthMode.HEADER, null),
			authModeCombination(AuthMode.COOKIE, AuthMode.COOKIE_AND_CSRF, null),
			authModeCombination(AuthMode.COOKIE, AuthMode.COOKIE, null),
			authModeCombination(AuthMode.COOKIE, AuthMode.NO_TOKEN, StatusCode.FORBIDDEN),
			authModeCombination(AuthMode.NO_TOKEN, AuthMode.HEADER, null),
			authModeCombination(AuthMode.NO_TOKEN, AuthMode.COOKIE_AND_CSRF, null),
			authModeCombination(AuthMode.NO_TOKEN, AuthMode.COOKIE, null),
			authModeCombination(AuthMode.NO_TOKEN, AuthMode.NO_TOKEN, null)
		};
	}
	
	private Object[] authModeCombination(AuthMode m, AuthMode i, StatusCode code) {
		return new Object[] {
			supplier(()->{
				MappedAction mapped = mock(MappedAction.class);
				when(mapped.isSecured()).thenReturn(true);
				when(mapped.getSecurityMode()).thenReturn(m);
				return mapped;
			}),
			supplier(()->{
				Identity identity = mock(Identity.class);
				when(identity.isAnonymous()).thenReturn(false);
				when(identity.getLoginMode()).thenReturn(i);
				return identity;
			}),
			code
		};
	}

	@Test(expected = ServerException.class)
	public void testParseBodyThrowsWithNotSupportedTypes() throws ServerException {
		ControllerAnswer answer = new ControllerAnswer(
			mock(Router.class), mock(Param.class), new HashMap<>(),
			mock(SessionUserProvider.class), mock(IdentityFactory.class),
			mock(Link.class), mock(Translator.class), mock(Logger.class)
		);
		Request request = new Request(
			HttpMethod.GET, 
			new Headers(), 
			MapDictionary.hashMap(),
			new RequestParameters().put("some", "parameter"),
			null, // body
			Optional.empty()
		);
		answer.parseBody(request, Arrays.asList(), mock(MappedAction.class));
	}
	
	@Test
	@Parameters(method="dataParseBody")
	public void testParseBody(Request request, List<BodyType> allowedTypes, Consumer<Request> check) throws ServerException {
		ControllerAnswer answer = new ControllerAnswer(
			mock(Router.class), mock(Param.class), new HashMap<>(),
			mock(SessionUserProvider.class), mock(IdentityFactory.class),
			mock(Link.class), mock(Translator.class), mock(Logger.class)
		);
		answer.parseBody(request, allowedTypes, mock(MappedAction.class));
		check.accept(request);
	}
	
	public Object[] dataParseBody() {
		return new Object[] {
			// request contains body in map - nothing change
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers(), 
					MapDictionary.hashMap(),
					new RequestParameters().put("some", "parameter"),
					null, // body
					Optional.empty()
				),
				Arrays.asList(BodyType.values()),
				consumer(Request.class, (request)->{
					assertEquals(
						new RequestParameters().put("some", "parameter"),
						request.getBodyParams()
					);
				})
			},
			// request contains body with json - allowed - add
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers().addHeader("content-type", "application/json"), 
					MapDictionary.hashMap(),
					new RequestParameters(),
					getJson(), // body
					Optional.empty()
				),
				Arrays.asList(BodyType.values()),
				consumer(Request.class, (request)->{
					assertEquals(
						new RequestParameters().put("some", "parameter"),
						request.getBodyParams()
					);
				})
			},
			// request contains body with json - not allowed - nothing
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers().addHeader("content-type", "application/json"), 
					MapDictionary.hashMap(),
					new RequestParameters(),
					getJson(), // body
					Optional.empty()
				),
				Arrays.asList(),
				consumer(Request.class, (request)->{
					assertEquals(new RequestParameters(), request.getBodyParams());
				})
			},
			// request contains body with json - without header - nothing
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers(), 
					MapDictionary.hashMap(),
					new RequestParameters(),
					getJson(), // body
					Optional.empty()
				),
				Arrays.asList(BodyType.values()),
				consumer(Request.class, (request)->{
					assertEquals(new RequestParameters(), request.getBodyParams());
				})
			},
			// request contains body with json - but list - nothing
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers().addHeader("content-type", "application/json"), 
					MapDictionary.hashMap(),
					new RequestParameters(),
					"[]".getBytes(), // body
					Optional.empty()
				),
				Arrays.asList(BodyType.values()),
				consumer(Request.class, (request)->{
					assertEquals(new RequestParameters(), request.getBodyParams());
				})
			},
			// request contains body with xml - allowed - add
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers().addHeader("content-type", "application/xml"), 
					MapDictionary.hashMap(),
					new RequestParameters(),
					getXml(), // body
					Optional.empty()
				),
				Arrays.asList(BodyType.values()),
				consumer(Request.class, (request)->{
					assertEquals(
						new RequestParameters().put("some", "parameter"),
						request.getBodyParams()
					);
				})
			},
			// request contains body with xml - not allowed - nothing
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers().addHeader("content-type", "application/xml"), 
					MapDictionary.hashMap(),
					new RequestParameters(),
					getXml(), // body
					Optional.empty()
				),
				Arrays.asList(),
				consumer(Request.class, (request)->{
					assertEquals(new RequestParameters(), request.getBodyParams());
				})
			},
			// request contains body with xml - without header - nothing
			new Object[] {
				new Request(
					HttpMethod.GET,
					new Headers(), 
					MapDictionary.hashMap(),
					new RequestParameters(),
					getXml(), // body
					Optional.empty()
				),
				Arrays.asList(BodyType.values()),
				consumer(Request.class, (request)->{
					assertEquals(new RequestParameters(), request.getBodyParams());
				})
			}
		};
	}

	private byte[] getJson() {
		return "{\"some\": \"parameter\"}".getBytes();
	}

	private byte[] getXml() {
		return "<root><some>parameter</some></root>".getBytes();
	}
/*
	private Consumer<Request> consumer(Consumer<Request> consumer) {
		return consumer;
	}

	private <T> Supplier<T> supplier(Supplier<T> supplier) {
		return supplier;
	}

	private Object function(Function<Object, Method> function) {
		return function;
	}
	*/
}
