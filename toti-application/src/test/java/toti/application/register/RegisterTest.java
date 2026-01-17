package toti.application.register;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import test.ControllerA;
import test.ControllerB;
import test.ControllerC;
import test.TestModule;
import toti.answers.router.UriPattern;
import toti.common.structures.ObjectBuilder;
import toti.extensions.Extension;
import toti.tcpip.enums.HttpMethod;

public class RegisterTest {
	
	// TODO test with two controllers
	
	@Test
	public void testAddController() {
		Param root = new Param(null);
		Register register = new Register(root, new ObjectBuilder<>(new TestModule()), getPattern(), new HashMap<>());
		register.addController(ControllerA.class, ()->new ControllerA());
		
	//	System.out.println(root);
		
		assertEquals(1, root.getChilds().size());
		assertEquals(0, root.getActions().size());
		
		Param module = root.getChild("testingModule");
		assertNotNull(module);
		assertEquals(1, module.getChilds().size());
		assertEquals(0, module.getActions().size());
		
		Param controller = module.getChild("controllerA");
		assertNotNull(controller);
		assertEquals(3, controller.getChilds().size());
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
		
		Param index = controller.getChild(null);
		assertNotNull(index);
		assertEquals(1, index.getChilds().size());
		assertEquals(2, index.getActions().size());
		
		assertNotNull(index.getAction(HttpMethod.GET));
		assertNotNull(index.getAction(HttpMethod.POST));
		assertTrue(
				index.getAction(HttpMethod.POST)
			.asssertNames("testingModule", ControllerA.class.getName(), "index", "[class java.lang.String]")
		);
		assertTrue(
				index.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerA.class.getName(), "index", "[class java.lang.Integer]")
		);

		Param index2 = index.getChild(null);
		assertNotNull(index2);
		assertEquals(0, index2.getChilds().size());
		assertEquals(1, index2.getActions().size());
		
		assertNotNull(index2.getAction(HttpMethod.GET));
		assertTrue(
			index2.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerA.class.getName(), "index", "[int, class java.lang.String]")
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
		assertEquals(1, get.getChilds().size());
		assertEquals(0, get.getActions().size());
		
		Param getParam = get.getChild(null);
		assertNotNull(getParam);
		assertEquals(0, getParam.getChilds().size());
		assertEquals(1, getParam.getActions().size());

