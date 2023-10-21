package toti.answers.router;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import ji.common.structures.ObjectBuilder;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import test.ControllerA;
import test.NotController;
import test.NotRegisteredController;
import test.TestModule;
import toti.application.Module;
import toti.application.register.Param;
import toti.application.register.Register;

@RunWith(JUnitParamsRunner.class)
public class LinkTest {
	
	@Test(expected = LogicException.class)
	@Parameters({ "", ":", "something", "something:" })
	public void testParseStringHrefThrowsWithWrongString(String href) {
		Link link = new Link(mock(Register.class));
		link.parseStringHref(href);
	}

	@Test
	@Parameters(method="dataParseStringHrefReturnsCorrectResult")
	public void testParseStringHrefReturnsCorrectResult(String href, StringHref expected) {
		Link link = new Link(mock(Register.class));
		StringHref actual = link.parseStringHref(href);
		assertEquals(expected, actual);
	}
	
	public Object[] dataParseStringHrefReturnsCorrectResult() {
		return new Object[] {
			new Object[] {
				":method", new StringHref(null, "method", MapInit.create().toMap(), Arrays.asList())
			},
			new Object[] {
				"controller:method", new StringHref("controller", "method", MapInit.create().toMap(), Arrays.asList())
			},
			new Object[] {
				"controller:method:p=v", new StringHref(
					"controller", "method",
					MapInit.create().append("p", "v").toMap(),
					Arrays.asList()
				)
			},
			new Object[] {
				"controller:method:p1=v1:p2=v2", new StringHref(
					"controller", "method",
					MapInit.create().append("p1", "v1").append("p2", "v2").toMap(),
					Arrays.asList()
				)
			},
			new Object[] {
				"controller:method:x", new StringHref(
					"controller", "method",
					MapInit.create().toMap(),
					Arrays.asList("x")
				)
			},
			new Object[] {
				"controller:method:x:y", new StringHref(
					"controller", "method",
					MapInit.create().toMap(),
					Arrays.asList("x", "y")
				)
			},
			new Object[] {
				"controller:method:x:p=v", new StringHref(
					"controller", "method",
					MapInit.create().append("p", "v").toMap(),
					Arrays.asList("x")
				)
			},
			new Object[] {
				"controller:method:x:y:p1=v1:p2=v2", new StringHref(
					"controller", "method",
					MapInit.create().append("p1", "v1").append("p2", "v2").toMap(),
					Arrays.asList("x", "y")
				)
			},
			new Object[] {
				"controller:method:p1=v1:p2=v2:x:y", new StringHref(
						"controller", "method",
						MapInit.create().append("p1", "v1").append("p2", "v2").toMap(),
						Arrays.asList("x", "y")
					)
			},
			new Object[] {
				"controller:method:x:p1=v1:y:p2=v2", new StringHref(
					"controller", "method",
					MapInit.create().append("p1", "v1").append("p2", "v2").toMap(),
					Arrays.asList("x", "y")
				)
			},
			new Object[] {
				"controller:method:p1=v1:x:p2=v2:y", new StringHref(
					"controller", "method",
					MapInit.create().append("p1", "v1").append("p2", "v2").toMap(),
					Arrays.asList("x", "y")
				)
			}
		};
	}
	
	@Test(expected = NoSuchMethodException.class)
	@Parameters(method="dataGetMethodThrowsIfNoMethodFound")
	public void testGetMethodThrowsIfNoMethodFound(String method, int count) throws NoSuchMethodException {
		Link link = new Link(mock(Register.class));
		link.getMethod(NotRegisteredController.class, method, count);
	}
	
	public Object[] dataGetMethodThrowsIfNoMethodFound()  {
		return new Object[] {
			new Object[] {
				"index2", 0 // not existing
			},
			new Object[] {
				"index", 2 // wrong param count
			},
			new Object[] {
				"someMethod", 0 // not action
			},
			new Object[] {
				"someMethod", 2 // not action
			},
			new Object[] {
				"someMethod", 1 // not action - same method as previous but second param will be null
			}
		};
	}
	
	@Test
	@Parameters(method="dataGetMethodReturnsCorrectMethod")
	public void testGetMethodReturnsCorrectMethod(String method, int parameterCount, Method expected) throws NoSuchMethodException {
		Link link = new Link(mock(Register.class));
		Method actual = link.getMethod(NotRegisteredController.class, method, parameterCount);
		assertEquals(expected, actual);
	}
	
	public Object[] dataGetMethodReturnsCorrectMethod() throws NoSuchMethodException, SecurityException {
		return new Object[] {
			new Object[] {
				"index", 0, NotRegisteredController.class.getMethod("index")
			},
			new Object[] {
				// there are two methods with one parameter, with int is first
				"index", 1, NotRegisteredController.class.getMethod("index", Integer.class)
			},
			new Object[] {
				"moreParams", 2, NotRegisteredController.class.getMethod("moreParams", String.class, Integer.class)
			},
			// second parameter of moreParams will be null
			new Object[] {
				"moreParams", 1, NotRegisteredController.class.getMethod("moreParams", String.class, Integer.class)
			}
		};
	}
	
