package toti.core.answers;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.logging.log4j.Logger;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import toti.core.answers.action.ResponseAction;
import toti.core.answers.request.Identity;
import toti.core.answers.request.IdentityFactory;
import toti.core.answers.request.Request;
import toti.core.answers.response.EmptyResponse;
import toti.core.answers.response.FinalResponse;
import toti.core.answers.response.Response;
import toti.core.answers.response.TextResponse;
import toti.core.answers.router.Link;
import toti.core.answers.router.Router;
import toti.core.application.register.MappedAction;
import toti.core.application.register.Param;
import toti.core.extensions.TemplateFactory;
import toti.lib.common.structures.MapDictionary;
import toti.lib.common.structures.ThrowingFunction;
import static toti.lib.common.tests.TestCase.assertEquals;
import static toti.lib.common.tests.TestCase.throwingFunction;
import toti.lib.tcpip.enums.HttpMethod;
import toti.lib.tcpip.enums.StatusCode;
import toti.lib.tcpip.structures.RequestParameters;

public class ControllerAnswerTest {

	@Test
	public void testAnswerNoMappedAction() throws Exception {
		Router router = mock(Router.class);
		// MappedAction.test("routered", "route", "method")
		when(router.getUrlMapping(any())).thenReturn(null);
		
		IdentityFactory identityFactory = mock(IdentityFactory.class);
		Param root = new Param(null);
		
		ControllerAnswer answer = spy(new ControllerAnswer(
			router, root, mock(TemplateFactory.class),
			mock(Link.class), mock(Logger.class)
		));
		// doReturn(null).when(answer).getMappedAction(any(), any(), any());
		
		Request r =  new Request(
			"/a/b/c", HttpMethod.GET, new Headers(),
			MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		);
		
		assertNull(answer.answer(r, mock(Identity.class), new Headers(), ""));
		
		verify(answer, times(1)).answer(any(), any(), any(), any());
		verify(answer, times(1)).getMappedAction(root, new LinkedList<>(Arrays.asList(
				"a", "b", "c"
			)), HttpMethod.GET, new Request(
				"/a/b/c", HttpMethod.GET, new Headers(),
				MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
			)
		);
		verify(router, times(1)).getUrlMapping("/a/b/c");
		verify(answer, times(1)).getUrlParts("/a/b/c");
		verifyNoMoreInteractions(identityFactory, answer, router);
	}

	@Test
	public void testAnswer() throws Throwable {
		Router router = mock(Router.class);
		when(router.getUrlMapping("/routered-action")).thenReturn("/routered/route/method");
		//.thenReturn(MappedAction.test("routered", "route", "method"));
		Identity identity = mock(Identity.class);
		
		MappedAction mappedAction = MappedAction.test("a", "b", "c");
		
		Param root = new Param(null);
		ControllerAnswer answer = spy(new ControllerAnswer(
			router, root, mock(TemplateFactory.class),
			mock(Link.class), mock(Logger.class)
		));
		FinalResponse finalResponse = mock(FinalResponse.class);
		Response response = mock(Response.class);
		when(response.prepare(any())).thenReturn(finalResponse);
		doReturn(mappedAction).when(answer).getMappedAction(any(), any(), any(), any());
		doReturn(response).when(answer).run(any(), any(), any(), any());

		Headers responseHeaders = new Headers();
		
		Request request = new Request(
			"/a/b/c", HttpMethod.GET, new Headers(),
			MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		);
		
		assertEquals(finalResponse, answer.answer(request, identity, responseHeaders, ""));

		Request expectedRequest = new Request(
			"/a/b/c", HttpMethod.GET, new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty()
		);

		verify(answer, times(1)).answer(any(), any(), any(), any());
		verify(answer, times(1)).getMappedAction(root, new LinkedList<>(
			Arrays.asList("a", "b", "c")
		), HttpMethod.GET, expectedRequest);
			// .getMappedAction("/a/b/c", HttpMethod.GET, request);
		verify(answer, times(1)).run("/a/b/c", mappedAction, expectedRequest, identity);
		verify(router, times(1)).getUrlMapping("/a/b/c");
		verify(answer, times(1)).getUrlParts("/a/b/c");
		verifyNoMoreInteractions(answer, router);
	}
	
