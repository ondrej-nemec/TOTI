package toti.answers.router;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.exceptions.LogicException;
import ji.common.structures.MapInit;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import test.NotRegisteredController;
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
	// TODO test create from string controler, string method - template tag
	/*
	class is not annotated as controller
	method is not annotated as action
	register returns null for class module - not registered
	with and without query params - staci jednoduse - parse je testovano samostatne
	with and without path params
	*/

	// TODO test parseParams


	 @Test
	 public void test() {
	// TODO test create from class, function etc
		 fail("TODO");
	 }
	
}