	@Test(expected = ClassNotFoundException.class)
	public void testGetControllerThrowsOnWrongClass() throws ClassNotFoundException {
		Link link = new Link(mock(Register.class));
		link.getController("test.NotExistingClass");
	}
	
	@Test
	public void testGetControllerReturnsCorrectClass() throws ClassNotFoundException {
		Link link = new Link(mock(Register.class));
		assertEquals(ControllerA.class, link.getController("test.ControllerA"));
	}
	
	@Test
	public void testGetControllerReturnsClassIfStacktraceIsController() throws ClassNotFoundException {
		Link link = new Link(mock(Register.class));
		NotRegisteredController controller = new NotRegisteredController();
		assertEquals(NotRegisteredController.class, controller.runLinkMethod(()->link.getController(null)));
	}

	@Test(expected = ClassNotFoundException.class)
	public void testGetControllerThrowsWithNullNameAndNoController() throws ClassNotFoundException {
		Link link = new Link(mock(Register.class));
		link.getController(null);
	}
	
	@Test(expected = LogicException.class)
	public void testCreateThrowsIfClassIsNotController() throws NoSuchMethodException, SecurityException {
		Link link = new Link(mock(Register.class));
		link.create(NotController.class, NotController.class.getMethod("index"), new HashMap<>());
	}
	
	@Test(expected = LogicException.class)
	public void testCreateThrowsIfMethodNotAction() throws NoSuchMethodException, SecurityException {
		Link link = new Link(mock(Register.class));
		link.create(ControllerA.class, ControllerA.class.getMethod("notAction"), new HashMap<>());
	}
	
	@Test
	@Parameters(method="dataCreate")
	public void testCreate(Map<String, Object> queryParams, Object[] pathParams, String expected) throws NoSuchMethodException, SecurityException {
		ObjectBuilder<Module> module = new ObjectBuilder<>(new TestModule());
		Register register = new Register(new Param(null), module);
		register.addController(ControllerA.class, ()->new ControllerA());
		Link link = new Link(register);
		assertEquals(expected, link.create(
			ControllerA.class, ControllerA.class.getMethod("index"), queryParams, pathParams
		));
	}
	 
	public Object[] dataCreate() {
		return new Object[] {
			new Object[] {
				MapInit.create().toMap(),
				new Object[] {},
				"/testingModule/controllerA/index"
			},
			new Object[] {
				MapInit.create().toMap(),
				new Object[] { "a", "b", "c" },
				"/testingModule/controllerA/index/a/b/c"
			},
			new Object[] {
				MapInit.create().append("a", "b").append("c", "d").toMap(),
				new Object[] {},
				"/testingModule/controllerA/index?a=b&c=d"
			},
			new Object[] {
				MapInit.create().append("a", "b").append("c", "d").toMap(), 
				new Object[] { "a", "b", "c" },
				"/testingModule/controllerA/index/a/b/c?a=b&c=d"
			}
		};
	}
	
	@Test
	@Parameters(method="dataCreateBase")
	public void testCreateBase(String module, String controller, String action, String expected) {
		Link link = new Link(mock(Register.class));
		assertEquals(expected, link.createBase(module, controller, action));
	}
	
	public Object[] dataCreateBase() {
		return new Object[] {
			new Object[] { "m", "c", "a", "/m/c/a" },
			new Object[] { "", "c", "a", "/c/a" },
			new Object[] { null, "c", "a", "/c/a" },
			new Object[] { "m", "", "a", "/m/a" },
			new Object[] { "m", null, "a", "/m/a" },
			new Object[] { "m", "c", "", "/m/c" },
			new Object[] { "m", "c", null, "/m/c" },
			new Object[] { "", "", "", "" },
		};
	}

	@Test
	@Parameters(method="dataParseParams")
	public void testParseParams(String key, Object value, String expected) {
		StringBuilder result = new StringBuilder();
		Link link = new Link(mock(Register.class));
		link.parseParams(result, key, value);
		assertEquals(expected, result.toString());
	}
	
	public Object[] dataParseParams() {
		return new Object[] {
			new Object[] {
				"key", "value", "key=value"
			},
			new Object[] {
				"list", Arrays.asList("a", "b", "c"), "list[]=a&list[]=b&list[]=c"
			},
			new Object[] {
				"map", MapInit.create().append("a", "b").append("c", "d").toMap(),
				"map[a]=b&map[c]=d"
			},
			new Object[] {
				"main", Arrays.asList(
					MapInit.create().append("a1", "b1").append("c1", "d1").toMap(),
					MapInit.create().append("a2", "b2").append("c2", "d2").toMap()
				), "main[][a1]=b1&main[][c1]=d1&main[][a2]=b2&main[][c2]=d2"
			},
			new Object[] {
				"main", MapInit.create().append("a", Arrays.asList(1, 2, 3)).toMap(),
				"main[a][]=1&main[a][]=2&main[a][]=3"
			}
		};
	}
	
}
