package toti.url;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.ListInit;
import ji.common.structures.MapInit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.register.Register;

@RunWith(JUnitParamsRunner.class)
public class LinkTest {
	
	// TODO parse method - lang,translations,...


	@Test
	@Parameters(method="dataCreateLink")
	public void testCreateLink(
			String pattern, String expected, 
			String module, String path, String controller, String method,
			List<UrlParam> params) {
		assertEquals(expected, new Link(pattern, null).parse(module, path, controller, method, params));
	}
	
	public Object[] dataCreateLink() {
		return new Object[] {
			new Object[] {
				"/[path]/[module]/[controller]/[method]/[param]",
				"/api/core/users/get/5",
				"core", "api", "users", "get",
				Arrays.asList(new UrlParam(5))
			},
			new Object[] {
					"/[path]/[module]/[controller]/[method]/[param]",
					"/api/core/users/get/5",
					"core/", "/api", "/users/", "get",
					Arrays.asList(new UrlParam(5))
				},
			new Object[] {
					"</[path]>/[module]/[controller]/[method]</[param]>",
					"/core/users/get/5",
					"core", null, "users", "get",
					Arrays.asList(new UrlParam(5))
				},
			new Object[] {
					"</[path]>/[module]/[controller]/[method]</[param]></[param]>",
					"/core/users/list",
					"core", null, "users", "list",
					Arrays.asList()
				},
			 new Object[] {
					"/[module]/[controller]/<[method]/[param]>",
					"/core/users/",
					"core", null, "users", "get",
					Arrays.asList()
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]/[param]",
					"/api/core/users/get/5",
					"core", "api", "users", "get",
					Arrays.asList(new UrlParam(5), new UrlParam("smith.j"))
				}
		};
	}

	@Test
	@Parameters(method="dataCreateLinkGetParameters")
	public void testCreateLinkGetParameters(
			String pattern, String expected, 
			String module, String path, String controller, String method,
			List<UrlParam> params) {
		assertEquals(expected, new Link(pattern, null).parse(module, path, controller, method, params));
	}
	
	public Object[] dataCreateLinkGetParameters() {
		return new Object[] {
			new Object[] {
				"/[path]/[module]/[controller]/[method]/[param]",
				"/api/core/users/get/5?login=smith.j",
				"core", "api", "users", "get",
				Arrays.asList(new UrlParam(5), new UrlParam("login", "smith.j"))
			},
			new Object[] {
					"/[path]/[module]/[controller]/[method]/[param]",
					"/api/core/users/get/5?login=smith.j&age=18",
					"core", "api", "users", "get",
					Arrays.asList(new UrlParam(5), new UrlParam("login", "smith.j"), new UrlParam("age", 18))
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]",
					"/api/core/users/insert?user[login]=smith.j&user[age]=18",
					"core", "api", "users", "insert",
					Arrays.asList(new UrlParam(
						"user",
						new MapInit<>().append("login", "smith.j").append("age", 18).toMap()
					))
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]",
					"/api/core/users/delete?login[]=smith.j&login[]=doe.j",
					"core", "api", "users", "delete", Arrays.asList(
						new UrlParam(
							"login",
							new ListInit<>().add("smith.j").add("doe.j").toList()
						)
					)
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]",
					"/api/core/users/update?user[login][]=smith.j&user[login][]=doe.j&user[age]=18",
					"core", "api", "users", "update", Arrays.asList(
						new UrlParam(
							"user",
							new MapInit<>()
								.append("login", new ListInit<>().add("smith.j").add("doe.j").toList())
								.append("age", 18).toMap()
						)
					)
				}
		};
	}
	
	///////////////////////////////////////

	@Test
	public void testCreateUrlFromClassNoParams() {
		assertEquals(
			"/toti/test/no-param",
			getLink().create(TestingController.class, c->c.methodNoParams())
		);
	}

	@Test
	public void testCreateUrlFromClassUrlParam() {
		assertEquals(
			"/toti/test/url-param/42",
			getLink().create(TestingController.class, c->c.methodPathParam(null), "42")
		);
	}

	@Test
	public void testCreateUrlFromClassGetParam() {
		Map<String, Object> params = new HashMap<>();
		params.put("param", 27);
		assertEquals(
			"/toti/test/get-param?param=27",
			getLink().create(TestingController.class, c->c.methodQueryParam(null), params)
		);
	}
	
	@Test
	public void testCreateUrlFromClassUrlAndGetParam() {
		Map<String, Object> params = new HashMap<>();
		params.put("param", 27);
		assertEquals(
			"/toti/test/url-get-param/42?param=27",
			getLink().create(TestingController.class, c->c.methodPathAndQueryParam(null, null), params, 42)
		);
	}
	
	/********************************/
	
	@Test
	public void testCreateUrlFromStringDefinitionNoParams() {
		assertEquals(
			"/toti/test/no-param",
			getLink().create( "toti.url.TestingController", "methodNoParams")
		);
	}

	@Test
	public void testCreateUrlFromStringDefinitionUrlParam() {
		assertEquals(
			"/toti/test/url-param/42",
			getLink().create("toti.url.TestingController", "methodUrlParam", "42")
		);
	}

	@Test
	public void testCreateUrlFromStringDefinitionGetParam() {
		Map<String, Object> params = new HashMap<>();
		params.put("param", 27);
		assertEquals(
			"/toti/test/get-param?param=27",
			getLink().create("toti.url.TestingController", "methodGetParam", params)
		);
	}
	
	@Test
	public void testCreateUrlFromStringDefinitionUrlAndGetParam() {
		Map<String, Object> params = new HashMap<>();
		params.put("param", 27);
		assertEquals(
			"/toti/test/get-param/42?param=27",
			getLink().create("toti.url.TestingController", "methodGetParam", params, 42)
		);
	}
	
	/*********************************/
	
	// TODO throw if not in expected format
	@Test
	@Parameters(method="dataCreateUrlFromSingleString")
	public void testCreateUrlFromSingleString(String expected, String string) {
		assertEquals(expected, getLink().create(string));
	}
	
	public Object[] dataCreateUrlFromSingleString() {
		return new Object[] {
			new Object[] {
				"/toti/test/no-param",
				"toti.url.TestingController:methodNoParams"
			},
			new Object[] {
				"/toti/test/url-param/42",
				"toti.url.TestingController:methodUrlParam:42"
			},
			new Object[] {
				"/toti/test/url-get-param/42?param=27",
				"toti.url.TestingController:methodUrlAndGetParam:42:param=27"
			},
			new Object[] {
				"/toti/test/get-param?param=27",
				"toti.url.TestingController:methodGetParam:param=27"
			}
		};
	}
	
	////////////////////////////////////
	
	@Test
	@Parameters(method="dataIsRelativeCheckURL")
	public void testIsRelativeCheckURL(String url, boolean expected) {
		assertEquals(expected, Link.isRelative(url));
	}
	
	public Object[] dataIsRelativeCheckURL() {
		return new Object[] {
			new Object[] { true, "/some/url" },
			new Object[] { false, "https://example.com" },
			new Object[] { false, "example.com" },
			new Object[] { false, "//example.com" },
			new Object[] { false, "https:/\\example.com" }
		};
	}
	
	private Link getLink() {
		Register register = new Register();
		Link link = new Link("/[module]/[controller]/[method]</[param]></[param]>", register);
		try {
			new TestingModule().initInstances(null, null, register, link, null, null);
		} catch (Exception e) { /* ignored */ }
		return link;
	}
	
}
