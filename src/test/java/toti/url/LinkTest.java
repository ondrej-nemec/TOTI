package toti.url;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

import common.structures.ListInit;
import common.structures.MapInit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class LinkTest {
	
	@Test
	public void testCreateLinkWithMethod() {
		assertEquals(
			"/core/users/insert",
			// new Link("/[module]/[controller]/[method]").create(TestingModule.class, TestingController.class, (c)->c.doInsert())
			new Link("/[module]/[controller]/[method]").create(TestingModule.class, TestingController.class, TestingController::doInsert)
		);
	}

	@Test
	@Parameters(method="dataCreateLink")
	public void testCreateLink(
			String pattern, String expected, 
			String module, String path, String controller, String method,
			UrlParam ...params) {
		assertEquals(expected, new Link(pattern).create(module, path, controller, method, params));
	}
	
	public Object[] dataCreateLink() {
		return new Object[] {
			new Object[] {
				"/[path]/[module]/[controller]/[method]/[param]",
				"/api/core/users/get/5",
				"core", "api", "users", "get", new UrlParam[] {
					new UrlParam(5)
				}
			},
			new Object[] {
					"/[path]/[module]/[controller]/[method]/[param]",
					"/api/core/users/get/5",
					"core/", "/api", "/users/", "get", new UrlParam[] {
						new UrlParam(5)
					}
				},
			new Object[] {
					"</[path]>/[module]/[controller]/[method]</[param]>",
					"/core/users/get/5",
					"core", null, "users", "get", new UrlParam[] {
						new UrlParam(5)
					}
				},
			new Object[] {
					"</[path]>/[module]/[controller]/[method]</[param]></[param]>",
					"/core/users/list",
					"core", null, "users", "list", new UrlParam[] {}
				},
			 new Object[] {
					"/[module]/[controller]/<[method]/[param]>",
					"/core/users/",
					"core", null, "users", "get", new UrlParam[] {}
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]/[param]",
					"/api/core/users/get/5",
					"core", "api", "users", "get", new UrlParam[] {
						new UrlParam(5), new UrlParam("smith.j")
					}
				}
		};
	}

	@Test
	@Parameters(method="dataCreateLinkGetParameters")
	public void testCreateLinkGetParameters(
			String pattern, String expected, 
			String module, String path, String controller, String method,
			UrlParam ...params) {
		assertEquals(expected, new Link(pattern).create(module, path, controller, method, params));
	}
	
	public Object[] dataCreateLinkGetParameters() {
		return new Object[] {
			new Object[] {
				"/[path]/[module]/[controller]/[method]/[param]",
				"/api/core/users/get/5?login=smith.j",
				"core", "api", "users", "get", new UrlParam[] {
					new UrlParam(5), new UrlParam("login", "smith.j")
				}
			},
			new Object[] {
					"/[path]/[module]/[controller]/[method]/[param]",
					"/api/core/users/get/5?login=smith.j&age=18",
					"core", "api", "users", "get", new UrlParam[] {
						new UrlParam(5), new UrlParam("login", "smith.j"), 
						new UrlParam("age", 18)
					}
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]",
					"/api/core/users/insert?user[login]=smith.j&user[age]=18",
					"core", "api", "users", "insert", new UrlParam[] {
						new UrlParam(
							"user",
							new MapInit<>().append("login", "smith.j").append("age", 18).toMap()
						)
					}
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]",
					"/api/core/users/delete?login[]=smith.j&login[]=doe.j",
					"core", "api", "users", "delete", new UrlParam[] {
						new UrlParam(
							"login",
							new ListInit<>().add("smith.j").add("doe.j").toList()
						)
					}
				},
			new Object[] {
					"/[path]/[module]/[controller]/[method]",
					"/api/core/users/update?user[login][]=smith.j&user[login][]=doe.j&user[age]=18",
					"core", "api", "users", "update", new UrlParam[] {
						new UrlParam(
							"user",
							new MapInit<>()
								.append("login", new ListInit<>().add("smith.j").add("doe.j").toList())
								.append("age", 18).toMap()
						)
					}
				}
		};
	}
	
}
