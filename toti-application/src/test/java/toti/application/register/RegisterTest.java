package toti.application.register;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ji.common.structures.ObjectBuilder;
import ji.socketCommunication.http.HttpMethod;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import test.ControllerA;
import test.TestModule;

@RunWith(JUnitParamsRunner.class)
public class RegisterTest {
	
	// TODO test with two controllers
	
	@Test
	public void testAddController() {
		Param root = new Param(null);
		Register register = new Register(root, new ObjectBuilder<>(new TestModule()));
		register.addController(ControllerA.class, ()->new ControllerA());
		
	//	System.out.println(root);
		
		assertEquals(1, root.getChilds().size());
		assertEquals(0, root.getActions().size());
		
		Param module = root.getChild("testingModule");
		assertNotNull(module);
		assertEquals(1, module.getChilds().size());
		assertEquals(0, module.getActions().size());
		
		Param controller = module.getChild("A");
		assertNotNull(controller);
		assertEquals(2, controller.getChilds().size());
		assertEquals(3, controller.getActions().size());
		
		assertNotNull(controller.getAction(HttpMethod.GET));
		assertNotNull(controller.getAction(HttpMethod.POST));
		assertNotNull(controller.getAction(HttpMethod.PUT));

		assertTrue(
			controller.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerA.class.getName(), "index")
		);
		assertTrue(
			controller.getAction(HttpMethod.POST)
			.asssertNames("testingModule", ControllerA.class.getName(), "form")
		);
		assertTrue(
			controller.getAction(HttpMethod.PUT)
			.asssertNames("testingModule", ControllerA.class.getName(), "form")
		);
		
		Param list = controller.getChild("list");
		assertNotNull(list);
		assertEquals(0, list.getChilds().size());
		assertEquals(1, list.getActions().size());
		assertNotNull(list.getAction(HttpMethod.GET));
		assertTrue(
			list.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerA.class.getName(), "list")
		);
		
		Param get = controller.getChild("get");
		assertNotNull(get);
		assertEquals(0, get.getChilds().size());
		assertEquals(1, get.getActions().size());

		assertTrue(
			get.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerA.class.getName(), "get")
		);
	}
	
	@Test(expected = RegisterException.class)
	public void testAddControllerThanAlreadyExists() {
		Param root = new Param(null);
		Register register = new Register(root, new ObjectBuilder<>(new TestModule()));
		register.addController(ControllerA.class, ()->new ControllerA());
		register.addController(ControllerA.class, ()->new ControllerA());
	}
	
	@Test(expected = RuntimeException.class)
	public void testGetParamThrowsIfPartContainsSlash() {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>());
		register.getParam("a/x", new Param(""));
	}
	
	@Test
	@Parameters(method="dataGetParam")
	public void testGetParam(Param parent, String part, Param expected) {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>());
		assertEquals(expected, register.getParam(part, parent));
	}
	
	public Object[] dataGetParam() {
		return new Object[] {
			new Object[] {
				new Param("parent"), null, new Param("parent")
			},
			new Object[] {
				new Param("parent"), "", new Param("parent")
			},
			new Object[] {
				new Param("parent"), "child", new Param("child")
			}
		};
	}

	@Test(expected = RegisterException.class)
	public void testAddControllerThrowsIfModuleIsNull() {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>());
		register.addController(ControllerA.class, ()->new ControllerA());
	}
	
	@Test(expected = RegisterException.class)
	public void testAdControllerThrowsIfClassIsNotController() {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>(new TestModule()));
		register.addController(SomeClass.class, ()->new SomeClass());
	}

	@Test(expected = RegisterException.class)
	@Ignore
	public void testAddControllerThrowsIfClassIsAnnonymous() {
		@SuppressWarnings("unused")
		Register register = new Register(mock(Param.class), new ObjectBuilder<>());
		// register.addController(RegisteredController.class, ()->new RegisteredController());
	}
	
	class SomeClass {
		private String id;
		public SomeClass() {}
		public SomeClass(String id) {
			this.id = id;
		}
		public String getId() {
			return id;
		}
	}
	
	@Test
	public void testFactoryWithoutCustomName() {
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class));
		
		assertFalse(register.isFactoryPresent(SomeClass.class));
		
		register.addFactory(SomeClass.class, ()->new SomeClass("id"));
		assertEquals("id", register.getFactory(SomeClass.class).create().getId());
	}
	
	@Test
	public void testFactoryWithCustomName() {
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class));
		
		assertFalse(register.isFactoryPresent("someName"));
		
		register.addFactory("someName", ()->new SomeClass("id"));
		assertEquals("id", register.getFactory("someName", SomeClass.class).create().getId());
		assertFalse(register.isFactoryPresent(SomeClass.class));
	}
	
	@Test
	public void testServiceWithoutCustomName() {
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class));
		
		assertFalse(register.isServicePresent(SomeClass.class));
		SomeClass instance = new SomeClass();
		register.addService(instance);
		assertEquals(instance, register.getService(SomeClass.class));
	}
	
	@Test
	public void testServiceWithCustomName() {
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class));
		
		assertFalse(register.isServicePresent("someInstance"));
		SomeClass instance = new SomeClass();
		register.addService("someInstance", instance);
		assertEquals(instance, register.getService("someInstance", SomeClass.class));
		
		assertFalse(register.isServicePresent(SomeClass.class));
	}

}
