package toti.url;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import common.exceptions.LogicException;
import common.functions.StackTrace;
import common.structures.DictionaryValue;
import common.structures.ObjectBuilder;
import common.structures.ThrowingFunction;
import common.structures.ThrowingSupplier;
import toti.Module;
import toti.annotations.Action;
import toti.annotations.Controller;
import toti.url.mock.MockCreator;
import toti.registr.Registr;
import toti.response.Response;

public class Link {
	
	public final static String PATH = "[path]";
	public final static String MODULE = "[module]";
	public final static String CONTROLLER = "[controller]";
	public final static String METHOD = "[method]";
	public final static String LANG = "[lang]";
	public final static String PARAM = "[param]";
	
	public static String PATTERN = "/[module]</[path]>/[controller]/[method]</[param]></[param]>"; // TODO configurable, one place urls
	
	public static Link get() {
		return new Link(PATTERN);
	}

	public static Link get(String pattern) {
		return new Link(pattern);
	}
	
	private final String pattern;
	
	private ThrowingFunction<Class<?>, Module, Exception> getModule = (controllerClass)->{
		String moduleClass = Registr.get()._getFactory(controllerClass.getName())._2();
		return Module.class.cast(Class.forName(moduleClass).newInstance());
	};
	private ThrowingSupplier<Class<?>, Exception> getController = ()->Class.forName(StackTrace.classParent(
		ste->Class.forName(ste.getClassName()).isAnnotationPresent(Controller.class)
	));
	private ThrowingFunction<Class<?>, Method, Exception> getMethod = null; // TODO set actual method
	private List<UrlParam> params = new LinkedList<>();
	
	protected Link(String pattern) {
		this.pattern = pattern;
	}
	
	/*** module ***/
	
	public Link setModule(Module module) {
		getModule = (c)->module;
		return this;
	}
	
	public <M extends Module> Link setModule(Class<M> moduleClass) {
		getModule = (c)->Module.class.cast(moduleClass.newInstance());
		return this;
	}
	
	/*** controller ***/
	
	public Link setController(Class<?> controllerClass) {
		if (!controllerClass.isAnnotationPresent(Controller.class)) {
			throw new LogicException("Class " + controllerClass + " is not TOTI controller");
		}
		getController = ()->controllerClass;
		return this;
	}
	
	/*** method ***/
	
	public Link setMethod(String methodName) {
		getMethod = (clazz)->{
			try {
				Class<?> []classes = new Class[params.size()];
				for (int i = 0; i < params.size(); i++) {
					classes[i] = params.get(i).getClass(); // TODO can be null !!
				}
				Method method = clazz.getMethod(methodName, classes);
				if (!method.isAnnotationPresent(Action.class)) {
					throw new LogicException("Method " + method + " is not TOTI action");
				}
				return method;
			} catch (NoSuchMethodException e) {
				for (Method m : clazz.getMethods()) {
					if (m.isAnnotationPresent(Action.class) && m.getName().equals(methodName)) {
						return m;
					}
				}
				throw e;
			}
		};
		return this;
	}
	
	/*** universal ***/
	
	public Link addGetParam(String name, Object value) {
		params.add(new UrlParam(name, value));
		return this;
	}
	
	public Link addUrlParam(Object value) {
		params.add(new UrlParam(value));
		return this;
	}
	
	/******/
	
	private String getPath(Module module, Class<?> controller) {
		return controller.getName()
				.replace(".", "/")
				.replace(module.getControllersPath(), "")
				.replace(controller.getSimpleName(), "");
	}
	
	
	public String create() {
		if (getMethod == null) {
			throw new LogicException("Target method is missing");
		}
		try {
			Class<?> controller = getController.get();
			Module module = getModule.apply(controller);
			Method method = getMethod.apply(controller);
			return create(
				module.getName(),
				getPath(module, controller),
				controller.getAnnotation(Controller.class).value(), 
				method.getAnnotation(Action.class).value(),
				params
			);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link", e);
		}
	}
	
	public <T> String create(Class<T> controller, Function<T, Response> method) {
		if (!controller.isAnnotationPresent(Controller.class)) {
			throw new LogicException("Class " + controller + " is not TOTI controller");
		}
		try {
			Module module = getModule.apply(controller);
			
			ObjectBuilder<Method> builder = new ObjectBuilder<>();
			T result = new MockCreator().createMock(controller, builder);
	    	method.apply(result);
	    	Method m = builder.get();
			return create(
				module.getName(),
				getPath(module, controller),
				controller.getAnnotation(Controller.class).value(), 
				m.getAnnotation(Action.class).value(),
				params
			);
		} catch (Exception e) {
			throw new RuntimeException("Cannot create link", e);
		}
	}

	protected String create(String module, String path, String controller, String method, List<UrlParam> params) {
		return create(pattern, module, path, controller, method, params);
	}
	
	private String create(String pattern, String module, String path, String controller, String method, List<UrlParam> params) {
		// TODO lang
		pattern = parseUrl(pattern, MODULE, module);
		pattern = parseUrl(pattern, PATH, path);
		pattern = parseUrl(pattern, CONTROLLER, controller);
		pattern = parseUrl(pattern, METHOD, method);
		StringBuilder get = new StringBuilder("");
		for (UrlParam p : params) {
			Object value = p.getValue() == null ? "" : p.getValue();
			if (p.getName() == null) {
				pattern = parseUrl(pattern, PARAM, value.toString());
			} else {
				parseParams(get, p.getName(), value);
			}
		}
		pattern = pattern.replaceAll("<[^<>\\(]*\\[([^<>]*)\\][^<>]*>", "") //replace missing optional characters
				.replace("<", "")
				.replace(">", "");
		return pattern + (get.toString().length() > 1 ? "?" + get.toString() : "");
	}
	
	private void parseParams(StringBuilder get, String key, Object value) {
		if (value instanceof Map) {
			new DictionaryValue(value).getMap().forEach((k, v)->{
				parseParams(get, String.format("%s[%s]", key, k), v);
			});
		} else if (value instanceof Iterable) {
			new DictionaryValue(value).getList().forEach((v)->{
				parseParams(get, key + "[]", v);
			});
		} else {
			if (get.toString().length() > 0) {
				get.append("&");
			}
			get.append(key);
			get.append("=");
			get.append(value);
		}
	}
	
	private String parseUrl(String pattern, String key, String value) {
		value = normalize(value);
		if (value.length() == 0) {
			return pattern;
		}
		key = key.replace("[", "\\[").replace("]", "\\]");
		return pattern.replaceFirst(key, value);
	}
	
	private String normalize(String url) {
		if (url == null) {
			return "";
		}
		if (url.length() > 0 && url.charAt(0) == '/') {
			url = url.substring(1);
		}
		if (url.length() > 0 && url.charAt(url.length() - 1) == '/') {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}
	
}