		assertTrue(
			getParam.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerA.class.getName(), "get", "[class java.lang.Integer]")
		);
	}
	
	@Test
	public void testAddControllerWithCustomLink() {
		UriPattern pattern = mock(UriPattern.class);
		when(pattern.createUri(any(), any(), any(), any(), any(), any()))
			.thenReturn("/testingModule/controllerB/[param]")
			.thenReturn("/testingModule/controllerB/[param]/generate");
		Param root = new Param(null);
		
		Register register = new Register(root, new ObjectBuilder<>(new TestModule()), pattern, new HashMap<>());
		register.addController(ControllerB.class, ()->new ControllerB());
		
	//	System.out.println(root);
		
		assertEquals(1, root.getChilds().size());
		assertEquals(0, root.getActions().size());
		
		Param module = root.getChild("testingModule");
		assertNotNull(module);
		assertEquals(1, module.getChilds().size());
		assertEquals(0, module.getActions().size());
		
		Param controller = module.getChild("controllerB");
		assertNotNull(controller);
		assertEquals(1, controller.getChilds().size());
		assertEquals(0, controller.getActions().size());
		
		Param param = controller.getChild(null);
		assertNotNull(param);
		assertEquals(1, param.getChilds().size());
		assertEquals(1, param.getActions().size());
		assertNotNull(param.getAction(HttpMethod.GET));
		assertTrue(
			param.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerB.class.getName(), "get", "[class java.lang.Integer]")
		);
		
		Param generate = param.getChild("generate");
		assertNotNull(generate);
		assertEquals(0, generate.getChilds().size());
		assertEquals(1, generate.getActions().size());

		assertTrue(
				generate.getAction(HttpMethod.GET)
			.asssertNames("testingModule", ControllerB.class.getName(), "generate", "[class java.lang.Integer]")
		);
	}
	
	@Test
	public void testAddControllerWithMoreSameNameMethodsAndSameparametersCount() {
		Param root = new Param(null);
		Register register = new Register(root, new ObjectBuilder<>(new TestModule()), getPattern(), new HashMap<>());
		RegisterException expected = assertThrows(RegisterException.class, ()->{
			register.addController(ControllerC.class, ()->new ControllerC());
		});
		assertNotNull(expected);
	}
	
	@Test
	public void testAddControllerThanAlreadyExists() {
		UriPattern pattern = mock(UriPattern.class);
		when(pattern.createUri(any(), any(), any(), any(), any(), any())).thenReturn("/a/b/c");
		Param root = new Param(null);
		Register register = new Register(root, new ObjectBuilder<>(new TestModule()), pattern, new HashMap<>());
		register.addController(ControllerA.class, ()->new ControllerA());
		
		RegisterException expected = assertThrows(RegisterException.class, ()->{
			register.addController(ControllerA.class, ()->new ControllerA());
		});
		assertNotNull(expected);
	}
	
	@Test
	public void testGetParamThrowsIfPartContainsSlash() {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>(), mock(UriPattern.class), new HashMap<>());
		RuntimeException expected = assertThrows(RuntimeException.class, ()->{
			register.getParam("a/x", new Param(""));
		});
		assertNotNull(expected);
	}
	
	@ParameterizedTest
	@MethodSource("dataGetParam")
	public void testGetParam(Param parent, String part, Param expected) {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>(), getPattern(), new HashMap<>());
		assertEquals(expected, register.getParam(part, parent));
	}
	
	public static Object[] dataGetParam() {
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

	@Test
	public void testAddControllerThrowsIfModuleIsNull() {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>(), mock(UriPattern.class), new HashMap<>());
		RegisterException expected = assertThrows(RegisterException.class, ()->{
			register.addController(ControllerA.class, ()->new ControllerA());
		});
		assertNotNull(expected);
	}
	
	@Test
	public void testAdControllerThrowsIfClassIsNotController() {
		Register register = new Register(mock(Param.class), new ObjectBuilder<>(new TestModule()), mock(UriPattern.class), new HashMap<>());
		RegisterException expected = assertThrows(RegisterException.class, ()->{
			register.addController(SomeClass.class, ()->new SomeClass());
		});
		assertNotNull(expected);
	}

	@Test
	@Disabled
	public void testAddControllerThrowsIfClassIsAnnonymous() {
		@SuppressWarnings("unused")
		Register register = new Register(mock(Param.class), new ObjectBuilder<>(), getPattern(), new HashMap<>());
		RegisterException expected = assertThrows(RegisterException.class, ()->{
			// register.addController(RegisteredController.class, ()->new RegisteredController());
		});
		assertNotNull(expected);
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
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class), getPattern(), new HashMap<>());
		
		assertFalse(register.isFactoryPresent(SomeClass.class));
		
		register.addFactory(SomeClass.class, ()->new SomeClass("id"));
		assertEquals("id", register.getFactory(SomeClass.class).create().getId());
	}
	
	@Test
	public void testFactoryWithCustomName() {
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class), getPattern(), new HashMap<>());
		
		assertFalse(register.isFactoryPresent("someName"));
		
		register.addFactory("someName", ()->new SomeClass("id"));
		assertEquals("id", register.getFactory("someName", SomeClass.class).create().getId());
		assertFalse(register.isFactoryPresent(SomeClass.class));
	}
	
	@Test
	public void testServiceWithoutCustomName() {
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class), getPattern(), new HashMap<>());
		
		assertFalse(register.isServicePresent(SomeClass.class));
		SomeClass instance = new SomeClass();
		register.addService(instance);
		assertEquals(instance, register.getService(SomeClass.class));
	}
	
	@Test
	public void testServiceWithCustomName() {
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class), getPattern(), new HashMap<>());
		
		assertFalse(register.isServicePresent("someInstance"));
		SomeClass instance = new SomeClass();
		register.addService("someInstance", instance);
		assertEquals(instance, register.getService("someInstance", SomeClass.class));
		
		assertFalse(register.isServicePresent(SomeClass.class));
	}

	@Test
	public void testGetExtension() {
		Map<String, Extension> extensions = new HashMap<>();
		Extension ext1 = new Extension1();
		Extension ext2 = new Extension2();
	//	Extension ext3 = new Extension3();
		extensions.put(ext1.getClass().getName(), ext1);
		extensions.put(ext2.getClass().getName(), ext2);
	//	extensions.put(ext1.getClass().getName(), ext3);
		
		@SuppressWarnings("unchecked")
		Register register = new Register(mock(Param.class), mock(ObjectBuilder.class), getPattern(), extensions);
		
		assertEquals(ext1, register.getExtension(Extension1.class));
		assertEquals(ext2, register.getExtension(Extension2.class));
		assertNull(register.getExtension(Extension3.class));
	}
	
	private UriPattern getPattern() {
		return new UriPattern(){};
	}
	
}
