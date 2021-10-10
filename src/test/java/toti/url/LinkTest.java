package toti.url;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import common.structures.ListInit;
import common.structures.MapInit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import toti.url.Link;
import toti.url.UrlParam;

@RunWith(JUnitParamsRunner.class)
public class LinkTest {
	
	@Test
	public void testCreateLinkWithMethod() {
		assertEquals(
			"/core/users/insert",
			// new Link("/[module]/[controller]/[method]").create(TestingModule.class, TestingController.class, (c)->c.doInsert())
			new Link("/[module]/[controller]/[method]")
				.setModule(TestingModule.class)
				.create(TestingController.class, TestingController::doInsert)
		);
	}

	@Test
	@Parameters(method="dataCreateLink")
	public void testCreateLink(
			String pattern, String expected, 
			String module, String path, String controller, String method,
			List<UrlParam> params) {
		assertEquals(expected, new Link(pattern).create(module, path, controller, method, params));
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
		assertEquals(expected, new Link(pattern).create(module, path, controller, method, params));
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
	
}
