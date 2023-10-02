package toti.answers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.MapDictionary;
import ji.common.structures.ThrowingFunction;
import ji.socketCommunication.http.StatusCode;
import ji.socketCommunication.http.structures.RequestParameters;
import ji.testing.TestCase;
import ji.translator.Locale;
import ji.translator.Translator;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.Headers;
import toti.Router;
import toti.ServerException;
import toti.answers.action.BodyType;
import toti.answers.action.RequestInterruptedException;
import toti.answers.action.ResponseAction;
import toti.answers.action.ResponseBuilder;
import toti.answers.request.Request;
import toti.answers.response.Response;
import toti.application.register.MappedAction;
import toti.application.register.Param;
import toti.security.AuthMode;
import toti.security.Authenticator;
import toti.security.Authorizator;
import toti.security.Identity;
import toti.security.IdentityFactory;
import toti.url.Link;

@RunWith(JUnitParamsRunner.class)
public class ControllerAnswerTest extends TestCase {

	@Test
	public void testAnswer() {
		// TODO
		// projit correct state a overit, ze se vola, co se volat ma
		// stavy, kdy neco hazi vyjimky - chytat pouze server exception - mozna kontrolovat status code
		fail("TODO");
	}

	@Test
	public void testGetMappedAction() {
		// TODO
		/*
		router action
		ruzne pripady mapovani a vysledky
		*/
		fail("TODO");
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
			mock(Authenticator.class), mock(Authorizator.class), mock(IdentityFactory.class),
			mock(Link.class), translator, mock(Logger.class)
		);
		Request request = new Request(new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty());
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
								throw new RequestInterruptedException(Response.getText("interrupted"));
							})
							.createRequest((request, translator, identity)->{
								fail();
								return Response.getText("Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getText("interrupted")
			},
			// interrupted in authorize - authMode is header, redirect is null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.HEADER, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(Response.getText("interrupted"));
							})
							.createRequest((request, translator, identity)->{
								fail();
								return Response.getText("Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getText("interrupted")
			},
			// interrupted in authorize - authMode is header, redirect is not null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.HEADER, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(Response.getText("interrupted"));
							})
							.createRequest((request, translator, identity)->{
								fail();
								return Response.getText("Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getText("interrupted")
			},
			// interrupted in authorize - authMode is not header, redirect is null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(Response.getText("interrupted"));
							})
							.createRequest((request, translator, identity)->{
								fail();
								return Response.getText("Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getText("interrupted")
			},
			// interrupted in authorize - authMode is not header, redirect is not null
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(Response.getText("interrupted"));
							})
							.createRequest((request, translator, identity)->{
								fail();
								return Response.getText("Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getRedirect("/redirect?backlink=%2Furi")
			},
			// interrupted in authorize - authMode is not header, redirect is not null, url is root
			new Object[] {
				"/", Arrays.asList(), AuthMode.COOKIE, "/redirect",
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.authorize((request, translator, identity)->{
								throw new RequestInterruptedException(Response.getText("interrupted"));
							})
							.createRequest((request, translator, identity)->{
								fail();
								return Response.getText("Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getRedirect("/redirect")
			},
			// interrupted in validate
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.validate((request, translator, identity)->{
								throw new RequestInterruptedException(Response.getText("interrupted"));
							})
							.createRequest((request, translator, identity)->{
								fail();
								return Response.getText("Fail");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getText("interrupted")
			},
			// interrupted in create
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.createRequest((request, translator, identity)->{
								throw new RequestInterruptedException(Response.getText("interrupted"));
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getText("interrupted")
			},
			// create return response
			new Object[] {
				"/uri", Arrays.asList(), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index() {
						return ResponseBuilder.get()
							.createRequest((request, translator, identity)->{
								return Response.getText("response");
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index")),
				Response.getText("response")
			},
			// create with params
			new Object[] {
				"/uri", Arrays.asList(10, "aaa"), AuthMode.COOKIE, null,
				new Object() {
					@SuppressWarnings("unused")
					public ResponseAction index(int id, String value) {
						return ResponseBuilder.get()
							.createRequest((request, translator, identity)->{
								return Response.getText("Response " + id + ": " + value);
							});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class)),
				Response.getText("Response 10: aaa")
			}
		};
	}

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
			mock(Authenticator.class), mock(Authorizator.class), mock(IdentityFactory.class),
			mock(Link.class), translator, mock(Logger.class)
		);
		Request request = new Request(new Headers(), MapDictionary.hashMap(), new RequestParameters(), null, Optional.empty());
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
						.createRequest((request, translator, identity)->{
							return Response.getText("Response " + id);
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
						.createRequest((request, translator, identity)->{
							return Response.getText("Response " + id);
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
						.createRequest((request, translator, identity)->{
							return Response.getText("Response " + id + ": " + value);
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
						.createRequest((request, translator, identity)->{
							return Response.getText("Response " + id + ": " + value);
						});
					}
					@SuppressWarnings("unused")
					public ResponseAction index(int id) {
						return ResponseBuilder.get()
						.createRequest((request, translator, identity)->{
							fail();
							return Response.getText("Response " + id);
						});
					} 
				}, throwingFunction((o)->o.getClass().getMethod("index", int.class, String.class))
			}
		};
	}

	@Test
	@Parameters(method="dataCheckSecured")
	public void testCheckSecured(Supplier<MappedAction> mapped, Supplier<Identity> identity, StatusCode expectedCode) {
		ControllerAnswer answer = new ControllerAnswer(
			mock(Router.class), mock(Param.class), new HashMap<>(),
			mock(Authenticator.class), mock(Authorizator.class), mock(IdentityFactory.class),
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
			mock(Authenticator.class), mock(Authorizator.class), mock(IdentityFactory.class),
			mock(Link.class), mock(Translator.class), mock(Logger.class)
		);
		Request request = new Request(
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
			mock(Authenticator.class), mock(Authorizator.class), mock(IdentityFactory.class),
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