	@ParameterizedTest
	@MethodSource("dataGetUrlParts")
	public void testGetUrlParts(String url, List<String> expected) {
		ControllerAnswer answer = new ControllerAnswer(null, null, null, null, null);
		assertEquals(expected, answer.getUrlParts(url));
	}
	
	public static Object[] dataGetUrlParts() {
		return new Object[] {
			new Object[] { "", Arrays.asList() },
			new Object[] { "/", Arrays.asList() },
			new Object[] { "/a", Arrays.asList("a") },
			new Object[] { "/a/b/c", Arrays.asList("a", "b", "c") },
			new Object[] { "/a/a", Arrays.asList("a", "a") }
		};
	}

	@ParameterizedTest
	@MethodSource("dataGetMappedAction")
	public void testGetMappedAction(String url, HttpMethod method, Param root, MappedAction expected, List<Object> params) {
		Router router = mock(Router.class);
	//	when(router.getUrlMapping("/routered")).thenReturn("/routered-method");
		
		ControllerAnswer answer = new ControllerAnswer(
			router, root, mock(TemplateFactory.class),
			mock(Link.class), mock(Logger.class)
		);
		Request request = new Request(
			"", HttpMethod.GET, new Headers(), MapDictionary.hashMap(),
			new RequestParameters(), null, Optional.empty()
		);

		LinkedList<String> urls = new LinkedList<>();
		if (url.length() > 0) {
			urls.addAll(Arrays.asList(url.substring(1).split("/")));
		}
		
		MappedAction actual = answer.getMappedAction(root, urls, method, request);
		if (expected == null) {
			assertNull(actual);
		} else {
			assertTrue(
				expected.assertForTest(actual),
				String.format(
					"Expected: %s, Actual: %s",
					expected.simpleString(),
					actual == null ? "NULL" : actual.simpleString()
				)
			);
		}
		assertEquals(params, request.getPathParams().toList());
	//	verify(router, times(1)).getUrlMapping(url);
		verifyNoMoreInteractions(router);
	}

	public static Object[] dataGetMappedAction() {
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
	
	private static Param mapping() {
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
	
	private static Param param(Consumer<Param> create) {
		Param root = new Param(null);
		create.accept(root);
		return root;
	}
	
	
	// TODO checkSecured throws serverException
	
	@ParameterizedTest
	@MethodSource("dataRun")
	public void testRun(
			String uri, List<Object> pathParams, String redirect,
			Object controller, ThrowingFunction<Object, Method, Exception> getMethod,
			Response expected) throws Throwable {
		
		Identity identity = mock(Identity.class);
		
		MappedAction action = new MappedAction(
			null, null, null, null,
			getMethod.apply(controller), ()->controller,
			null
		);
		
		ControllerAnswer answer = new ControllerAnswer(
			mock(Router.class), mock(Param.class), mock(TemplateFactory.class),
			mock(Link.class), mock(Logger.class)
		);
		Request request = new Request(
			"", HttpMethod.GET, new Headers(), MapDictionary.hashMap(),
			new RequestParameters(), null, Optional.empty()
		);
		request.getPathParams().addAll(pathParams);
		
		Response actual = answer.run(uri, action, request, identity);
		assertEquals(expected, actual);
	}
	
	public static Object[] dataRun() {
		return new Object[] {
			// interruption in prevalidate
			new Object[] {
				"/uri", Arrays.asList(), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "interrupted");
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize 
			new Object[] {
				"/uri", Arrays.asList(), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "interrupted");
							};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// TODO convert to correct calling
			/*// interrupted in authorize - authMode is header, redirect is null
			new Object[] {
				"/uri", Arrays.asList(), HEADER, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize - authMode is header, redirect is not null
			new Object[] {
				"/uri", Arrays.asList(), HEADER, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize - authMode is not header, redirect is null
			new Object[] {
				"/uri", Arrays.asList(), COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in authorize - authMode is not header, redirect is not null
			new Object[] {
				"/uri", Arrays.asList(), COOKIE, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, new Headers(), "/redirect?backlink=%2Furi", false)
			},
			// interrupted in authorize - authMode is not header, redirect is not null, url is root
			new Object[] {
				"/", Arrays.asList(), COOKIE, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, identity)->{
								throw new RequestInterruptedException(
									new TextResponse(StatusCode.OK, new Headers(), "interrupted")
								);
							})
							.createResponse((request, identity)->{
								fail();
								return new TextResponse(StatusCode.OK, new Headers(), "Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				new RedirectResponse(StatusCode.TEMPORARY_REDIRECT, new Headers(), "/redirect", false)
			},*/
			// interrupted in validate
			new Object[] {
				"/uri", Arrays.asList(), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "interrupted");
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// interrupted in create
			new Object[] {
				"/uri", Arrays.asList(), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "interrupted");
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "interrupted")
			},
			// create return response
			new Object[] {
				"/uri", Arrays.asList(), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "response");
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index")),
				new TextResponse(StatusCode.OK, new Headers(), "response")
			},
			// create with params
			new Object[] {
				"/uri", Arrays.asList(10, "aaa"), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index", int.class, String.class)),
				new TextResponse(StatusCode.OK, new Headers(), "Response 10: aaa")
			},
			// create with params - cast
			new Object[] {
				"/uri", Arrays.asList("10", "aaa"), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index", int.class, String.class)),
				new TextResponse(StatusCode.OK, new Headers(), "Response 10: aaa")
			},
			// create with params - wrong type
			new Object[] {
				"/uri", Arrays.asList("not a number", "aaa"), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index", int.class, String.class)),
				new EmptyResponse(StatusCode.BAD_REQUEST, new Headers())
			},
			// create with params - less that expected
			new Object[] {
				"/uri", Arrays.asList(10), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index", int.class, String.class)),
				new EmptyResponse(StatusCode.BAD_REQUEST, new Headers())
			},
			// create with params - more than expected
			new Object[] {
				"/uri", Arrays.asList(10, "aaa", "bb"), null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return (request, identity)->{
							return new TextResponse(StatusCode.OK, new Headers(), "Response " + id + ": " + value);
						};
					} 
				}, throwingFunction(Object.class, Method.class, (o)->o.getClass().getMethod("index", int.class, String.class)),
				new EmptyResponse(StatusCode.BAD_REQUEST, new Headers())
			}
		};
	}

/*
	@Test
	public void testParseBodyThrowsWithNotSupportedTypes() throws ServerException {
		ControllerAnswer answer = new ControllerAnswer(
			mock(Router.class), mock(Param.class), mock(TemplateFactory.class),
			mock(Link.class), mock(Logger.class)
		);
		Request request = new Request(
			"",
			HttpMethod.GET, 
			new Headers(), 
			MapDictionary.hashMap(),
			new RequestParameters().put("some", "parameter"),
			null, // body
			Optional.empty()
		);
		ServerException expected = assertThrows(ServerException.class, ()->{
			answer.parseBody(request, Arrays.asList(), mock(MappedAction.class));
		});
		assertNotNull(expected);
	}
	
	@ParameterizedTest
	@MethodSource("dataParseBody")
	public void testParseBody(Request request, List<BodyType> allowedTypes, Consumer<Request> check) throws ServerException {
		ControllerAnswer answer = new ControllerAnswer(
			mock(Router.class), mock(Param.class), mock(TemplateFactory.class),
			mock(Link.class), mock(Logger.class)
		);
		answer.parseBody(request, allowedTypes, mock(MappedAction.class));
		check.accept(request);
	}
	
	public static Object[] dataParseBody() {
		return new Object[] {
			// request contains body in map - nothing change
			new Object[] {
				new Request(
					"",
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
					"",
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
					"",
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
					"",
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
					"",
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
					"",
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
					"",
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
					"",
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

	private static byte[] getJson() {
		return "{\"some\": \"parameter\"}".getBytes();
	}

	private static byte[] getXml() {
		return "<root><some>parameter</some></root>".getBytes();
	}*/

}
